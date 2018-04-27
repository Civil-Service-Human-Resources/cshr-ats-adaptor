package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping the correct value for Overseas job
 */
@Component
@Slf4j
class OverseasJobMapper extends DataMapper {
    private static final String MAIN_FIELD = "location_method";

    /**
     * Maps the location type and returns true if it has a value of 'OVERSEAS' otherwise false.
     *
     * @param source data from Applicant Tracking System containing data used to derive the value
     * @return <code>true</code> if it has a value of 'OVERSEAS' otherwise <code>false</code>
     */
    boolean map(Map<String, Object> source) {
        log.debug("Mapping a data for whether a job is overseas or not");

        boolean result = false;

        if (source != null && !source.isEmpty()) {
            result = isOverseasJob(source);
        }

        log.debug("Result of OverseasJobMapper mapping is " + result);

        return result;
    }

    boolean isOverseasJob(Map<String, Object> source) {
        String locationType = getValue(source, MAIN_FIELD);
        return "OVERSEAS".equalsIgnoreCase(locationType);
    }
}
