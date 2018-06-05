package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping the data for the Nationality Statement out of data supplied
 * in the Applicant Tracking System data model.
 */
@Component
@Slf4j
class NationalStatementMapper extends DataMapper {
    private static final String NOT_APPLICABLE = "NONE";

    /**
     * Maps the data from the source to the CSHR Data model.
     * <p>
     * <p>The method will return if there is no data to map.
     *
     * @param source data from Applicant Tracking System containing data used to derive the value of
     *               the Nationality Statement which is a numeric value. The mapper converts this to friendly
     *               text.
     * @return friendly text version of the Nationality Statement or null if there is no data
     */
    String map(Map<String, Object> source) {
        log.debug("Mapping a data for nationality statement");

        String result = null;

        if (source != null) {
            try {
                String value = getValue(source);

                if ("167681".equals(value)) {
                    result = "NON_RESERVED";
                } else if ("167682".equals(value)) {
                    result = "RESERVED";
                } else {
                    result = NOT_APPLICABLE;
                }
            } catch (NullPointerException npe) {
                result = NOT_APPLICABLE;
            }
        }

        log.debug("Result of NationalStatementMapper mapping is " + result);

        return result;
    }

    private String getValue(Map<String, Object> source) {
        Map<String, Object> allFields = (Map<String, Object>) source.get(FIELD);
        Map<String, Object> field = (Map<String, Object>) allFields.get("nghr_reserve");
        Map<String, Object> value = (Map<String, Object>) field.get(VALUE);

        return (String) value.get("code");
    }
}
