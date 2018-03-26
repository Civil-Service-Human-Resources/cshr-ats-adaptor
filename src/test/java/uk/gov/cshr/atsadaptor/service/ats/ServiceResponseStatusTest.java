package uk.gov.cshr.atsadaptor.service.ats;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.fail;

import org.junit.Test;
import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.error.ErrorCode;

/**
 * Tests {@link ServiceResponseStatus}
 */
public class ServiceResponseStatusTest {
    @Test
    public void findByCode_nullCode() {
        assertThat(ServiceResponseStatus.findByCode(null), nullValue());
    }

    @Test
    public void findByCode_unknownCode() {
        assertThat(ServiceResponseStatus.findByCode("foo"), nullValue());
    }

    @Test
    public void findByCode_SUCCESS() {
        assertThat(ServiceResponseStatus.findByCode("1"), is(equalTo(ServiceResponseStatus.SUCCESS)));
    }

    @Test
    public void findByCode_INVALID_REQUEST() {
        assertThat(ServiceResponseStatus.findByCode("2"), is(equalTo(ServiceResponseStatus.INVALID_REQUEST)));
    }

    @Test
    public void findByCode_INVALID_TOKEN() {
        assertThat(ServiceResponseStatus.findByCode("3"), is(equalTo(ServiceResponseStatus.INVALID_TOKEN)));
    }

    @Test
    public void findByCode_SERVICE_BUSY() {
        assertThat(ServiceResponseStatus.findByCode("4"), is(equalTo(ServiceResponseStatus.SERVICE_BUSY)));
    }

    @Test
    public void findByCode_SERVICE_ERROR() {
        assertThat(ServiceResponseStatus.findByCode("5"), is(equalTo(ServiceResponseStatus.SERVICE_ERROR)));
    }

    @Test
    public void checkForError_noneFound() {
        try {
            ServiceResponseStatus.SUCCESS.checkForError();
        } catch (Exception e) {
            fail("No exception should have been thrown");
        }
    }

    @Test
    public void checkForError_invalidRequest() {
        doExceptionTest(ServiceResponseStatus.INVALID_REQUEST, "The external service reported that an invalid request was received");
    }

    private void doExceptionTest(ServiceResponseStatus status, String expectedMessage) {
        try {
            status.checkForError();
        } catch (Exception e) {
            assertThat(e, instanceOf(ExternalApplicantTrackingSystemException.class));

            ExternalApplicantTrackingSystemException ex = (ExternalApplicantTrackingSystemException) e;
            assertThat(ex.getCshrServiceStatus().getCode(), is(equalTo(ErrorCode.THIRD_PARTY_SERVICE_ERROR.getCode())));
            assertThat(ex.getCshrServiceStatus().getSummary(), is(equalTo(expectedMessage)));
        }
    }

    @Test
    public void checkForError_invalidAuthToken() {
        doExceptionTest(ServiceResponseStatus.INVALID_TOKEN, "The an external service reported that in invalid authorisation token was used");
    }

    @Test
    public void checkForError_externalServiceBusy() {
        doExceptionTest(ServiceResponseStatus.SERVICE_BUSY, "The external service reported that it was busy");
    }

    @Test
    public void checkForError_externalServiceError() {
        doExceptionTest(ServiceResponseStatus.SERVICE_ERROR, "The an external service reported that it encountered an internal error");
    }
}
