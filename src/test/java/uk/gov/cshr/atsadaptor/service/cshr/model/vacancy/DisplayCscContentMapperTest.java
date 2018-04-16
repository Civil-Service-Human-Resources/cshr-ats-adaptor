package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

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
 * Tests {@link DisplayCscContentMapper}
 */
public class DisplayCscContentMapperTest extends AbstractMappingTest {
    private DisplayCscContentMapper mapper;

    @Before
    public void setup() {
        mapper = new DisplayCscContentMapper();
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
    public void testMap_notDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "123_5070000", false);

        assertThat(mapper.map(source), is(false));
    }

    @Test
    public void testMap_displayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "123_5070000", true);

        assertThat(mapper.map(source), is(true));
    }
}
