package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link SalaryOverrideMapper}
 */
public class SalaryOverrideMapperTest extends AbstractMappingTest {
    private SalaryOverrideMapper mapper;

    @Before
    public void setup() {
        mapper = new SalaryOverrideMapper();
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
    public void testMap_noValuesExist() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_min_salary", false);
        toggleDisplayFieldValue(source, "nghr_max_salary", false);

        assertThat(mapper.map(source), is(nullValue()));
    }

    @Test
    public void testMap_justMinOverrideValueExists() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_min_salary", true);
        toggleDisplayFieldValue(source, "nghr_max_salary", false);

        assertThat(mapper.map(source), is(equalTo("£43,967")));
    }

    @Test
    public void testMap_bothValuesExist() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_min_salary", true);
        toggleDisplayFieldValue(source, "nghr_max_salary", true);

        assertThat(mapper.map(source), is(equalTo("£43,967 - £56,786")));
    }
}
