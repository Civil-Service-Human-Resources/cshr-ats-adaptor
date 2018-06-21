package uk.gov.cshr.atsadaptor.controller;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListFilter;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.cshr.AuditFileProcessor;
import uk.gov.cshr.atsadaptor.service.cshr.SlackNotificationService;
import uk.gov.cshr.atsadaptor.service.cshr.VacancyService;
import uk.gov.cshr.atsadaptor.service.cshr.model.ProcessStatistics;
import uk.gov.cshr.exception.CSHRServiceException;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

@RestController
@Slf4j
public class VacanciesController implements VacanciesApi {
    private AuditFileProcessor auditFileProcessor;
    private JobsListFilter jobsListFilter;
    private JobsListRetriever jobsListRetriever;
    private SlackNotificationService slackNotificationService;
    private VacancyService vacancyService;

    @Value("${ats.jobrun.history.directory}")
    private String historyFileDirectory;
    @Value("${ats.jobrun.history.file}")
    private String historyFileName;

    public VacanciesController(VacancyService vacancyService, JobsListFilter jobsListFilter,
                               JobsListRetriever jobsListRetriever, AuditFileProcessor auditFileProcessor,
                               SlackNotificationService slackNotificationService) {
        this.vacancyService = vacancyService;
        this.jobsListFilter = jobsListFilter;
        this.jobsListRetriever = jobsListRetriever;
        this.auditFileProcessor = auditFileProcessor;
        this.slackNotificationService = slackNotificationService;
    }

    @Override
    public ResponseEntity<CSHRServiceStatus> loadVacancies() {
        log.info("STARTED: Processing jobs from Applicant Tracking System into the CSHR Vacancy Data Store");
        ProcessStatistics processStatistics = new ProcessStatistics(0, 0, 0, 0, 0, null, System.nanoTime());

        Path auditFilePath = auditFileProcessor.createInitialAuditEntry();

        List<VacancyListData> liveJobs = jobsListRetriever.getLiveVacancies(auditFilePath);

        if (!liveJobs.isEmpty()) {
            List<VacancyListData> changedJobs = jobsListFilter.filter(liveJobs);

            Integer numToProcess = processStatistics.getNumProcessed();
            processStatistics.setNumProcessed(numToProcess + changedJobs.size());

            List<String> jobsNoLongerActive = new ArrayList<>();
            vacancyService.processChangedVacancies(changedJobs, jobsNoLongerActive, auditFilePath, processStatistics);

            vacancyService.deleteNonActiveVacancies(liveJobs, jobsNoLongerActive, auditFilePath, processStatistics);
        }

        auditFileProcessor.addFinalAuditEntry(auditFilePath, processStatistics);

        log.info("COMPLETED: Processing jobs from Applicant Tracking System into the CSHR Vacancy Data Store");

        return ResponseEntity.ok(ResponseBuilder.buildServiceStatus(processStatistics));
    }

    @Scheduled(fixedDelayString = "${cshr.jobrun.fixed.delay}")
    public void runLoadVacancies() {
        CSHRServiceStatus serviceStatus;

        try {
            serviceStatus = loadVacancies().getBody();
        } catch (CSHRServiceException ex ) {
            List<String> details = new ArrayList<>();
            details.add(ex.getCshrServiceStatus().getSummary());

            if (ex.getCshrServiceStatus().getDetail() != null
                    && !ex.getCshrServiceStatus().getDetail().isEmpty()) {
                details.addAll(ex.getCshrServiceStatus().getDetail());
            }

            serviceStatus = buildCshrServiceStatus(details);

        } catch (Exception re) {
            log.error("An error occurred trying to run the load vacancies job.", re);

            List<String> details = new ArrayList<>();
            details.add(re.getMessage());

            serviceStatus = buildCshrServiceStatus(details);
        }

        slackNotificationService.postResponseToSlack(serviceStatus);
    }

    private CSHRServiceStatus buildCshrServiceStatus(List<String> details) {
        return CSHRServiceStatus.builder()
                .code(StatusCode.INTERNAL_SERVICE_ERROR.getCode())
                .summary("An unexpected error has occurred trying to run the ATS Vacancy Data Load process")
                .detail(details)
                .build();
    }
}
