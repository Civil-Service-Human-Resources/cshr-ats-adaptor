package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping a simple array of values into a comma separated string.
 */
@Component
@Slf4j
class MultiLookupMapper extends DataMapper {
    /**
     * This method iterates the list of values for the given fieldName and maps the values to a comma
     * separated list.
     * <p>
     * <p>The method can return null if there is nothing to map.
     *
     * @param source    raw data from Applicant Tracking System used to extract and map
     * @param fieldName name of the field whose values are to be mapped
     * @return comma separated string for the values for the given fieldName or null
     */
    String map(Map<String, Object> source, String fieldName) {
        log.debug("Mapping multi lookup data for a field called " + fieldName);

        String result = null;

        if (mainFieldCanBeMapped(source, fieldName)) {
            List<Map<String, Object>> fieldValues = getValue(fieldName, source);

            result =
                    fieldValues
                            .stream()
                            .map(fv -> (String) fv.get("text"))
                            .filter(StringUtils::isNotEmpty)
                            .sorted()
                            .collect(Collectors.joining(", "));
        }

        log.debug("Result of MultiLookupMapper mapping for field " + fieldName + " is " + result);

        return result;
    }

    private List<Map<String, Object>> getValue(String fieldName, Map<String, Object> source) {
        Map<String, Object> fields = (Map<String, Object>) source.get(FIELD);
        Map<String, Object> field = (Map<String, Object>) fields.get(fieldName);

        return (List<Map<String, Object>>) field.get(VALUE);
    }
}
