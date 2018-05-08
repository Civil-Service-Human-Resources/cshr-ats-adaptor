package uk.gov.cshr.atsadaptor.service.cshr;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.atsadaptor.service.ats.ServiceResponseStatus;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.JobRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.model.JobRequestResponseWrapper;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.cshr.model.StatisticsKeyNames;
import uk.gov.cshr.atsadaptor.service.cshr.model.vacancy.AtsToCshrDataMapper;
import uk.gov.cshr.atsadaptor.service.util.PathUtil;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class is responsible for coordinating the work involved in retrieving the full details of
 * jobs from the Applicant Tracking System, mapping them to the CSHR model and posting them to the
 * CSHR API.
 */
@Component
@Slf4j
public class VacancyProcessor {
    private static final String HYPHEN = " - ";
    private static final String VACANCY_WITH_REFERENCE = "Vacancy with reference";

    private RestTemplate cshrRestTemplate;
    private RestTemplate cshrSearchRestTemplate;
    private AtsToCshrDataMapper dataMapper;
    private JobRetriever jobRetriever;
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${cshr.api.service.vacancy.save.username}")
    private String chsrApiUsername;
    @Value("${cshr.api.service.vacancy.save.password}")
    private String cshrApiPassword;
    @Value("${cshr.api.service.vacancy.save.endpoint}")
    private String chsrSaveVacancyEndpoint;
    @Value("${cshr.api.service.search.username}")
    private String cshrApiSearchUsername;
    @Value("${cshr.api.service.search.password}")
    private String cshrApiSearchPassword;
    @Value("${ats.jobrun.history.directory}")
    private String historyFileDirectory;
    @Value("${ats.jobrun.history.file}")
    private String historyFileName;
    @Value("${cshr.api.service.vacancy.load.endpoint}")
    private String loadVacancyEndpoint;

