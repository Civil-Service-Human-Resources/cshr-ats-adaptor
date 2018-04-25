package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Maps the Location information from the Applicant Tracking System to the CSHR data model.
 */
@Component
@Slf4j
class LocationsMapper extends DataMapper {
    private static final String MAIN_FIELD = "location";

    /**
     * Maps the Applicant Tracking System vacancy information to the CSHR data model.
     * <p>
     * <p>The method checks the location type which must have a value of 'absolute'
     *
     * @param source vacancy info information about the locations of the vacancy
     * @return list of locations or an empty list if the location type does not have a value of
     * 'absolute'
     */
    List<Map<String, Object>> map(Map<String, Object> source) {
        log.debug("Mapping a data for locations");

        List<Map<String, Object>> vacancyLocations = new ArrayList<>();

        if (locationsCanBeMapped(source)) {
            List<Map<String, Object>> locations = getValue(source);

            vacancyLocations = locations.stream().map(this::mapLocation).collect(Collectors.toList());
        }

        log.debug("Result of LocationsMapper mapping is " + vacancyLocations.toString());

        return vacancyLocations;
    }

    private List<Map<String, Object>> getValue(Map<String, Object> source) {
        Map<String, Object> fields = (Map<String, Object>) source.get(FIELD);
        Map<String, Object> field = (Map<String, Object>) fields.get(MAIN_FIELD);

        return (List<Map<String, Object>>) field.get(VALUE);
    }

    private boolean locationsCanBeMapped(Map<String, Object> source) {
        return source != null && !source.isEmpty() && locationIsAbsoluteType(source);
    }

    private boolean locationIsAbsoluteType(Map<String, Object> source) {
        return "POSTCODE".equalsIgnoreCase(getValue(source, "location_method"));
    }

    private Map<String, Object> mapLocation(Map<String, Object> location) {
        Map<String, Object> mappedLocation = new LinkedHashMap<>();

        mappedLocation.put("latitude", location.get("latitude"));
        mappedLocation.put(MAIN_FIELD, location.get("text"));
        mappedLocation.put("longitude", location.get("longitude"));

        return mappedLocation;
    }
}
