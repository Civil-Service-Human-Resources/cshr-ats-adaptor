package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.service.ats.ServiceResponseStatus;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequest;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequestWrapper;

@Component
public class AtsJobsListRetriever implements JobsListRetriever {
    private String atsListUrl;
    private String authenticationToken;
    private String clientId;
    private RestTemplate restTemplate;

    /**
     * Default constructor for autowiring.
     *
     * @param restTemplateBuilder instance of RestTemplateBuilder to be autowired
     * @param atsListUrl          value of url to external applicant tracking system (ats) system
     */
    public AtsJobsListRetriever(RestTemplateBuilder restTemplateBuilder,
                                @Value("${ats.list.request.url}") String atsListUrl,
                                @Value("${ats.authentication.token}") String authenticationToken,
                                @Value("${ats.client.id}") String clientId) {
        this.atsListUrl = atsListUrl;
        this.authenticationToken = authenticationToken;
        this.clientId = clientId;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<VacancyListData> getLiveVacancies() {
        VacancyRequest rv = VacancyRequest.builder().requestType("listRequest").requestClientId(clientId).requestAuthToken(authenticationToken).build();
        VacancyRequestWrapper request = VacancyRequestWrapper.builder().vacancyRequest(rv).build();
        ResponseEntity<VacancyListResponseWrapper> response = restTemplate.postForEntity(atsListUrl, request, VacancyListResponseWrapper.class);

        ServiceResponseStatus.findByCode(response.getBody().getVacancyResponse().getStatusCode()).checkForError();

        return response.getBody().getVacancyResponse().getResponseData().getVacancyList();
    }
}
