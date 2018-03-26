package uk.gov.cshr.atsadaptor.service.ats;

import java.util.HashMap;
import java.util.Map;

import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.error.CSHRServiceStatus;
import uk.gov.cshr.error.ErrorCode;

public enum ServiceResponseStatus {
    SUCCESS("The external service reported that an invalid request was received"),
    INVALID_REQUEST("The external service reported that an invalid request was received"),
    INVALID_TOKEN("The an external service reported that in invalid authorisation token was used"),
    SERVICE_BUSY("The external service reported that it was busy"),
    SERVICE_ERROR("The an external service reported that it encountered an internal error");

    private static final Map<String, ServiceResponseStatus> STATUSES = new HashMap<>();

    static {
        STATUSES.put("1", SUCCESS);
        STATUSES.put("2", INVALID_REQUEST);
        STATUSES.put("3", INVALID_TOKEN);
        STATUSES.put("4", SERVICE_BUSY);
        STATUSES.put("5", SERVICE_ERROR);
    }

    private String errorMessage;

    ServiceResponseStatus(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * This method is responsible for checking if the enum is an error condition and if so throws a RuntimeException.
     */
    public void checkForError() {
        if (!this.equals(SUCCESS)) {
            CSHRServiceStatus status = CSHRServiceStatus.builder()
                    .code(ErrorCode.THIRD_PARTY_SERVICE_ERROR.getCode())
                    .summary(errorMessage)
                    .build();

            throw new ExternalApplicantTrackingSystemException(status);
        }
    }

    /**
     * Finds an instance of this enum using the given code.
     * <p>
     * A null will be returned if there is no matching enum.
     *
     * @param code instance of this enum using the given code
     * @return ServiceResponseStatus instance of this enum for the given code
     */
    public static ServiceResponseStatus findByCode(String code) {
        return STATUSES.get(code);
    }
}
