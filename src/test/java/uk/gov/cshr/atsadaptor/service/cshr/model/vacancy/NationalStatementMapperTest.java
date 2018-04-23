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
 * Tests {@link NationalStatementMapper}
 */
public class NationalStatementMapperTest extends AbstractMappingTest {
    private static final String FIELD_NAME = "nghr_reserve";
    private static final String NOT_APPLICABLE = "NONE";
    private static final String VALUE = "value";

    private NationalStatementMapper mapper;

    @Before
    public void setup() {
        mapper = new NationalStatementMapper();
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
        assertThat(mapper.map(new LinkedHashMap<>()), is(equalTo(NOT_APPLICABLE)));
    }

    @Test
    public void testMap_fieldIsNull() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> nationality = getField(source, FIELD_NAME);
        nationality.remove(VALUE);

        assertThat(mapper.map(new LinkedHashMap<>()), is(equalTo(NOT_APPLICABLE)));
    }

    @Test
    public void testMap_invalidValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> nationality = getField(source,FIELD_NAME);
        Map<String, Object> value = (Map<String, Object>) nationality.get(VALUE);
        value.put("code", "foo");

        assertThat(mapper.map(source), is(equalTo(NOT_APPLICABLE)));
    }

    @Test
    public void testMap_nonReservedVacancy() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> nationality = getField(source, FIELD_NAME);
        Map<String, Object> value = (Map<String, Object>) nationality.get(VALUE);
        value.put("code", "167681");

        assertThat(mapper.map(source), is(equalTo("NON_RESERVED")));
    }

    @Test
    public void testMap_ReservedVacancy() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        assertThat(mapper.map(source), is(equalTo("RESERVED")));
    }
}
