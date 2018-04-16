package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link OverseasJobMapper}
 */
public class OverseasJobMapperTest extends AbstractMappingTest {
    private static final String LOCATION_METHOD = "location_method";
    private static final String VALUE = "value";

    private OverseasJobMapper mapper;

    @Before
    public void setup() {
        mapper = new OverseasJobMapper();
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void testMap_noSourceSupplied() {
        assertThat(mapper.map(null), is(false));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        assertThat(mapper.map(new LinkedHashMap<>()), is(false));
    }

    @Test
    public void testMap_notOverseasType() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source, LOCATION_METHOD);
        field.put(VALUE, "ABSOLUTE");

        assertThat(mapper.map(source), is(false));
    }

    @Test
    public void testMap_overseasType() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, LOCATION_METHOD, true);
        Map<String, Object> field = getField(source, LOCATION_METHOD);
        field.put(VALUE, "OVERSEAS");

        assertThat(mapper.map(source), is(true));
    }
}
