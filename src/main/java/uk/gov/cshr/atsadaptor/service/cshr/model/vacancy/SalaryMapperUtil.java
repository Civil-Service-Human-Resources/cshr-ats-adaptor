package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * This class supplies some methods for working salary bands and verify and converting strings taken
 * from those bands into numbers.
 */
@Slf4j
public final class SalaryMapperUtil {
    private SalaryMapperUtil() {
    }

    /**
     * This method is responsible for verifying if a given string is a number or not after it has had
     * any characters associated with a salary removed, for example comma and £ sign.
     *
     * @param part string to evaluate
     * @return <code>true</code> if given string represents a number otherwise <code>false</code>
     */
    static boolean isNumber(String part) {
        part = StringUtils.removeAll(part, ",");
        part = StringUtils.removeAll(part, "£");

        return StringUtils.isNumeric(part);
    }

    /**
     * This method is responsible for converting String representing a number into an Integer.
     * <p>
     * <p>It will check if the given string is a number and return null if not.
     *
     * @param number value to convert to an Integer
     * @return value converted to an Integer or null if it cannot be converted.
     */
    static Integer convert(String number) {
        Integer result = null;

        if (isNumber(number)) {
            try {
                // getNumberInstance does not handle £. getCurrencyInstance is not what we need.
                number = number.replace("£", "");
                result = NumberFormat.getNumberInstance(Locale.UK).parse(number).intValue();
            } catch (ParseException e) {
                log.error("A parse exception occurred", e);
            }
        }

        return result;
    }
}
