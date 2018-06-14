package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.ArrayList;
import java.util.List;

import uk.gov.cshr.atsadaptor.service.cshr.DepartmentsService;
import uk.gov.cshr.atsadaptor.service.cshr.model.department.Department;

public class TestDepartmentService implements DepartmentsService {
    @Override
    public List<Department> getDepartments() {
        List<Department> departments = new ArrayList<>();

        departments.add(Department.builder().id(1).name("Department for International Trade").build());

        return departments;
    }

    @Override
    public void evictCache() {
    }
}
