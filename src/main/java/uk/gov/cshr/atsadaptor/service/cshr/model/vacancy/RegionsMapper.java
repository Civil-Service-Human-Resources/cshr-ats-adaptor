package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.model.Region;

@Component
@Slf4j
class RegionsMapper extends DataMapper {
    private static final String MAIN_FIELD = "location";

    /**
     * Data from Applicant Tracking System containing data used to derive the value that represents
     * absolute locations in the CSHR Data model.
     *
     * @param source raw data from Applicant Tracking System used to extract and map
     * @return a comma separated list of the regions
     */
    String map(Map<String, Object> source) {
        log.debug("Mapping a data for regions");

        String result = null;

        if (regionsCanBeMapped(source)) {
            List<Map<String, Object>> locations = getValue(source);

            result =
                    locations
                            .stream()
                            .map(this::mapCodeToText)
                            .filter(StringUtils::isNotEmpty)
                            .sorted()
                            .collect(Collectors.joining(", "));
        }

        log.debug(
                "Result of RegionsMapper mapping is " + (StringUtils.isNotEmpty(result) ? result : null));

        return StringUtils.isNotEmpty(result) ? result : null;
    }

    private boolean regionsCanBeMapped(Map<String, Object> source) {
        return source != null && !source.isEmpty() && locationIsRegionType(source);
    }

    boolean locationIsRegionType(Map<String, Object> source) {
        return "REGION".equalsIgnoreCase(getValue(source, "location_method"));
    }

    private List<Map<String, Object>> getValue(Map<String, Object> source) {
        Map<String, Object> fields = (Map<String, Object>) source.get(FIELD);
        Map<String, Object> field = (Map<String, Object>) fields.get(MAIN_FIELD);

        return (List<Map<String, Object>>) field.get(VALUE);
    }

    private String mapCodeToText(Map<String, Object> location) {
        String code = (String) location.get("region");

        return Region.lookupDescription(code);
    }
}
