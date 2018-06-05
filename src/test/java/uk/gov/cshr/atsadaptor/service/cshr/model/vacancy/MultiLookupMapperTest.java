package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link MultiLookupMapper}
 */
public class MultiLookupMapperTest extends AbstractMappingTest {
    private MultiLookupMapper mapper;

    @Before
    public void setup() {
        mapper = new MultiLookupMapper();
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void testMap_noSourceSupplied() {
        assertThat(mapper.map(null, "nghr_emp_type"), is(nullValue()));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        assertThat(mapper.map(new LinkedHashMap<>(), "nghr_emp_type"), is(nullValue()));
    }

    @Test
    public void testMap_notDisplayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_emp_type", false);

        assertThat(mapper.map(source, "nghr_emp_type"), is(nullValue()));
    }

    @Test
    public void testMap_displayed() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        toggleDisplayFieldValue(source, "nghr_emp_type", true);

        assertThat(mapper.map(source, "nghr_emp_type"), is(equalTo("Loan, Overseas")));
    }
}
