package uk.gov.cshr.atsadaptor.service.ats.jobrequest;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.service.ats.ServiceResponseStatus;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.model.JobRequestResponseWrapper;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequest;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequestWrapper;

/**
 * This class is reponsible for retrieving the full details of a collection of Jobs from the
 * Applicant Tracking System using its API.
 */
@Component
@Slf4j
public class JobRetriever {
    private String atsRequestEndpoint;
    private String authenticationToken;
    private String clientId;
    private RestTemplate restTemplate;

    public JobRetriever(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${ats.request.endpoint}") String atsRequestEndpoint,
            @Value("${ats.authentication.token}") String authenticationToken,
            @Value("${ats.client.id}") String clientId) {
        this.atsRequestEndpoint = atsRequestEndpoint;
        this.authenticationToken = authenticationToken;
        this.clientId = clientId;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * This method retrieves the full details of the given jobs from the Applicant Tracking System
     * using its API.
     *
     * @param jobs list of jobs to get full details.
     * @return the response containing all the data for the given jobs plus additional information
     * about the response.
     */
    public JobRequestResponseWrapper retrieveJob(List<VacancyListData> jobs) {
        log.info("Retrieving full details of jobs from ATS");

        String[] jcodes = jobs.stream().map(VacancyListData::getJcode).toArray(String[]::new);

        VacancyRequest rv =
                VacancyRequest.builder()
                        .requestType("jobRequest")
                        .requestClientId(clientId)
                        .requestAuthToken(authenticationToken)
                        .jcode(jcodes)
                        .build();

        VacancyRequestWrapper request = VacancyRequestWrapper.builder().vacancyRequest(rv).build();
        ResponseEntity<JobRequestResponseWrapper> vacancyData =
                restTemplate.postForEntity(atsRequestEndpoint, request, JobRequestResponseWrapper.class);

        ServiceResponseStatus.checkForError(vacancyData.getBody().getVacancyResponse().getStatusCode());

        log.info("Response from ATS for jobs request was successful");

        return vacancyData.getBody();
    }
}
