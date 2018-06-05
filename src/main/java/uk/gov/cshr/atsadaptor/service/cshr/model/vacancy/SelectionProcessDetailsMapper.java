package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping data from the Applicant Tracking System model to the CSHR
 * data model to represent the selection process details for the vacancy.
 */
@Component
@Slf4j
class SelectionProcessDetailsMapper extends DataMapper {
    private static final String MAIN_FIELD = "047_5070000";
    private static final String PROCESS_DETAILS_FIELD = "150_5070000";
    private static final String TEST_INSTRUCTIONS_FIELD = "180_5070000";
    private static final String TRAVEL_COSTS_FIELD = "152_5070000";

    /**
     * Maps the data from the source to the CSHR Data model.
     *
     * @param source data from Applicant Tracking System containing data used to derive the value of
     *               the selection process details for the vacancy.
     * @return selection process details for the vacancy
     */
    String map(Map<String, Object> source) {
        log.debug("Mapping a data for selection process details");

        StringBuilder builder = new StringBuilder();

        if (mainFieldCanBeMapped(source, MAIN_FIELD)) {
            builder.append(getValue(source, MAIN_FIELD));

            addSubFieldIfRequired(source, builder, TRAVEL_COSTS_FIELD);

            addSubFieldIfRequired(source, builder, TEST_INSTRUCTIONS_FIELD);

            addSubFieldIfRequired(source, builder, PROCESS_DETAILS_FIELD);
        }

        log.debug(
                "Result of SelectionProcessDetailsMapper mapping is "
                        + (builder.length() > 0 ? builder.toString() : null));

        return builder.length() > 0 ? builder.toString() : null;
    }

    private void addSubFieldIfRequired(
            Map<String, Object> source, StringBuilder builder, String fieldName) {
        if (displayValue(source, fieldName)) {
            String value = getValue(source, fieldName);
            if (StringUtils.isNotEmpty(value)) {
                builder.append(OPEN_PARA).append(value).append(CLOSE_PARA);
            }
        }
    }
}
