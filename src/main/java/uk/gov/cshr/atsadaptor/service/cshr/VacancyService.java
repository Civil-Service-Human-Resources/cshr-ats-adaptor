package uk.gov.cshr.atsadaptor.service.cshr;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
     * @param jobsNoLongerActive list of jobs that are in changedVacancies list but are no longer active by the time they are processed.
     * @param auditFilePath path to the audit file
     * @param statistics totals of number processed, created, changed, deleted vacancies and number of errors
     * @return list of jobs that are in changedVacancies list but are no longer active by the time they are processed
     */
    List<String> processChangedVacancies(List<VacancyListData> changedVacancies, List<String> jobsNoLongerActive, Path auditFilePath, Map<String, Integer> statistics);

    /**
     * This method is responsible for processing the collection of vacancies in the given list of
     * vacancies that are no longer active since the last run.
     * <p>
     * <p>Vacancies no longer active in this context means those that are are not in the ATS list of live vacancies
     * <p>
     * <p>The processing involves retrieving the full data set for each vacancy, mapping the Applicant
     * Tracking System data model onto the CSHR data model and submitting each mapped vacancy to the
     * CSHR data store using the CSHR-API service.
     *
     * @param liveJobs list of vacancies that have changed since the last run.
     * @param jobsNoLongerActive list of jobs that are in changedVacancies list but are no longer active by the time they are processed.
     * @param auditFilePath path to the audit file
     * @param statistics totals of number processed, created, changed, deleted vacancies and number of errors
     */
    void deleteNonActiveVacancies(List<VacancyListData> liveJobs, List<String> jobsNoLongerActive, Path auditFilePath, Map<String, Integer> statistics);
}
