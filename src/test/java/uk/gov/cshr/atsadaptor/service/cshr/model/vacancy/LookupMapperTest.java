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
 * Tests {@link LookupMapper}
 */
public class LookupMapperTest extends AbstractMappingTest {
    private static final String FIELD_NAME = "070_5070000";
    private static final String VALUE = "value";

    private LookupMapper mapper;

    @Before
    public void setup() {
        mapper = new LookupMapper();
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void testMap_noSourceSupplied() {
        assertThat(mapper.map(null, FIELD_NAME), is(nullValue()));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        assertThat(mapper.map(new LinkedHashMap<>(), FIELD_NAME), is(nullValue()));
    }

    @Test
    public void testMap_fieldIsNull() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> lookup = getField(source, FIELD_NAME);
        Map<String, Object> value = (Map<String, Object>) lookup.get(VALUE);
        value.put("code", "");

        assertThat(mapper.map(new LinkedHashMap<>(), FIELD_NAME), is(nullValue()));
    }

    @Test
    public void testMap_validValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> lookup = getField(source, FIELD_NAME);
        Map<String, Object> value = (Map<String, Object>) lookup.get(VALUE);
        value.put("code", "1");

        assertThat(mapper.map(source, FIELD_NAME), is(equalTo("1")));
    }
}
