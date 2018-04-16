package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
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
 * Tests {@link LocationsMapper}
 */
public class LocationsMapperTest extends AbstractMappingTest {
    private LocationsMapper mapper;

    @Before
    public void setup() {
        mapper = new LocationsMapper();
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void testMap_noSourceSupplied() {
        assertThat(mapper.map(null), is(empty()));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        assertThat(mapper.map(new LinkedHashMap<>()), is(empty()));
    }

    @Test
    public void testMap_notAbsoluteLocationType() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "location", true);
        Map<String, Object> locationMethodField = getField(source,"location_method");
        locationMethodField.put("value", "REGION");

        assertThat(mapper.map(source), is(empty()));
    }

    @Test
    public void testMap_validLocations() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "location", true);
        Map<String, Object> locationMethodField = getField(source,"location_method");
        locationMethodField.put("value", "ABSOLUTE");

        Map<String, Object> locationField = getField(source,"location");
        List<Map<String, Object>> locations = new ArrayList<>();
        locations.add(createLocation("51.4549291", "-2.6278111", "Bristol"));
        locations.add(createLocation("54.9806308", "-1.6167437", "Newcastle"));
        locationField.put("value", locations);

        assertThat(mapper.map(source), is(equalTo(buildExpectedLocations())));
    }

    private Map<String, Object> createLocation(String latitude, String longitude, String place) {
        Map<String, Object> location = new LinkedHashMap<>();

        location.put("easting", "0");
        location.put("latitude", latitude);
        location.put("longitude", longitude);
        location.put("northing", "0");
        location.put("region", "");
        location.put("text", place);

        return location;
    }

    private List<Map<String, Object>> buildExpectedLocations() {
        List<Map<String, Object>> locations = new ArrayList<>();

        Map<String, Object> location = new LinkedHashMap<>();
        location.put("latitude", "51.4549291");
        location.put("location", "Bristol");
        location.put("longitude", "-2.6278111");

        locations.add(location);

        location = new LinkedHashMap<>();

        location.put("latitude", "54.9806308");
        location.put("location", "Newcastle");
        location.put("longitude", "-1.6167437");

        locations.add(location);

        return locations;
    }
}
