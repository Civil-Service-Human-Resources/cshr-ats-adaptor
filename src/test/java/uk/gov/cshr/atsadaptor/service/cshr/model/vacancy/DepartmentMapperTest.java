package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.atsadaptor.service.cshr.DepartmentsService;
import uk.gov.cshr.atsadaptor.service.cshr.model.department.Department;

/**
 * Tests {@link DepartmentMapper}
 */
@RunWith(SpringRunner.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DepartmentMapperTest extends AbstractMappingTest {
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
    public void testMap_noSourceSupplied() {
        assertThat(mapper.map(null), is(nullValue()));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        assertThat(mapper.map(new LinkedHashMap<>()), is(nullValue()));
    }

    @Test
    public void testMap_departmentUnknown() throws IOException {
        setupMock();

        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_dept", true);
        Map<String, Object> field = getField(source, "nghr_dept");
        Map<String, Object> departmentField = (Map<String, Object>) field.get("value");
        departmentField.put("text", "unknownDepartment");

        assertThat(mapper.map(source), is(nullValue()));
    }

    private void setupMock() {
        List<Department> departments = new ArrayList<>();
        departments.add(Department.builder().id(1).name("name").build());

        given(departmentsService.getDepartments()).willReturn(departments);
    }

    @Test
    public void testMap_departmentKnown() throws IOException {
        setupMock();

        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_dept", true);
        Map<String, Object> field = getField(source, "nghr_dept");
        Map<String, Object> departmentField = (Map<String, Object>) field.get("value");
        departmentField.put("text", "name");

        Map<String, Object> actual = mapper.map(source);

        assertThat(actual.get("id"), is(equalTo(1)));
        assertThat(actual.get("name"), is(equalTo("name")));
    }

    @Test
    public void testMap_departmentKnownMixedCase() throws IOException {
        setupMock();

        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_dept", true);
        Map<String, Object> field = getField(source, "nghr_dept");
        Map<String, Object> departmentField = (Map<String, Object>) field.get("value");
        departmentField.put("text", "NAMe");

        Map<String, Object> actual = mapper.map(source);

        assertThat(actual.get("id"), is(equalTo(1)));
        assertThat(actual.get("name"), is(equalTo("name")));
    }
}
