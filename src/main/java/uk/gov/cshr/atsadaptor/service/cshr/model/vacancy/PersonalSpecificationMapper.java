package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for building the content that represents the personal specification
 * required for the vacancy.
 */
@Component
@Slf4j
class PersonalSpecificationMapper extends DataMapper {
    private static final String GRADE_ENTRY_FIELD_NAME = "099_5070000";
    private static final String LANGUAGES_FIELD_NAME = "113_5070000";
    private static final String LICENCES_FIELD_NAME = "111_5070000";
    private static final String PERSONAL_SPECIFICATION_FIELD_NAME = "148_5070000";
    private static final String MEMBERSHIPS_FIELD_NAME = "112_5070000";

    /**
     * This method maps the content of the personal specification text from one or more fields of data
     * in the Applicant Tracking System's data model to the CSHR Data model.
     *
     * @param source data from the Applicant Tracking System containing the fields to use to map the
     *               content.
     * @return content of the personal specification text from one or more fields of data from the
     * Applicant Tracking System's data model
     */
    String map(Map<String, Object> source) {
        log.debug("Mapping a data for personal specification");

        StringBuilder builder = new StringBuilder();

        if (mainFieldCanBeMapped(source, PERSONAL_SPECIFICATION_FIELD_NAME)) {
            builder.append(getValue(source, PERSONAL_SPECIFICATION_FIELD_NAME));

            addSubFieldIfRequired(source, builder, LICENCES_FIELD_NAME);

            addSubFieldIfRequired(source, builder, MEMBERSHIPS_FIELD_NAME);

            addSubFieldIfRequired(source, builder, LANGUAGES_FIELD_NAME);

            addSubFieldIfRequired(source, builder, GRADE_ENTRY_FIELD_NAME);
        }

        log.debug(
                "Result of PersonalSpecificationMapper mapping is "
                        + (builder.length() > 0 ? builder.toString() : null));

        return builder.length() > 0 ? builder.toString() : null;
    }

    private void addSubFieldIfRequired(
            Map<String, Object> source, StringBuilder builder, String fieldName) {
        if (displayValue(source, fieldName)) {
            builder
                    .append(BR)
                    .append(OPEN_BOLD)
                    .append(getLabel(source, fieldName))
                    .append(CLOSE_BOLD)
                    .append(BR)
                    .append(getValue(source, fieldName));
        }
    }
}
