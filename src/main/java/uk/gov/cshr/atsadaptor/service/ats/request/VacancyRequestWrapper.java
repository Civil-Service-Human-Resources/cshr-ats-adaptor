package uk.gov.cshr.atsadaptor.service.ats.request;

import lombok.Builder;
import lombok.Data;

/**
 * This class represents the top level object that wraps a VacancyRequest.
 */
@Builder
@Data
public class VacancyRequestWrapper {
    private VacancyRequest vacancyRequest;
}
