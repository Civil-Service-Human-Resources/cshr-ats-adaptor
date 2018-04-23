package uk.gov.cshr.atsadaptor.service.cshr;

import java.util.List;

import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;

/**
 * Specifies the methods for working with the CSHR Vacancy service.
 */
public interface VacancyService {
    /**
     * This method is responsible for processing the collection of vacancies in the given list of
     * vacancies that have changed since the last run.
     * <p>
     * <p>Changed vacancies in this context means those that are new or have been modified. It does
     * not include those that have been closed.
     * <p>
     * <p>The processing involves retrieving the full data set for each vacancy, mapping the Applicant
     * Tracking System data model onto the CSHR data model and submitting each mapped vacancy to the
     * CSHR data store using the CSHR-API service.
     *
     * @param changedVacancies list of vacancies that have changed since the last run.
     */
    void processChangedVacancies(List<VacancyListData> changedVacancies);
}
