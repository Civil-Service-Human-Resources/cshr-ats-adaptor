package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.cshr.atsadaptor.exception.InvalidDepartmentException;
import uk.gov.cshr.atsadaptor.service.cshr.DepartmentsService;
import uk.gov.cshr.atsadaptor.service.cshr.model.department.Department;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class is responsible for mapping the Applicant Tracking System's value for the name of the
 * department into the identifier required by the CSHR vacancy data store
 */
@Component
@Slf4j
class DepartmentMapper extends LookupMapper {
    private DepartmentsService departmentsService;

    DepartmentMapper(DepartmentsService departmentsService) {
        this.departmentsService = departmentsService;
    }

    /**
     * Maps the department details based on the name of the department found in the source data.
     * <p>
     * <p>Since the CSHR internal department id is unknown in the source data a lookup, using the
     * Department service, for its id is performed.
     * <p>
     * <p>If no id can be found then null object representing the department will be returned.
     *
     * @param jobRef the job reference for the vacancy in the Applicant Tracking System
     * @param source raw data from Applicant Tracking System used to extract and map
     * @return object representing the department or null if it cannot be mapped
     */
    Map<String, Object> map(String jobRef, Map<String, Object> source) {
        log.debug("Mapping a data for a department");

        Map<String, Object> departmentContent = null;

        if (mainFieldCanBeMapped(source, "nghr_dept")) {
            String departmentName = getValue(source, "nghr_dept");

            if (StringUtils.isBlank(departmentName)) {
                log.debug("No department name supplied");
                throw new InvalidDepartmentException(buildStatusForException("No department was supplied for a vacancy with jobRef '" + jobRef + "'"));
            }

            log.debug("Looking up id for department called " + departmentName);

            Optional<Department> department =
                    departmentsService
                            .getDepartments()
                            .stream()
                            .filter(d -> d.getName().equalsIgnoreCase(departmentName))
                            .findFirst();

            if (department.isPresent()) {
                departmentContent = new LinkedHashMap<>();
                departmentContent.put("id", department.get().getId());
                departmentContent.put("name", department.get().getName());
                log.debug("Result of DepartmentMapper mapping is " + departmentContent.toString());
            } else {
                String summary = "A department called '"
                        + departmentName
                        + "' for a vacancy with jobRef '"
                        + jobRef
                        + "' is not recognised in the CSHR Vacancy Data Store";
                log.debug(summary);

                throw new InvalidDepartmentException(buildStatusForException(summary));
            }
        }

        return departmentContent;
    }

    private CSHRServiceStatus buildStatusForException(String summary) {
        return CSHRServiceStatus.builder()
                .code(StatusCode.INTERNAL_SERVICE_ERROR.getCode())
                .summary(summary)
                .build();
    }
}
