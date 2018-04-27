package uk.gov.cshr.atsadaptor.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListFilter;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.cshr.model.StatisticsKeyNames;
import uk.gov.cshr.atsadaptor.service.util.PathUtil;
import uk.gov.cshr.atsadaptor.service.cshr.VacancyService;
import uk.gov.cshr.status.CSHRServiceStatus;

@RestController
@Slf4j
public class VacanciesController implements VacanciesApi {
    private VacancyService vacancyService;
    private JobsListFilter jobsListFilter;
    private JobsListRetriever jobsListRetriever;

    @Value("${ats.jobrun.history.directory}")
    private String historyFileDirectory;
    @Value("${ats.jobrun.history.file}")
    private String historyFileName;
    @Value("${cshr.jobrun.audit.directory}")
    private String auditFileDirectory;
    @Value("${cshr.jobrun.audit.basefilename}")
    private String auditFileBaseName;

    public VacanciesController(VacancyService vacancyService, JobsListFilter jobsListFilter,
                               JobsListRetriever jobsListRetriever) {
        this.vacancyService = vacancyService;
        this.jobsListFilter = jobsListFilter;
        this.jobsListRetriever = jobsListRetriever;
    }

    @Override
    public ResponseEntity<CSHRServiceStatus> getVacancies() {
        log.info("STARTED: Processing jobs from Applicant Tracking System into the CSHR Vacancy Data Store");
        Map<String, Integer> statistics = createStatisticsMap();

        List<VacancyListData> liveJobs = jobsListRetriever.getLiveVacancies();
        if (!liveJobs.isEmpty()) {
            Path auditFilePath = createAuditFile();

            vacancyService.deleteNonActiveVacancies(liveJobs, auditFilePath, statistics);

            List<VacancyListData> changedJobs = jobsListFilter.filter(liveJobs);

            Integer numToProcess = statistics.get(StatisticsKeyNames.NUMBER_PROCESSED);
            statistics.put(StatisticsKeyNames.NUMBER_PROCESSED, numToProcess + changedJobs.size());

            vacancyService.processChangedVacancies(changedJobs, auditFilePath, statistics);

            createAuditEntry(auditFilePath, statistics);

            updateLastJobHistoryFile(changedJobs);
        }

        log.info("COMPLETED: Processing jobs from Applicant Tracking System into the CSHR Vacancy Data Store");

        return ResponseEntity.ok(ResponseBuilder.buildServiceStatus(statistics));
    }

    private void updateLastJobHistoryFile(List<VacancyListData> changedJobs) {
        if (changedJobs != null && !changedJobs.isEmpty()) {
            VacancyListData lastJob = changedJobs.get(changedJobs.size() - 1);
            Path path = FileSystems.getDefault().getPath(historyFileDirectory, historyFileName);

            PathUtil.createFile(path);

            try {
                LocalDateTime lastTimestamp = lastJob.getVacancyTimestamp().toLocalDateTime();
                String timestamp = lastTimestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                FileUtils.write(path.toFile(), timestamp, Charset.forName("UTF-8"), true);
            } catch (IOException e) {
                log.error("Error writing last processed timestamp to " + path.getFileName(), e);
            }
        }
    }

    private Map<String, Integer> createStatisticsMap() {
        Map<String, Integer> statistics = new HashMap<>();

        statistics.put(StatisticsKeyNames.NUMBER_PROCESSED, 0);
        statistics.put(StatisticsKeyNames.NUMBER_CREATED, 0);
        statistics.put(StatisticsKeyNames.NUMBER_SAVED, 0);
        statistics.put(StatisticsKeyNames.NUMBER_DELETED, 0);
        statistics.put(StatisticsKeyNames.NUMBER_OF_ERRORS, 0);

        return statistics;
    }

    private Path createAuditFile() {
        String fileName = auditFileBaseName
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + ".log";

        Path path = FileSystems.getDefault().getPath(auditFileDirectory, fileName);


        PathUtil.createFile(path);

        return path;
    }

    private void createAuditEntry(Path path, Map<String, Integer> statistics) {
        Integer numProcessed = statistics.get(StatisticsKeyNames.NUMBER_PROCESSED);
        Integer numSaved = statistics.get(StatisticsKeyNames.NUMBER_SAVED);
        Integer numDeleted = statistics.get(StatisticsKeyNames.NUMBER_DELETED);

        if (numSaved.compareTo(0) > 0) {
            numSaved = numSaved - numDeleted;
            statistics.put(StatisticsKeyNames.NUMBER_SAVED, numSaved - numDeleted);
        }

        statistics.put(StatisticsKeyNames.NUMBER_PROCESSED, numProcessed + numDeleted);

        String entry = ResponseBuilder.buildLogEntry(statistics);
        try {
            FileUtils.write(path.toFile(), entry, Charset.forName("UTF-8"), true);
        } catch (IOException e) {
            log.error("Error writing auditFileEntry for the results of processing those vacancies that have changed. The entry was " + entry, e);
        }
    }
}
