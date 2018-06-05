package uk.gov.cshr.atsadaptor.service.ats.request;

import lombok.Builder;
import lombok.Data;

/**
 * This class represents the request body sent to the Applicant Tracking System's (ATS) api.
 * <p>
 * <p>A request will consist of three elements:
 * <p>
 * <pre>
 *     <ul>
 *         <li>
 *             requestType - he type of request that the ATS API will perform.
 *             <ul>
 *                 <li>listRequest - retrieves the list of vacancies that are live in the ATS</li>
 *                 <li>jobRequest - retrieves the details of each specified vacancy from the ATS</li>
 *                 <li>layoutRequest - retrieves meta information for the layout of the V9 candidate vacancy display</li>
 *             </ul>
 *         </li>
 *         <li>requestClientId - Client ID to be sent along with each request to the ATS API</li>
 *         <li>
 *             requestAuthToken - The Authentication token to be sent along with each request to the ATS API.
 *             This value is set by CSHR in the ATS itself and must be supplied with this service starts.
 *         </li>
 *     </ul>
 * </pre>
 */
@Builder
@Data
public class VacancyRequest {
    private String requestType;
    private String requestClientId;
    private String requestAuthToken;
    private String[] jcode;
}
