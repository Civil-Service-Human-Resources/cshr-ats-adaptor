package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import java.util.List;

/**
 * Defines the methods available for getting the list of active jobs from an external Applicant Tracking System (ATS)
 */
public interface JobsListRetriever {
    /**
     * This method is responsible for getting a list of live vacancies from an external ATS.
     * <p>
     * The list will be used to retrieve the details of each vacancy in the in the list and also to check for any closed vacancies in the CSHR Vacancy data store.
     *
     * @return List<VacancyListData> list of live vacancies
     */
    List<VacancyListData> getLiveVacancies();
}
