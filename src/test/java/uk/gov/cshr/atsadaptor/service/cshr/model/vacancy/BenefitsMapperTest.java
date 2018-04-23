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
 * Tests {@link BenefitsMapper}
 */
public class BenefitsMapperTest extends AbstractMappingTest {
    private BenefitsMapper mapper;

    @Before
    public void setup() {
        mapper = new BenefitsMapper();
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
    public void testMap_notDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "046_5070000", false);

        assertThat(mapper.map(new LinkedHashMap<>()), is(nullValue()));
    }

    @Test
    public void testMap_onlyMainDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleDisplayFieldValue(source, "046_5070000", true);
        toggleDisplayFieldValue(source, "222_5070000", false);
        toggleDisplayFieldValue(source, "221_5070000", false);

        assertThat(mapper.map(source), is(equalTo("Benefits Text")));
    }

    @Test
    public void testMap_onlyMainAndOverseasAllowancesDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleDisplayFieldValue(source, "046_5070000", true);
        toggleDisplayFieldValue(source, "222_5070000", true);
        toggleDisplayFieldValue(source, "221_5070000", false);

        assertThat(mapper.map(source), is(equalTo("Benefits Text<p>Overseas Allowances text</p>")));
    }

    @Test
    public void testMap_onlyMainAndThirdParaDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleDisplayFieldValue(source, "046_5070000", true);
        toggleDisplayFieldValue(source, "222_5070000", false);
        toggleDisplayFieldValue(source, "221_5070000", true);

        assertThat(mapper.map(source), is(equalTo("Benefits Text<p>Some benefits text</p>")));
    }

    @Test
    public void testMap_allPartsDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleDisplayFieldValue(source, "046_5070000", true);
        toggleDisplayFieldValue(source, "222_5070000", true);
        toggleDisplayFieldValue(source, "221_5070000", true);

        assertThat(mapper.map(source), is(equalTo("Benefits Text<p>Overseas Allowances text</p><p>Some benefits text</p>")));
    }
}
