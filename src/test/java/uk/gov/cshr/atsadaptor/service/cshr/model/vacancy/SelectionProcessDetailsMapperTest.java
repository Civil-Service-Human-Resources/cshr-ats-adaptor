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
 * Tests {@link SelectionProcessDetailsMapper}
 */
public class SelectionProcessDetailsMapperTest extends AbstractMappingTest {
    private SelectionProcessDetailsMapper mapper;

    @Before
    public void setup() {
        mapper = new SelectionProcessDetailsMapper();
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
    public void testMap_selectDetailsNotDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "047_5070000", false);

        assertThat(mapper.map(source), is(nullValue()));
    }

    @Test
    public void testMap_onlyMainFieldDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source, false, false, false);

        assertThat(mapper.map(source), is(equalTo("Sift/interview dates and location Text")));
    }

    @Test
    public void testMap_mainAndTravelCostsDisplayedAndNoCostValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source, "152_5070000");
        field.put("value", "");

        toggleFields(source, true, false, false);

        assertThat(mapper.map(source), is(equalTo("Sift/interview dates and location Text")));
    }

    @Test
    public void testMap_mainAndTravelCostsDisplayedAndWithValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source, true,false, false);

        assertThat(mapper.map(source),
                is(equalTo("Sift/interview dates and location Text<p>Travel Costs Text</p>")));
    }

    private void toggleFields(Map<String, Object> source, boolean displayTravelExpenses,
                              boolean displayTestInstructions, boolean displayProcessDetails) throws IOException {
        toggleDisplayFieldValue(source, "047_5070000", true);
        toggleDisplayFieldValue(source, "152_5070000", displayTravelExpenses);
        toggleDisplayFieldValue(source, "180_5070000", displayTestInstructions);
        toggleDisplayFieldValue(source, "150_5070000", displayProcessDetails);
    }

    @Test
    public void testMap_mainAndTestInstructionsDisplayedAndNoValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source, "180_5070000");
        field.put("value", "");

        toggleFields(source, false,true,false);

        assertThat(mapper.map(source), is(equalTo("Sift/interview dates and location Text")));
    }

    @Test
    public void testMap_mainAndTestInstructionsDisplayedAndWithValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source, false,true,false);

        assertThat(mapper.map(source),
                is(equalTo("Sift/interview dates and location Text<p>Online test instructions Text</p>")));
    }

    @Test
    public void testMap_mainAndProcessDetailsDisplayedAndNoValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source, "150_5070000");
        field.put("value", "");

        toggleFields(source,false,false,true);

        assertThat(mapper.map(source), is(equalTo("Sift/interview dates and location Text")));
    }

    @Test
    public void testMap_mainAndProcessDetailsDisplayedAndWithValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source,false,false,true);

        assertThat(mapper.map(source),
                is(equalTo("Sift/interview dates and location Text<p>Selection process details Text</p>")));
    }

    @Test
    public void testMap_allFieldsDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source,true,true,true);

        assertThat(mapper.map(source),
                is(equalTo("Sift/interview dates and location Text" +
                        "<p>Travel Costs Text</p>" +
                        "<p>Online test instructions Text</p>" +
                        "<p>Selection process details Text</p>")));
    }
}
