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
 * Tests {@link DefaultMapper}
 */
public class DefaultMapperTest extends AbstractMappingTest {
    private DefaultMapper mapper;

    @Before
    public void setup() {
        mapper = new DefaultMapper();
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void testMap_noSourceSupplied() {
        assertThat(mapper.map(null, "047_5070000"), is(nullValue()));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        assertThat(mapper.map(new LinkedHashMap<>(), "047_5070000"), is(nullValue()));
    }

    @Test
    public void testMap_notDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "047_5070000", false);

        assertThat(mapper.map(source, "047_5070000"), is(nullValue()));
    }

    @Test
    public void testMap_displayedButNoValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source, "047_5070000");
        field.put("value", "");

        assertThat(mapper.map(source, "047_5070000"), is(nullValue()));
    }

    @Test
    public void testMap_displayedWithValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        assertThat(mapper.map(source, "047_5070000"), is(equalTo("Sift/interview dates and location Text")));
    }

    @Test(expected = NullPointerException.class)
    public void testMap_displayedWithFooValue() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);

        mapper.map(source, "foodog");
    }
}
