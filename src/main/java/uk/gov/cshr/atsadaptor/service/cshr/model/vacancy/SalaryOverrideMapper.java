package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for deriving the value of the salary override text.
 */
@Component
@Slf4j
class SalaryOverrideMapper extends DataMapper {
    private static final String MAIN_FIELD = "nghr_min_salary";
    private static final String NGHR_MAX_SALARY = "nghr_max_salary";

    /**
     * Gets the value of the salary override text.
     * <p>
     * <p>The value is derived from two values in the Applicant Tracking System's data model. One of
     * the values is mandatory so the format of the salary override text can take one of two forms:
     * <p>
     * <p>Either: 'text' or 'text' - 'text'
     * <p>
     * <p>The method could also return null if the value cannot be derived.
     *
     * @param source raw data from Applicant Tracking System used to extract and map
     * @return the salary override text derived from two values in the Applicant Tracking System's
     * data model or null
     */
    String map(Map<String, Object> source) {
        log.debug("Mapping a data for salary override");

        String result = null;

        if (mainFieldCanBeMapped(source, MAIN_FIELD)) {
            StringBuilder builder = new StringBuilder();

            builder.append(getValue(source, MAIN_FIELD));

            if (displayValue(source, NGHR_MAX_SALARY)) {
                builder.append(" - ").append(getValue(source, NGHR_MAX_SALARY));
            }

            result = builder.toString();
        }

        log.debug(
                "Result of SalaryOverrideMapper mapping is "
                        + (StringUtils.isNotEmpty(result) ? result : null));

        return StringUtils.isNotEmpty(result) ? result : null;
    }
}
