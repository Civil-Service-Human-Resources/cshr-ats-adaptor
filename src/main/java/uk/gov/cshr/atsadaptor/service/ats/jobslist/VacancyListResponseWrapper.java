package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is the java representation of the json body returned from an Applicant Tracking System (ATS) when a
 * request to it for the list of live vacancies.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
class VacancyListResponseWrapper {
    private VacancyListResponse vacancyResponse;
}
