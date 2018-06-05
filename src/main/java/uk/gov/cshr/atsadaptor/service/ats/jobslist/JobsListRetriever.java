package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import java.nio.file.Path;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.atsadaptor.service.ats.ServiceResponseStatus;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListResponseWrapper;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequest;
import uk.gov.cshr.atsadaptor.service.ats.request.VacancyRequestWrapper;
import uk.gov.cshr.atsadaptor.service.cshr.AuditFileProcessor;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class is responsible for retrieving a list of live vacancies from a Applicant Tracking
 * System (ATS)
 */
@Component
@Slf4j
public class JobsListRetriever {
    private static final String NA = "N/a";

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
        ResponseEntity<VacancyListResponseWrapper> response;

        try {
            response = restTemplate.postForEntity(atsRequestEndpoint, request, VacancyListResponseWrapper.class);
        } catch (HttpClientErrorException hcee) {
            auditFileProcessor.addExceptionEntry(auditFilePath, NA, hcee);

            throw new ExternalApplicantTrackingSystemException (
                    CSHRServiceStatus.builder()
                            .code(StatusCode.THIRD_PARTY_SERVICE_ERROR.getCode())
                            .summary("An error occurred trying to use the ats endpoint - " + atsRequestEndpoint + " - and the status http status code was " + hcee.getStatusText())
                            .build());
        }

        try {
            ServiceResponseStatus.checkForError(response.getBody().getVacancyResponse().getStatusCode());
        } catch (ExternalApplicantTrackingSystemException ex) {
            auditFileProcessor.writeAuditFileEntry(auditFilePath, NA, ex.getCshrServiceStatus().getSummary());

            throw ex;
        } catch (Exception ex) {
            log.error("An unexpected error occurred trying to retrieve live vacancies from the external ats system.", ex);
            throw new ExternalApplicantTrackingSystemException (
                    CSHRServiceStatus.builder()
                            .code(StatusCode.THIRD_PARTY_SERVICE_ERROR.getCode())
                            .summary("An unexpected error occurred trying to retrieve live vacancies from the external ats system.")
                            .build());
        }

        log.debug("Response from ATS was successful");

        return response.getBody().getVacancyResponse().getResponseData().getVacancyList();
    }
}
