package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * This class is the default mapper that simply returns a value for a field unmodified.
 */
@Component
@Slf4j
class DefaultMapper extends DataMapper {
    /**
     * Maps the data from the source to the CSHR Data model.
     * <p>
     * <p>The method will return null if there is no data to map.
     *
     * @param source data from Applicant Tracking System containing data used to map the value of a
     *               generic field
     * @return the raw value of the field unmodified or null if there is no data to return
     */
    String map(Map<String, Object> source, String fieldName) {
        log.debug("Mapping data for a field called " + fieldName);

        String result = null;

        if (mainFieldCanBeMapped(source, fieldName)) {
            result = getValue(source, fieldName);
        }

        log.debug("Result of DefaultMapper mapping for " + fieldName + "is " + result);

        return StringUtils.isNotEmpty(result) ? result : null;
    }
}
