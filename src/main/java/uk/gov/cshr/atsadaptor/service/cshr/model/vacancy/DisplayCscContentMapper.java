package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class maps the value of the display property in the Applicant Tracking System's model of the
 * data for this field to a simple boolean
 */
@Component
@Slf4j
class DisplayCscContentMapper extends DataMapper {
    /**
     * Gets the value of the display property for the Display commissioners content field as a
     * boolean.
     *
     * @param source raw data from Applicant Tracking System used to extract and map
     * @return <code>true</code> if the value of field's display property is true otherwise <code>
     * false</code>
     */
    boolean map(Map<String, Object> source) {
        log.debug("Mapping a data for a CSC Content");

        boolean result = source != null && !source.isEmpty() && displayValue(source, "123_5070000");

        log.debug("Result of DisplayCscContentMapper mapping is " + result);

        return result;
    }
}
