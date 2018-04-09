package uk.gov.cshr.atsadaptor.service.ats.jobslist.model;

import lombok.Builder;
import lombok.Data;

/**
 * This class is the java representation of the json body returned from an Applicant Tracking System (ATS) when
 * a request to it for the list of live vacancies.
 * <p>
 * Included is a statusCode and and a statusMessage
 * <p>
 * The value of the statusCode can be one of:
 * <pre>
 *     <ul>
 *         <li>1 - The request was successful</li>
 *         <li>2 - The request was invalid</li>
 *         <li>3 - The authentication credentials were invalid</li>
 *         <li>4 - The system was busy</li>
 *         <li>5 - The system reported an error</li>
 *     </ul>
 * </pre>
 * <p>
 * If the statusCode is 1 then responseData will be returned otherwise there will be none.
 */
@Builder
@Data
public class VacancyListResponse {
    private ResponseListData responseData;
    private String responseType;
    private String serviceVersion;
    private String statusCode;
    private String statusMessage;
}
