package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping the Applicant Tracking System's data regarding benefits in
 * the CSHR data model.
 */
@Component
@Slf4j
class BenefitsMapper extends DataMapper {
    private static final String MAIN_FIELD = "046_5070000";

    /**
     * Maps the Applicant Tracking System's data regarding benefits to the CSRH data model.
     * <p>
     * <p>This data can consist of up to 3 fields from the Applicant Tracking System's data.
     * <p>
     * <p>If there is no data to be used the method will return null
     *
     * @param source raw data from Applicant Tracking System used to extract and map
     * @return the benefits that can be offered or null if there are none
     */
    String map(Map<String, Object> source) {
        log.debug("Mapping benefits");

        StringBuilder builder = new StringBuilder();

        if (mainFieldCanBeMapped(source, MAIN_FIELD)) {
            builder.append(getValue(source, MAIN_FIELD));

            addSubFieldIfRequired(source, builder, "222_5070000");
            addSubFieldIfRequired(source, builder, "221_5070000");
        }

        String result = builder.length() > 0 ? builder.toString() : null;

        log.debug("Result of BenefitsMapper mapping is " + result);

        return result;
    }

    private void addSubFieldIfRequired(
            Map<String, Object> source, StringBuilder builder, String fieldName) {
        if (displayValue(source, fieldName)) {
            builder.append(OPEN_PARA).append(getValue(source, fieldName)).append(CLOSE_PARA);
        }
    }
}
