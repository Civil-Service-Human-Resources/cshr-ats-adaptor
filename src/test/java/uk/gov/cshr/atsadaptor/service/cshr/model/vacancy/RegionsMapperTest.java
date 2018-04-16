package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link RegionsMapper}
 */
public class RegionsMapperTest extends AbstractMappingTest {
    private RegionsMapper mapper;

    @Before
    public void setup() {
        mapper = new RegionsMapper();
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
    public void testMap_notRegionType() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source,"location_method");
        field.put("value", "ABSOLUTE");

        assertThat(mapper.map(source), is(nullValue()));
    }

    @Test
    public void testMap_invalidRegion() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> field = getField(source,"location_method");
        field.put("value", "REGION");

        Map<String, Object> location = getField(source,"location");
        List<Map<String, Object>> regions = new ArrayList<>();
        regions.add(createRegion("FOO"));
        location.put("value", regions);

        assertThat(mapper.map(source), is(nullValue()));
    }

    @Test
    public void testMap_validRegions() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "location", true);
        Map<String, Object> field = getField(source,"location_method");
        field.put("value", "REGION");

        Map<String, Object> location = getField(source,"location");
        List<Map<String, Object>> regions = new ArrayList<>();
        regions.add(createRegion("REGION_EM"));
        regions.add(createRegion("REGION_LO"));
        regions.add(createRegion("REGION_WA"));
        location.put("value", regions);

        assertThat(mapper.map(source), is(equalTo("East Midlands, London, Wales")));
    }

    @Test
    public void testMap_validRegionsAndOneInvalid() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "location", true);
        Map<String, Object> field = getField(source,"location_method");
        field.put("value", "REGION");

        Map<String, Object> location = getField(source,"location");
        List<Map<String, Object>> regions = new ArrayList<>();
        regions.add(createRegion("REGION_LO"));
        regions.add(createRegion("FOO"));
        regions.add(createRegion("REGION_WA"));
        regions.add(createRegion("BAR"));
        regions.add(createRegion("REGION_EM"));
        location.put("value", regions);

        assertThat(mapper.map(source), is(equalTo("East Midlands, London, Wales")));
    }

    private Map<String, Object> createRegion(String code) {
        Map<String, Object> region = new LinkedHashMap<>();

        region.put("easting", "0");
        region.put("latitude", "0");
        region.put("longitude", "0");
        region.put("northing", "0");
        region.put("region", code);
        region.put("text", code);

        return region;
    }

}
