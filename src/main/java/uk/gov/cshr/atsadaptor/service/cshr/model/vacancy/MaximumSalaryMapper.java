package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping the data for minimum salary to the CSHR data model.
 */
@Component
@Slf4j
class MaximumSalaryMapper extends DataMapper {
    private static final Integer MAX_NUMERIC_SALARY_VALUE = 100001;

    /**
     * Maps the data from the source to the CSHR Data model.
     * <p>
     * <p>Where it exists the maximum salary could be an array and if that is the case the largest
     * value in the array is to be used.
     * <p>
     * <p>The method will return if there is no data to map.
     *
     * @param source data from Applicant Tracking System containing data used to map the value of the
     *               maximum salary.
     * @return value of the maximum salary or null if there is no data
     */
    Integer map(Map<String, Object> source) {
        Integer result = null;

        if (source != null && !source.isEmpty()) {
            List<Map<String, Object>> salaryBands = getValue(source);
            Optional<Map<String, Object>> salaryBand =
                    salaryBands.stream().filter(this::isMaximumAllowedValue).findFirst();

            if (!salaryBand.isPresent()) {
                result = processBands(salaryBands);
            }
        }

        log.debug("Result of MaximumSalaryMapper mapping is " + result);

        return result;
    }

    private List<Map<String, Object>> getValue(Map<String, ?> source) {
        Map<String, Object> allFields = (Map<String, Object>) source.get(FIELD);
        Map<String, Object> field = (Map<String, Object>) allFields.get("nghr_salary");

        return (List<Map<String, Object>>) field.get(VALUE);
    }

    private boolean isMaximumAllowedValue(Map<String, Object> band) {
        String tmp = (String) band.get("text");
        tmp = tmp.trim();
        return tmp.contains("100,001 and above");
    }

    private Integer processBands(List<Map<String, Object>> salaryBands) {
        return salaryBands
                .stream()
                .map(this::extractRangeDelimiter)
                .max(Comparator.comparing(Integer::valueOf))
                .orElse(null);
    }

    private Integer extractRangeDelimiter(Map<String, Object> band) {
        log.debug("Mapping a data for maximum salary");

        Integer highest = null;

        String[] parts = ((String) band.get("text")).split(" ");

        if (parts.length > 0) {
            highest =
                    Arrays.stream(parts)
                            .filter(SalaryMapperUtil::isNumber)
                            .map(SalaryMapperUtil::convert)
                            .max(Comparator.comparing(Integer::valueOf))
                            .orElse(null);
        }

        return MAX_NUMERIC_SALARY_VALUE.compareTo(highest) > 0 ? highest : null;
    }
}
