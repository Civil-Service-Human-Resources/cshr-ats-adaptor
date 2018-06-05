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
 * Tests {@link LinkToApplyMapper}
 */
public class LinkToApplyMapperTest extends AbstractMappingTest {
    private LinkToApplyMapper mapper;

    @Before
    public void setup() {
        mapper = new LinkToApplyMapper();
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
    public void testMap_applyOnline() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "vac_template", true);
        toggleDisplayFieldValue(source, "v9applyurl", true);
        Map<String, Object> vacancyTemplateField = getField(source,"vac_template");
        vacancyTemplateField.put("value", "1478");

        assertThat(mapper.map(source), is(equalTo("https://csrtesting.wcn.co.uk/csr/login.cgi?jcode=1562398")));
    }

    @Test
    public void testMap_advertOnly() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "vac_template", true);
        toggleDisplayFieldValue(source, "115_5070000", true);
        Map<String, Object> vacancyTemplateField = getField(source,"vac_template");
        vacancyTemplateField.put("value", "anyValue");
        Map<String, Object> advertOnlyField = getField(source,"115_5070000");
        advertOnlyField.put("value", "This would be a link to be able to find out how to apply for this vacancy.");

        assertThat(mapper.map(source), is(equalTo("This would be a link to be able to find out how to apply for this vacancy.")));
    }
}