    public VacancyProcessor(AtsToCshrDataMapper dataMapper, JobRetriever jobRetriever,
                            RestTemplateBuilder restTemplateBuilder) {
        this.dataMapper = dataMapper;
        this.jobRetriever = jobRetriever;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @PostConstruct
    public void init() {
        cshrRestTemplate = restTemplateBuilder.basicAuthorization(chsrApiUsername, cshrApiPassword).build();
        cshrSearchRestTemplate = restTemplateBuilder.basicAuthorization(cshrApiSearchUsername, cshrApiSearchPassword).build();
    }

    /**
     * This method is responsible for processing a collection of vacancies, retrieving all the data
     * and mapping onto the CSHR data model for submission to the data store.
     *
     * @param jobs jobs to be processed
     * @param jobsNoLongerActive list of jobs that are in changedVacancies list but are no longer active by the time they are processed.
     */
    public void process(List<VacancyListData> jobs, List<String> jobsNoLongerActive, Path auditFile,
                        Map<String, Integer> statistics) {
        log.info("Starting to process a batch of jobs");
        Path historyFile = FileSystems.getDefault().getPath(historyFileDirectory, historyFileName);

        JobRequestResponseWrapper jobsData = jobRetriever.retrieveJobs(jobs);

        jobsData.getVacancyResponse().getResponseData().getVacancy()
                .forEach(v -> processVacancy(v, jobsNoLongerActive, auditFile, historyFile, jobs, statistics));
    }

    private void processVacancy(Map<String, Object> sourceVacancyData, List<String> jobsNoLongerActive, Path auditFile,
                                Path historyFile, List<VacancyListData> jobs, Map<String, Integer> statistics) {
        String auditFileEntry;
        String jobRef = null;

        try {
            Map<String, Object> fields = (Map<String, Object>) sourceVacancyData.get("field");
            jobRef = (String) ((Map<String, Object>) fields.get("job_reference")).get("value");
            log.info("Processing a vacancy with job Reference = " + jobRef);

            checkResponseStatus(sourceVacancyData);

            Map<String, Object> mappedVacancy = dataMapper.map(sourceVacancyData, true);
            ResponseEntity<CSHRServiceStatus> response = cshrRestTemplate.postForEntity(chsrSaveVacancyEndpoint,
                    mappedVacancy, CSHRServiceStatus.class);

            if (StatusCode.RECORD_CREATED.getCode().equals(response.getBody().getCode())) {
                incrementStatistic(statistics, StatisticsKeyNames.NUMBER_CREATED);
            } else {
                incrementStatistic(statistics, StatisticsKeyNames.NUMBER_SAVED);
            }

            auditFileEntry = createAuditFileEntry(jobRef, response);
            updateJobHistoryFile(jobRef, historyFile, jobs);
        } catch (ExternalApplicantTrackingSystemException ex) {
            if (ServiceResponseStatus.JOB_NOT_LIVE.getErrorMessage().equals(ex.getCshrServiceStatus().getSummary())) {
                jobsNoLongerActive.add(jobRef);
            }
            incrementStatistic(statistics, StatisticsKeyNames.NUMBER_OF_ERRORS);
            auditFileEntry = createExceptionFileStatusEntry(jobRef, ex.getCshrServiceStatus());
        } catch (Exception ex) {
            incrementStatistic(statistics, StatisticsKeyNames.NUMBER_OF_ERRORS);
            auditFileEntry = createExceptionFileEntry(jobRef, ex);
        }

        writeAuditFileEntry(auditFile, jobRef, auditFileEntry);
    }

    private void checkResponseStatus(Map<String, Object> sourceVacancyData) {
        String code = String.valueOf(((Double) sourceVacancyData.get("vacancyResponseStatus")).intValue());

        ServiceResponseStatus.checkForError(code);
    }

    private void incrementStatistic(Map<String, Integer> statistics, String statisticKey) {
        Integer statistic = statistics.get(statisticKey);
        statistics.put(statisticKey, statistic + 1);
    }

    private void writeAuditFileEntry(Path auditFile, String jobRef, String auditFileEntry) {
        try {
            FileUtils.write(auditFile.toFile(), auditFileEntry, Charset.forName("UTF-8"), true);
        } catch (IOException e) {
            log.error("Error writing auditFileEntry for jobRef " + jobRef + " to a file called "
                    + auditFile.getFileName() + ". The content of the entry was: " + auditFileEntry, e);
        }
    }

    private String createAuditFileEntry(String jobRef, ResponseEntity<CSHRServiceStatus> response) {
        StringBuilder output = new StringBuilder();

        output.append(VACANCY_WITH_REFERENCE)
                .append(jobRef)
                .append(" has been processed: An HTTPStatus with a code of ")
                .append(response.getStatusCodeValue());

        if (response.getStatusCode().getReasonPhrase() != null) {
            output.append(" and a reason of ")
                    .append(response.getStatusCode().getReasonPhrase())
                    .append(". ");
        }

        output.append("A CSHR Service Status code and summary are ")
                .append(response.getBody().getCode())
                .append(HYPHEN)
                .append(response.getBody().getSummary());

        if (response.getBody().getDetail() != null && !response.getBody().getDetail().isEmpty()) {
            output.append(HYPHEN);

            response.getBody().getDetail().forEach(output :: append);
        }

        output.append(System.lineSeparator()).append(System.lineSeparator());

        return output.toString();
    }

    private String createExceptionFileStatusEntry(String jobRef, CSHRServiceStatus status) {
        return createExceptionEntry(jobRef, status.getSummary());
    }

    private String createExceptionEntry(String jobRef, String summary) {
        StringBuilder builder = new StringBuilder();
        builder.append(VACANCY_WITH_REFERENCE);

        if (jobRef != null) {
            builder.append(" ").append(jobRef);
        } else {
            builder.append(" unknown ");
        }

        return builder.append(summary).append(System.lineSeparator()).append(System.lineSeparator()).toString();
    }

    private String createExceptionFileEntry(String jobRef, Exception ex) {
        return createExceptionEntry(jobRef, ex.getMessage());
    }

    public void deleteVacancies(List<String> vacancies, Path auditFile, Map<String, Integer> statistics) {
        log.info("Starting to delete vacancies");

        vacancies.forEach(v -> markCshrVacancyDeleted(v, auditFile, statistics));
    }

    private void markCshrVacancyDeleted(String id, Path auditFile, Map<String, Integer> statistics) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        log.info("Attempting to delete a vacancy with id " + id);

        String auditFileEntry;
        try {
            ResponseEntity<Map> response =
                    cshrSearchRestTemplate.exchange(loadVacancyEndpoint, HttpMethod.GET, buildRequest(), Map.class, params);
            Map<String, Object> vacancy = response.getBody();

            if (HttpStatus.OK.equals(response.getStatusCode())) {
                vacancy.put("active", false);

                ResponseEntity<CSHRServiceStatus> saveResponse = cshrRestTemplate.postForEntity(chsrSaveVacancyEndpoint,
                        vacancy, CSHRServiceStatus.class);

                incrementStatistic(statistics, StatisticsKeyNames.NUMBER_DELETED);
                String jobRef = String.valueOf(((Double) vacancy.get("identifier")).intValue());
                auditFileEntry = createAuditFileEntry(jobRef, saveResponse);
            } else {
                incrementStatistic(statistics, StatisticsKeyNames.NUMBER_OF_ERRORS);
                auditFileEntry = createLoadVacancyErrorEntry(id, response);
            }
        } catch (Exception ex) {
            incrementStatistic(statistics, StatisticsKeyNames.NUMBER_OF_ERRORS);
            auditFileEntry = createExceptionFileEntry(id, ex);
        }

        writeAuditFileEntry(auditFile, id, auditFileEntry);
    }

    private HttpEntity<?> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity<>(headers);
    }

    private String createLoadVacancyErrorEntry(String id, ResponseEntity<Map> response) {
        StringBuilder output = new StringBuilder();

        output.append("An attempt to mark a CSHR vacancy with an id of ")
                .append(id)
                .append(" failed because the vacancy could not be loaded from the CSHR Data store: An HTTPStatus with a code of ")
                .append(response.getStatusCodeValue());

        if (response.getStatusCode().getReasonPhrase() != null) {
            output.append(" and a reason of ")
                    .append(response.getStatusCode().getReasonPhrase())
                    .append(". ");
        }

        output.append(System.lineSeparator()).append(System.lineSeparator());

        return output.toString();
    }

    // Only when a job is successfully processed is it's timestamp used in the history file.
    private void updateJobHistoryFile(String jobRef, Path historyFile, List<VacancyListData> jobs) {
        Timestamp timestamp = jobs
                .stream()
                .filter(j -> j.getJcode().equals(jobRef))
                .map(VacancyListData :: getVacancyTimestamp)
                .findFirst()
                .orElse(null);

        if (timestamp != null) {
            PathUtil.createFileIfRequired(historyFile);

            try {
                LocalDateTime lastTimestamp = timestamp.toLocalDateTime();
                String result = lastTimestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                FileUtils.write(historyFile.toFile(), result, Charset.forName("UTF-8"), false);
            } catch (IOException e) {
                log.error("Error writing last processed timestamp to " + historyFile.getFileName(), e);
            }
        }
    }
}
