package uk.gov.cshr.atsadaptor.service.ats;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import com.google.common.io.ByteStreams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.AtsJobsListRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.VacancyListData;
import uk.gov.cshr.atsadaptor.util.VacancyListDataBuilder;

/**
 * Tests {@link AtsJobsListRetriever}
 */
@RunWith(SpringRunner.class)
@RestClientTest(AtsJobsListRetriever.class)
public class AtsJobsListRetrieverTest {
    private static final String EXTERNAL_URL = "/theurl";

    @Inject
    private AtsJobsListRetriever processor;
    @Inject
    private MockRestServiceServer server;

    @Test(expected = NullPointerException.class)
    public void testGetLiveVacancies_noCodeReturned() throws Exception {
        server.expect(requestTo(EXTERNAL_URL))
                .andRespond(withSuccess(getExpectedResponse("unknownStatusCodeResponse.json"),
                        MediaType.APPLICATION_JSON));

        processor.getLiveVacancies();
    }

    @Test(expected = ExternalApplicantTrackingSystemException.class)
    public void testGetLiveVacancies_invaldRequest() throws Exception {
        doExternalErrorTest("request/invalidTokenResponse.json");
    }

    private void doExternalErrorTest(String expectedResponseFileName) throws Exception {
        server.expect(requestTo(EXTERNAL_URL))
                .andRespond(withSuccess(getExpectedResponse(expectedResponseFileName),
                        MediaType.APPLICATION_JSON));

        processor.getLiveVacancies();

    }

    @Test(expected = ExternalApplicantTrackingSystemException.class)
    public void testGetLiveVacancies_invalidAuthToken() throws Exception {
        doExternalErrorTest("request/invalidRequestResponse.json");
    }

    @Test(expected = ExternalApplicantTrackingSystemException.class)
    public void testGetLiveVacancies_serviceBusy() throws Exception {
        doExternalErrorTest("request/serviceBusyResponse.json");
    }

    @Test(expected = ExternalApplicantTrackingSystemException.class)
    public void testGetLiveVacancies_serviceError() throws Exception {
        doExternalErrorTest("request/serviceErrorResponse.json");
    }

    @Test
    public void testGetLiveVacancies_nothingExists() throws Exception {
        this.server.expect(requestTo(EXTERNAL_URL))
                .andRespond(withSuccess(getExpectedResponse("listRequestResponses/emptyResponse.json"),
                        MediaType.APPLICATION_JSON));

        assertThat(processor.getLiveVacancies(), is(empty()));
    }

    @Test
    public void testGetLiveVacancies_aVacancyFound() throws Exception {
        this.server.expect(requestTo(EXTERNAL_URL))
                .andRespond(withSuccess(getExpectedResponse("listRequestResponses/fourVacanciesList.json"),
                        MediaType.APPLICATION_JSON));

        List<VacancyListData> vacancies = processor.getLiveVacancies();

        assertThat(vacancies, is(equalTo(VacancyListDataBuilder.getInstance().buildExepctedVacancyListData())));
    }

    private String getExpectedResponse(String expectedResponseFileName) throws Exception {
        byte[] content;

        try (InputStream inputStream = AtsJobsListRetrieverTest.class.getResourceAsStream("/" + expectedResponseFileName)) {
            content = ByteStreams.toByteArray(inputStream);
        }

        return new String(content);
    }
}
