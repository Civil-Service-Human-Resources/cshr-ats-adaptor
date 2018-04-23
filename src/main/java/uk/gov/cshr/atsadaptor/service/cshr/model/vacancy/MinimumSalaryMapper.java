package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping the data for minimum salary to the CSHR data model.
 */
@Component
@Slf4j
class MinimumSalaryMapper extends DataMapper {
    /**
     * Maps the data from the source to the CSHR Data model.
     * <p>
     * <p>Where it exists the minimum salary could be an array and if that is the case the smallest
     * value in the array is to be used.
     * <p>
     * <p>The method will return if there is no data to map.
     *
     * @param source data from Applicant Tracking System containing data used to map the value of the
     *               minimum salary.
     * @return value of the minimum salary or null if there is no data
     */
    Integer map(Map<String, Object> source) {
        log.debug("Mapping a data for minimum salary");

        Integer result = null;

        if (source != null && !source.isEmpty()) {
            List<Map<String, Object>> salaryBands = getValue(source);
            result =
                    salaryBands
                            .stream()
                            .map(this::extractRangeDelimiter)
                            .min(Comparator.comparing(Integer::valueOf))
                            .orElse(null);
        }

        log.debug("Result of MinimumSalaryMapper mapping is " + result);

        return result;
    }

    private List<Map<String, Object>> getValue(Map<String, ?> source) {
        Map<String, Object> allFields = (Map<String, Object>) source.get(FIELD);
        Map<String, Object> field = (Map<String, Object>) allFields.get("nghr_salary");

        return (List<Map<String, Object>>) field.get(VALUE);
    }

    private Integer extractRangeDelimiter(Map<String, Object> band) {
        Integer lowest = null;

        String[] parts = ((String) band.get("text")).split(" ");

        if (parts.length > 0) {
            lowest =
                    Arrays.stream(parts)
                            .filter(SalaryMapperUtil::isNumber)
                            .map(SalaryMapperUtil::convert)
                            .min(Comparator.comparing(Integer::valueOf))
                            .orElse(null);
        }

        return lowest;
    }
}
