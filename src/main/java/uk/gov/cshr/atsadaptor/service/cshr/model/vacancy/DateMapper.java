package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping Applicant Tracking System's closing date value to the CSRH
 * equivalent in the ISO 8061 standard
 */
@Component
@Slf4j
class DateMapper extends DataMapper {
    /**
     * Maps an Applicant Tracking System's date which is in the format of yyyy-MM-dd to the ISO 8061
     * standard.
     *
     * @param source    raw data from Applicant Tracking System used to extract and map
     * @param fieldName name of the field whose value is to be mapped to the ISO 8061 standard
     * @return date in the ISO 8061 standard or null if there is no date to map.
     */
    String map(Map<String, Object> source, String fieldName, boolean startOfDay) {
        log.debug("Mapping a date for a field called " + fieldName);

        String result = null;

        if (source != null && !source.isEmpty()) {
            String date = getValue(source, fieldName);

            if (StringUtils.isNotEmpty(date)) {
                LocalDate then = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDateTime dateTime = startOfDay ? then.atTime(LocalTime.MIDNIGHT) : then.atTime(LocalTime.MAX);

                result =
                        ZonedDateTime.of(dateTime, ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            }
        }

        log.debug("Result of DateMapper mapping for field " + fieldName + " is " + result);

        return result;
    }
}
