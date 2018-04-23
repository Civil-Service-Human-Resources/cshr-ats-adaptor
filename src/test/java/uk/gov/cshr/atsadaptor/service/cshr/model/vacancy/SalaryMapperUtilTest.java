package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Tests {@link SalaryMapperUtil}
 */
public class SalaryMapperUtilTest {
    @Test
    public void testIsNumeric_nullSupplied() {
        assertThat(SalaryMapperUtil.isNumber(null), is(false));
    }

    @Test
    public void testIsNumeric_alphaSupplied() {
        assertThat(SalaryMapperUtil.isNumber("foo"), is(false));
    }

    @Test
    public void testIsNumeric_alphaNumericSupplied() {
        assertThat(SalaryMapperUtil.isNumber("fo123o"), is(false));
    }

    @Test
    public void testIsNumeric_numericSupplied() {
        assertThat(SalaryMapperUtil.isNumber("123"), is(true));
    }

    @Test
    public void testConvert_nullSupplied() {
        assertThat(SalaryMapperUtil.convert(null), is(nullValue()));
    }

    @Test
    public void testConvert_nonNumericSupplied() {
        assertThat(SalaryMapperUtil.convert("foo"), is(nullValue()));
    }

    @Test
    public void testConvert_numericSupplied() {
        assertThat(SalaryMapperUtil.convert("123"), is(equalTo(123)));
    }
}
