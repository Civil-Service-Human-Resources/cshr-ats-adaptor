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
import uk.gov.cshr.atsadaptor.service.cshr.VacancyService;
import uk.gov.cshr.atsadaptor.service.cshr.model.ProcessStatistics;
import uk.gov.cshr.status.CSHRServiceStatus;

@RestController
@Slf4j
public class VacanciesController implements VacanciesApi {
    private AuditFileProcessor auditFileProcessor;
    private JobsListFilter jobsListFilter;
    private JobsListRetriever jobsListRetriever;
    private VacancyService vacancyService;

    @Value("${ats.jobrun.history.directory}")
    private String historyFileDirectory;
    @Value("${ats.jobrun.history.file}")

    private String historyFileName;

    public VacanciesController(VacancyService vacancyService, JobsListFilter jobsListFilter,
                               JobsListRetriever jobsListRetriever, AuditFileProcessor auditFileProcessor) {
        this.vacancyService = vacancyService;
        this.jobsListFilter = jobsListFilter;
        this.jobsListRetriever = jobsListRetriever;
        this.auditFileProcessor = auditFileProcessor;
    }

    @Override
    public ResponseEntity<CSHRServiceStatus> getVacancies() {
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

    @Scheduled(cron = "${cshr.jobrun.cron.schedule}")
    public void loadVacancies() {
        getVacancies();
    }
}
