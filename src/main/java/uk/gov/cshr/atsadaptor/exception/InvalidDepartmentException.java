package uk.gov.cshr.atsadaptor.exception;

import uk.gov.cshr.exception.CSHRServiceException;
import uk.gov.cshr.status.CSHRServiceStatus;

/**
 * This exception is raised when either no department or unknown departement is supplied from the ats with the vacancy.
 */
public class InvalidDepartmentException extends CSHRServiceException {
    public InvalidDepartmentException(CSHRServiceStatus cshrServiceStatus) {
        super(cshrServiceStatus);
    }
}
