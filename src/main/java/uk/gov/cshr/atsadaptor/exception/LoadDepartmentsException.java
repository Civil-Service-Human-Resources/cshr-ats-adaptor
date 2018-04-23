package uk.gov.cshr.atsadaptor.exception;

import uk.gov.cshr.exception.CSHRServiceException;
import uk.gov.cshr.status.CSHRServiceStatus;

/**
 * This exception is raised when an external service reports a problem trying to load departments.
 */
public class LoadDepartmentsException extends CSHRServiceException {
    public LoadDepartmentsException(CSHRServiceStatus cshrServiceStatus) {
        super(cshrServiceStatus);
    }
}
