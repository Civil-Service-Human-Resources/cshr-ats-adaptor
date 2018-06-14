package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.atsadaptor.exception.InvalidDepartmentException;
import uk.gov.cshr.atsadaptor.service.cshr.DepartmentsService;
import uk.gov.cshr.atsadaptor.service.cshr.model.department.Department;
import uk.gov.cshr.status.CSHRServiceStatus;

/**
 * Tests {@link DepartmentMapper}
 */
@RunWith(SpringRunner.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DepartmentMapperTest extends AbstractMappingTest {
    private static final String JOB_REF = "1562398";

    @MockBean
    private DepartmentsService departmentsService;

    private DepartmentMapper mapper;

    @Before
    public void setup() {
        mapper = new DepartmentMapper(departmentsService);
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void map_noSourceSupplied() {
        assertThat(mapper.map(JOB_REF, null), is(nullValue()));
    }

    @Test
    public void tesap_emptySourceSupplied() {
        assertThat(mapper.map(JOB_REF, new LinkedHashMap<>()), is(nullValue()));
    }
    
    @Test
    public void map_noDepartmentSupplied() throws IOException {
        setupMock();

        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_dept", true);
        Map<String, Object> field = getField(source, "nghr_dept");
        Map<String, Object> departmentField = (Map<String, Object>) field.get("value");
        departmentField.put("text", null);

        try {
            mapper.map(JOB_REF, source);
            fail("An instance of InvalidDepartmentException should have been thrown");
        } catch (InvalidDepartmentException ide) {
            CSHRServiceStatus status = ide.getCshrServiceStatus();
            assertThat(status.getCode(), is(equalTo("CSHR_200")));
            assertThat(status.getSummary(), is(equalTo("No department was supplied for a vacancy with jobRef '1562398'")));
        }
    }

    @Test
    public void map_departmentUnknown() throws IOException {
        setupMock();

        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_dept", true);
        Map<String, Object> field = getField(source, "nghr_dept");
        Map<String, Object> departmentField = (Map<String, Object>) field.get("value");
        departmentField.put("text", "Department of Silly Walks");

        try {
            assertThat(mapper.map(JOB_REF, source), is(nullValue()));
            fail("An instance of InvalidDepartmentException should have been thrown");
        } catch (InvalidDepartmentException ide) {
            CSHRServiceStatus status = ide.getCshrServiceStatus();
            assertThat(status.getCode(), is(equalTo("CSHR_200")));
            assertThat(status.getSummary(), is(equalTo("A department called 'Department of Silly Walks' for a vacancy with jobRef '1562398' is not recognised in the CSHR Vacancy Data Store")));
        }
    }

    private void setupMock() {
        List<Department> departments = new ArrayList<>();
        departments.add(Department.builder().id(1).name("name").build());

        given(departmentsService.getDepartments()).willReturn(departments);
    }

    @Test
    public void map_departmentKnown() throws IOException {
        setupMock();

        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_dept", true);
        Map<String, Object> field = getField(source, "nghr_dept");
        Map<String, Object> departmentField = (Map<String, Object>) field.get("value");
        departmentField.put("text", "name");

        Map<String, Object> actual = mapper.map(JOB_REF, source);

        assertThat(actual.get("id"), is(equalTo(1)));
        assertThat(actual.get("name"), is(equalTo("name")));
    }

    @Test
    public void map_departmentKnownMixedCase() throws IOException {
        setupMock();

        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_dept", true);
        Map<String, Object> field = getField(source, "nghr_dept");
        Map<String, Object> departmentField = (Map<String, Object>) field.get("value");
        departmentField.put("text", "NAMe");

        Map<String, Object> actual = mapper.map(JOB_REF, source);

        assertThat(actual.get("id"), is(equalTo(1)));
        assertThat(actual.get("name"), is(equalTo("name")));
    }
}
