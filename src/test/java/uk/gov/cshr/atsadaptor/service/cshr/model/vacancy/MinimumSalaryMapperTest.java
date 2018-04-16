package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link MinimumSalaryMapper}
 */
public class MinimumSalaryMapperTest extends AbstractMappingTest {
    private MinimumSalaryMapper mapper;

    @Before
    public void setup() {
        mapper = new MinimumSalaryMapper();
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
    public void testMap_displayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        assertThat(mapper.map(source), is(equalTo(40001)));
    }

    @Test
    public void testMap_upToValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source, "nghr_salary");
        List<Map<String, Object>> bands = new ArrayList<>();

        Map<String, Object> band = new LinkedHashMap<>();
        band.put("code", "123");
        band.put("text", "Up to £15,000");
        bands.add(band);
        field.put("value", bands);

        assertThat(mapper.map(source), is(equalTo(15000)));
    }

    @Test
    public void testMap_onlySingleAboveValueElement() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source, "nghr_salary");
        List<Map<String, Object>> bands = new ArrayList<>();

        Map<String, Object> band = new LinkedHashMap<>();
        band.put("code", "123");
        band.put("text", "£100,001 and above");
        bands.add(band);
        field.put("value", bands);

        assertThat(mapper.map(source), is(equalTo(100001)));
    }
}
