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
 * Tests {@link DateMapper}
 */
public class DateMapperTest extends AbstractMappingTest {
    private DateMapper mapper;

    @Before
    public void setup() {
        mapper = new DateMapper();
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void testMap_noSourceSupplied() {
        assertThat(mapper.map(null, "closingdate", false), is(nullValue()));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        assertThat(mapper.map(new LinkedHashMap<>(), "closingdate", false), is(nullValue()));
    }

    @Test
    public void testMap_dateDoesNotExist() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "closingdate", true);
        Map<String, Object> field = getField(source,"closingdate");
        field.put("value", "");

        assertThat(mapper.map(source, "closingdate", false), is(nullValue()));
    }

    @Test
    public void testMap_dateExistsGMT() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "closingdate", true);
        Map<String, Object> field = getField(source,"closingdate");
        field.put("value", "2020-12-31");

        assertThat(mapper.map(source, "closingdate", false), is(equalTo("2020-12-31T23:59:59.999+0000")));
    }

    @Test
    public void testMap_dateExistsGMTExternalStartDate() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "startdate_external", true);
        Map<String, Object> field = getField(source,"startdate_external");
        field.put("value", "2020-12-31");

        assertThat(mapper.map(source, "startdate_external", true), is(equalTo("2020-12-31T00:00:00.000+0000")));
    }

    @Test
    public void testMap_dateExistsGMTGovtStartDate() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "startdate_across_gov", true);
        Map<String, Object> field = getField(source,"startdate_across_gov");
        field.put("value", "2020-12-31");

        assertThat(mapper.map(source, "startdate_across_gov", true), is(equalTo("2020-12-31T00:00:00.000+0000")));
    }

    @Test
    public void testMap_dateExistsGMTInternalStartDate() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "startdate_live", true);
        Map<String, Object> field = getField(source,"startdate_live");
        field.put("value", "2020-12-31");

        assertThat(mapper.map(source, "startdate_live", true), is(equalTo("2020-12-31T00:00:00.000+0000")));
    }
}
