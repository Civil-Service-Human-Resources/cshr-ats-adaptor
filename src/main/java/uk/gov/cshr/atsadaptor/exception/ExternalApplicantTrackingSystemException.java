package uk.gov.cshr.atsadaptor.exception;

import uk.gov.cshr.error.CSHRServiceStatus;
import uk.gov.cshr.exception.CSHRServiceException;

/**
 * This exception is raised the external Applicant Tracking System report a problem.
 */
public class ExternalApplicantTrackingSystemException extends CSHRServiceException {
    public ExternalApplicantTrackingSystemException(CSHRServiceStatus cshrServiceStatus) {
        super(cshrServiceStatus);
    }
}
