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
 * Tests {@link MaximumSalaryMapper}
 */
public class MaximumSalaryMapperTest extends AbstractMappingTest {
    private MaximumSalaryMapper mapper;

    @Before
    public void setup() {
        mapper = new MaximumSalaryMapper();
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

        assertThat(mapper.map(source), is(equalTo(60000)));
    }

    @Test
    public void testMap_containsAboveValueTextOnly() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> location = getField(source,"nghr_salary");

        List<Map<String, Object>> salaryBands = new ArrayList<>();
        salaryBands.add(createSalaryBand("166639", "  40,001 - 50,000"));
        salaryBands.add(createSalaryBand("166640", "  50,001 - 60,000"));
        salaryBands.add(createSalaryBand("166641", "  60,001 - 70,000"));
        salaryBands.add(createSalaryBand("166642", "  70,001 - 80,000"));
        salaryBands.add(createSalaryBand("166643", "  80,001 - 90,000"));
        salaryBands.add(createSalaryBand("166644", "  90,001 - 100,000"));
        salaryBands.add(createSalaryBand("166645", "  £100,001 and above"));

        location.put("value", salaryBands);

        assertThat(mapper.map(source), is(nullValue()));
    }

    private Map<String, Object> createSalaryBand(String code, String text) {
        Map<String, Object> band = new LinkedHashMap<>();

        band.put("code", code);
        band.put("text", text);

        return band;
    }
}
