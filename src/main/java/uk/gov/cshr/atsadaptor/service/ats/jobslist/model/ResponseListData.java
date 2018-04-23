package uk.gov.cshr.atsadaptor.service.ats.jobslist.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * This class represents the actual data in the response for the listRequest request type on the
 * external Applicant Tracking System (ATS).
 * <p>
 * <p>The response data for the 'listRequest' request type has the following attributes:
 * <p>
 * <pre>
 *     <ul>
 *         <li>responseDataTimestamp</li>
 *         <li>vacancyJobRequestLimit - Represents the maximum number of jobs that can be contained in the jobRequest request type. The ATS' api specifies this value.</li>
 *         <li>vacancyList - The list of active vacancies found in the ATS</li>
 *         <li>vacancyListCount - The number of live vacancies found</li>
 *     </ul>
 * </pre>
 */
@Builder
@Data
public class ResponseListData {
    private String responseDataTimestamp;
    private int vacancyJobRequestLimit;
    private List<VacancyListData> vacancyList;
    private int vacancyListCount;
}
