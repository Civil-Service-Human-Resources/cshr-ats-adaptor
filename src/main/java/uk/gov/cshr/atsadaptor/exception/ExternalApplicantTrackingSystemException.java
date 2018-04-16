package uk.gov.cshr.atsadaptor.exception;

import uk.gov.cshr.exception.CSHRServiceException;
import uk.gov.cshr.status.CSHRServiceStatus;

/**
 * This exception is raised when an Applicant Tracking System reports a problem.
 */
public class ExternalApplicantTrackingSystemException extends CSHRServiceException {
    public ExternalApplicantTrackingSystemException(CSHRServiceStatus cshrServiceStatus) {
        super(cshrServiceStatus);
    }
}
