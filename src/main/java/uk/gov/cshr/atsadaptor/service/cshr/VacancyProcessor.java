package uk.gov.cshr.atsadaptor.service.cshr;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.JobRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.model.JobRequestResponseWrapper;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.cshr.model.vacancy.AtsToCshrDataMapper;
import uk.gov.cshr.atsadaptor.service.util.PathUtil;
import uk.gov.cshr.exception.CSHRServiceException;
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

    private RestTemplate cshrRestTemplate;
    @Inject
    private AtsToCshrDataMapper dataMapper;
    @Inject
    private JobRetriever jobRetriever;
    @Inject
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${cshr.api.service.vacancy.save.username}")
    private String chsrApiUsername;
    @Value("${cshr.api.service.vacancy.save.password}")
    private String cshrApiPassword;
    @Value("${cshr.api.service.vacancy.save.endpoint}")
    private String chsrSaveVacancyEndpoint;

    @PostConstruct
    public void init() {
        log.info("User Name for cshrApi:" + chsrApiUsername);
        log.info(("Password for cshrApi" + cshrApiPassword));
        cshrRestTemplate = restTemplateBuilder.basicAuthorization(chsrApiUsername, cshrApiPassword).build();
    }

    /**
     * This method is responsible for processing a collection of vacancies, retrieving all the data
     * and mapping onto the CSHR data model for submission to the data store.
     *
     * @param jobs jobs to be processed
     */
    public void process(List<VacancyListData> jobs, Path auditFile, Map<String, Integer> statistics) {
        log.info("Starting to process a batch of jobs");

        JobRequestResponseWrapper jobsData = jobRetriever.retrieveJob(jobs);

        jobsData.getVacancyResponse().getResponseData().getVacancy()
                .forEach(v -> processVacancy(v, auditFile, statistics));
    }

    private void processVacancy(Map<String, Object> sourceVacancyData, Path auditFile, Map<String, Integer> statistics) {
        Map<String, Object> fields = (Map<String, Object>) sourceVacancyData.get("field");
        String jobRef = (String) ((Map<String, Object>) fields.get("job_reference")).get("value");

        log.info("Processing a vacancy with job Reference = " + jobRef);

        String auditFileEntry;

        Integer statistic;
        try {
            Map<String, Object> mappedVacancy = dataMapper.map(sourceVacancyData);
            ResponseEntity<CSHRServiceStatus> response = cshrRestTemplate.postForEntity(chsrSaveVacancyEndpoint,
                    mappedVacancy, CSHRServiceStatus.class);

            if (StatusCode.RECORD_CREATED.equals(response.getBody().getCode())) {
                statistic = statistics.get("numberCreated");
                statistics.put("numberCreated", statistic + 1);
            } else {
                statistic = statistics.get("numberSaved");
                statistics.put("numberSaved", statistic + 1);
            }

            auditFileEntry = createAuditFileEntry(jobRef, response);
        } catch (Exception ex) {
            System.out.println("******Saurabh Final Check");
            ex.printStackTrace();
            log.info(ex.getMessage());
            statistic = statistics.get("numberOfErrors");
            statistics.put("numberOfErrors", statistic + 1);
            auditFileEntry = createExceptionFileEntry(jobRef, ex);
        }

        try {
            FileUtils.write(auditFile.toFile(), auditFileEntry, Charset.forName("UTF-8"), true);
        } catch (IOException e) {
            log.error("Error writing auditFileEntry for jobRef " + jobRef + " to a file called "
                    + auditFile.getFileName() + ". The content of the entry was: " + auditFileEntry, e);
        }
    }

    private String createAuditFileEntry(String jobRef, ResponseEntity<CSHRServiceStatus> response) {
        StringBuilder output = new StringBuilder();

        output.append("Vacancy with ")
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

    private String createExceptionFileEntry(String jobRef, Exception ex) {
        return "Vacancy with "
                + jobRef
                + " has been processed and encountered an exception: "
                + ex.getMessage()
                + System.lineSeparator()
                + System.lineSeparator();
    }
}
