package uk.gov.cshr.atsadaptor.service.ats.jobslist.model;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

/**
 * This class represents the data about each vacancy in the Applicant Tracking System (ATS) when the
 * listRequest request type is sent to it.
 * <p>
 * The data consists of two properties:
 * <pre>
 *     <ul>
 *         <li>jcode - The id of the vacancy in the ATS</li>
 *         <li>vacancyTimestamp - Represents the last time the vacancy was changed in the ATS.</li>
 *     </ul>
 * </pre>
 */
@Builder
@Data
public class VacancyListData {
    private String jcode;
    private Timestamp vacancyTimestamp;
}
