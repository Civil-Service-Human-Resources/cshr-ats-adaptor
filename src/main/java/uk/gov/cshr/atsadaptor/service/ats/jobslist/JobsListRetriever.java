package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import java.nio.file.Path;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.atsadaptor.service.ats.ServiceResponseStatus;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListResponseWrapper;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequest;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequestWrapper;
import uk.gov.cshr.atsadaptor.service.cshr.AuditFileProcessor;

/**
 * This class is responsible for retrieving a list of live vacancies from a Applicant Tracking
 * System (ATS)
 */
@Component
@Slf4j
public class JobsListRetriever {
    private AuditFileProcessor auditFileProcessor;
    private RestTemplate restTemplate;

    @Value("${ats.request.endpoint}")
    private String atsRequestEndpoint;
    @Value("${ats.authentication.token}")
    private String authenticationToken;
    @Value("${ats.client.id}")
    private String clientId;

    public JobsListRetriever(AuditFileProcessor auditFileProcessor,
                             RestTemplateBuilder restTemplateBuilder) {
        this.auditFileProcessor = auditFileProcessor;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * This method is responsible for getting a list of live vacancies from an external ATS.
     * <p>
     * <p>The list will be used to retrieve the details of each vacancy in the in the list and also to
     * check for any closed vacancies in the CSHR Vacancy data store.
     *
     * @return List<VacancyListData> list of live vacancies
     */
    public List<VacancyListData> getLiveVacancies(Path auditFilePath) {
        log.info("Retrieving the list of live vacancies from the ATS");

        VacancyRequest rv =
                VacancyRequest.builder()
                        .requestType("listRequest")
                        .requestClientId(clientId)
                        .requestAuthToken(authenticationToken)
                        .build();

        VacancyRequestWrapper request = VacancyRequestWrapper.builder().vacancyRequest(rv).build();
        ResponseEntity<VacancyListResponseWrapper> response = restTemplate.postForEntity(atsRequestEndpoint,
                request, VacancyListResponseWrapper.class);

        try {
            ServiceResponseStatus.checkForError(response.getBody().getVacancyResponse().getStatusCode());
        } catch (ExternalApplicantTrackingSystemException ex) {
            log.error("EXCEPTION");
            log.error(ex);
            auditFileProcessor.writeAuditFileEntry(auditFilePath, "N/a", ex.getCshrServiceStatus().getSummary());

            throw ex;
        }

        log.debug("Response from ATS was successful");

        return response.getBody().getVacancyResponse().getResponseData().getVacancyList();
    }
}
