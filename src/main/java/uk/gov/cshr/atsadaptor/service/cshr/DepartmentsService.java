package uk.gov.cshr.atsadaptor.service.cshr;

import java.util.List;

import uk.gov.cshr.atsadaptor.service.cshr.model.department.Department;

/**
 * Defines the methods for working with the cshr department apis
 */
public interface DepartmentsService {
    /**
     * Loads all the departments from the CSHR Vacancy data store and creates a cache on startup so
     * that the api call to the CSHR data store is only made once.
     *
     * @return list of departments from the CSHR Vacancy data store
     */
    List<Department> getDepartments();

    /**
     * Removes all departments from the cache
     */
    void evictCache();
}
