package uk.gov.cshr.atsadaptor.service.ats;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.fail;

import org.junit.Test;
import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.status.StatusCode;

/**
 * Tests {@link ServiceResponseStatus}
 */
public class ServiceResponseStatusTest {
    @Test
    public void checkForError_noneFound() {
        try {
            ServiceResponseStatus.checkForError("1");
        } catch (Exception e) {
            fail("No exception should have been thrown");
        }
    }

    @Test
    public void checkForError_invalidRequest() {
        doExceptionTest("2", "The external service reported that an invalid request was received");
    }

    private void doExceptionTest(String code, String expectedMessage) {
        try {
            ServiceResponseStatus.checkForError(code);
        } catch (Exception e) {
            assertThat(e, instanceOf(ExternalApplicantTrackingSystemException.class));

            ExternalApplicantTrackingSystemException ex = (ExternalApplicantTrackingSystemException) e;
            assertThat(ex.getCshrServiceStatus().getCode(), is(equalTo(StatusCode.THIRD_PARTY_SERVICE_ERROR.getCode())));
            assertThat(ex.getCshrServiceStatus().getSummary(), is(equalTo(expectedMessage)));
        }
    }

    @Test
    public void checkForError_invalidAuthToken() {
        doExceptionTest("3", "The an external service reported that in invalid authorisation token was used");
    }

    @Test
    public void checkForError_externalServiceBusy() {
        doExceptionTest("4", "The external service reported that it was busy");
    }

    @Test
    public void checkForError_externalServiceError() {
        doExceptionTest("5", "The an external service reported that it encountered an internal error");
    }

    @Test
    public void checkForError_notInLiveVacancySet() {
        doExceptionTest("6", "The external service reported that the job being processed is no longer live");
    }

    @Test
    public void checkForError_beyoneMaxBatchSize() {
        doExceptionTest("7", "The an external service reported that the job being processed is part " +
                "of a request that has exceeded the maximum batch size of job details that can be requested from the " +
                "ATS.");
    }

    @Test
    public void checkForError_unknownStatus() {
        doExceptionTest("8", "The an external service returned an unknown status");
    }

    @Test
    public void getMessage_SUCCESS() {
        assertThat(ServiceResponseStatus.SUCCESS.getErrorMessage(), is(equalTo("The external service reported that an invalid request was received")));
    }

    @Test
    public void getMessage_INVALID_REQUEST() {
        assertThat(ServiceResponseStatus.INVALID_REQUEST.getErrorMessage(), is(equalTo("The external service reported that an invalid request was received")));
    }

    @Test
    public void getMessage_INVALID_TOKEN() {
        assertThat(ServiceResponseStatus.INVALID_TOKEN.getErrorMessage(), is(equalTo("The an external service reported that in invalid authorisation token was used")));
    }

    @Test
    public void getMessage_SERVICE_BUSY() {
        assertThat(ServiceResponseStatus.SERVICE_BUSY.getErrorMessage(), is(equalTo("The external service reported that it was busy")));
    }

    @Test
    public void getMessage_SERVICE_ERROR() {
        assertThat(ServiceResponseStatus.SERVICE_ERROR.getErrorMessage(), is(equalTo("The an external service reported that it encountered an internal error")));
    }

    @Test
    public void getMessage_JOB_NOT_LIVE() {
        assertThat(ServiceResponseStatus.JOB_NOT_LIVE.getErrorMessage(), is(equalTo("The external service reported that the job being processed is no longer live")));
    }

    @Test
    public void getMessage_BATCH_LIMIT_EXCEEDED() {
        assertThat(ServiceResponseStatus.BATCH_LIMIT_EXCEEDED.getErrorMessage(), is(equalTo("The an external service reported that the job being processed is part of a request that has exceeded the maximum batch size of job details that can be requested from the ATS.")));
    }
}
