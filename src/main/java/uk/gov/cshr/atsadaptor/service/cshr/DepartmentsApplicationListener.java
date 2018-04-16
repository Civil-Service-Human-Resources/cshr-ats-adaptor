package uk.gov.cshr.atsadaptor.service.cshr;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.gov.cshr.atsadaptor.service.cshr.model.department.Department;

/**
 * This class is responsible for initiating the data load for Departments
 */
@Component
@Slf4j
public class DepartmentsApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
    private DepartmentsService departmentsService;

    public DepartmentsApplicationListener(DepartmentsService departmentsService) {
        this.departmentsService = departmentsService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        List<Department> departments = departmentsService.getDepartments();

        if (log.isDebugEnabled()) {
            departments.forEach(d -> log.debug("Found info for a department which is : " + d.toString()));
        }
    }
}
