package uk.gov.cshr.atsadaptor.service.ats;

import java.util.HashMap;
import java.util.Map;

import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This enum represents the possible status codes that can be returned from any request to the
 * Applicant Tracking System's api.
 * <p>
 * <p>It provides a method for throwing an exception for any status that is not SUCCESS.
 */
public enum ServiceResponseStatus {
    SUCCESS("The external service reported that an invalid request was received"),
    INVALID_REQUEST("The external service reported that an invalid request was received"),
    INVALID_TOKEN("The an external service reported that in invalid authorisation token was used"),
    SERVICE_BUSY("The external service reported that it was busy"),
    SERVICE_ERROR("The an external service reported that it encountered an internal error"),
    JOB_NOT_LIVE("The external service reported that the job being processed is no longer live"),
    BATCH_LIMIT_EXCEEDED("The an external service reported that the job being processed is part of a request that has exceeded the maximum batch size of job details that can be requested from the ATS.");

    private static final Map<String, ServiceResponseStatus> STATUSES = new HashMap<>();

    static {
        STATUSES.put("1", SUCCESS);
        STATUSES.put("2", INVALID_REQUEST);
        STATUSES.put("3", INVALID_TOKEN);
        STATUSES.put("4", SERVICE_BUSY);
        STATUSES.put("5", SERVICE_ERROR);
        STATUSES.put("6", JOB_NOT_LIVE);
        STATUSES.put("7", BATCH_LIMIT_EXCEEDED);
    }

    private String errorMessage;

    ServiceResponseStatus(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * This method is responsible for checking if the enum is an error condition and if so throws a
     * ExternalApplicantTrackingSystemException.
     */
    public static void checkForError(String code) {
        ServiceResponseStatus status = STATUSES.get(code);
        String message =
                status != null ? status.errorMessage : "The an external service returned an unknown status";
        if (!SUCCESS.equals(STATUSES.get(code))) {
            throw new ExternalApplicantTrackingSystemException(
                    CSHRServiceStatus.builder()
                            .code(StatusCode.THIRD_PARTY_SERVICE_ERROR.getCode())
                            .summary(message)
                            .build());
        }
    }
}
