package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.service.ats.ServiceResponseStatus;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListResponseWrapper;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequest;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequestWrapper;

/**
 * This class is responsible for retrieving a list of live vacancies from a Applicant Tracking System (ATS)
 */
@Component
@Slf4j
public class JobsListRetriever {
    private String atsListUrl;
    private String authenticationToken;
    private String clientId;
    private RestTemplate restTemplate;

    public JobsListRetriever(RestTemplateBuilder restTemplateBuilder,
                             @Value("${ats.list.request.url}") String atsListUrl,
                             @Value("${ats.authentication.token}") String authenticationToken,
                             @Value("${ats.client.id}") String clientId) {
        this.atsListUrl = atsListUrl;
        this.authenticationToken = authenticationToken;
        this.clientId = clientId;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * This method is responsible for getting a list of live vacancies from an external ATS.
     * <p>
     * The list will be used to retrieve the details of each vacancy in the in the list and also to check for any closed vacancies in the CSHR Vacancy data store.
     *
     * @return List<VacancyListData> list of live vacancies
     */
    public List<VacancyListData> getLiveVacancies() {
        log.debug("Obtaining the list of live vacancies from the ATS");

        VacancyRequest rv = VacancyRequest.builder().requestType("listRequest").requestClientId(clientId).requestAuthToken(authenticationToken).build();
        VacancyRequestWrapper request = VacancyRequestWrapper.builder().vacancyRequest(rv).build();
        ResponseEntity<VacancyListResponseWrapper> response = restTemplate.postForEntity(atsListUrl, request, VacancyListResponseWrapper.class);

        ServiceResponseStatus.findByCode(response.getBody().getVacancyResponse().getStatusCode()).checkForError();

        return response.getBody().getVacancyResponse().getResponseData().getVacancyList();
    }
}
