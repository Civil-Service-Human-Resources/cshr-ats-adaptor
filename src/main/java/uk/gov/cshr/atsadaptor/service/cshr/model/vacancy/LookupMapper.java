package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping the data for a lookup value out of data supplied in the
 * Applicant Tracking System data model.
 */
@Component
@Slf4j
class LookupMapper extends DataMapper {
    /**
     * Maps the data from the source to the CSHR Data model.
     * <p>
     * <p>The method will return if there is no data to map.
     *
     * @param source data from Applicant Tracking System containing data that is a lookup value
     * @return value of the lookup or null if there is no data
     */
    String map(Map<String, Object> source, String fieldName) {
        log.debug("Mapping a lookup data for a field called " + fieldName);

        String result = null;

        if (mainFieldCanBeMapped(source, fieldName)) {
            result = getValue(source, fieldName);
        }

        log.debug("Result of LookupMapper mapping for field " + fieldName + " is " + result);

        return result;
    }

    @Override
    protected String getValue(Map<String, Object> source, String fieldName) {
        Map<String, Object> allFields = (Map<String, Object>) source.get(FIELD);
        Map<String, Object> field = (Map<String, Object>) allFields.get(fieldName);
        Map<String, Object> value = (Map<String, Object>) field.get(VALUE);

        return (String) value.get("text");
    }
}
