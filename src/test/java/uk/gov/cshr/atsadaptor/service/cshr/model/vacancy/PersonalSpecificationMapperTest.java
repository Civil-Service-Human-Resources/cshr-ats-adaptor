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
 * Tests {@link PersonalSpecificationMapper}
 */
public class PersonalSpecificationMapperTest extends AbstractMappingTest {
    private PersonalSpecificationMapper mapper;

    @Before
    public void setup() {
        mapper = new PersonalSpecificationMapper();
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
    public void testMap_doNotDisplay() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "148_5070000", false);

        assertThat(mapper.map(source), is(nullValue()));
    }

    @Test
    public void testMap_onlyMainFieldDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source,false, false, false, false);

        assertThat(mapper.map(source), is(equalTo("Person specification Required")));
    }

    private void toggleFields(Map<String, Object> source, boolean displayLanguages, boolean displayLicences,
                              boolean displayMemberships, boolean displayGradeEntry) throws IOException {
        toggleDisplayFieldValue(source, "113_5070000", displayLanguages);
        toggleDisplayFieldValue(source, "111_5070000", displayLicences);
        toggleDisplayFieldValue(source, "112_5070000", displayMemberships);
        toggleDisplayFieldValue(source, "099_5070000", displayGradeEntry);
    }

    @Test
    public void testMap_mainAndLangaugesDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

      toggleFields(source,true, false, false, false);

        assertThat(mapper.map(source),
                is(equalTo("Person specification Required<br><b>Languages Label</b><br>Languages Required")));
    }

    @Test
    public void testMap_mainAndLicencesDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source,false, true, false, false);

        assertThat(mapper.map(source),
                is(equalTo("Person specification Required<br><b>Licences Label</b><br>Licences Required")));
    }

    @Test
    public void testMap_mainAndMembershipsDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source,false, false, true, false);

        assertThat(mapper.map(source),
                is(equalTo("Person specification Required<br><b>Memberships Label</b><br>Memberships Required")));
    }

    @Test
    public void testMap_mainAndGradeEntryDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source,false, false, false, true);

        assertThat(mapper.map(source),
                is(equalTo("Person specification Required<br><b>Grade entry qualifications Label</b><br>Grade entry qualifications Text")));
    }

    @Test
    public void testMap_allDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        toggleFields(source,true, true, true, true);

        assertThat(mapper.map(source),
                is(equalTo(buildExpected())));
    }

    private String buildExpected() {
        return "Person specification Required<br><b>Licences Label</b><br>Licences Required" +
                "<br><b>Memberships Label</b><br>Memberships Required" +
                "<br><b>Languages Label</b><br>Languages Required" +
                "<br><b>Grade entry qualifications Label</b><br>Grade entry qualifications Text";
    }
}
