package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * This is responsible for extracting the information on how to apply for the vacancy.
 */
@Component
@Slf4j
class LinkToApplyMapper extends DataMapper {

    private static final String MAIN_FIELD = "vac_template";

    /**
     * This method gets the information on how to apply for the vacancy.
     * <p>
     * <p>The vacancy may have a link to apply online or if it is an advert only vacancy there will be
     * a different link that does not start an application process.
     *
     * @param source data from Applicant Tracking System containing data used to map 'link to apply'
     *               value
     * @return the link to apply value or null if there is no data
     */
    String map(Map<String, Object> source) {
        log.debug("Mapping a data for a link to apply");

        String result = null;

        if (source != null && !source.isEmpty()) {
            String fieldName = vacancyHasApplyOnlineLink(source) ? "v9applyurl" : "115_5070000";

            result = getValue(source, fieldName);
        }

        log.debug(
                "Result of LinkToApplyMapper mapping is "
                        + (StringUtils.isNotEmpty(result) ? result : null));

        return StringUtils.isNotEmpty(result) ? result : null;
    }

    private boolean vacancyHasApplyOnlineLink(Map<String, Object> source) {
        String vacancyTemplateValue = getValue(source, MAIN_FIELD);

        return "1478".equals(vacancyTemplateValue);
    }
}
