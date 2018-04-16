package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.atsadaptor.service.cshr.DepartmentsService;

/**
 * Tests {@link AtsToCshrDataMapper}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AtsToCshrDataMapperTest extends AbstractMappingTest {
    @Inject
    private AtsToCshrDataMapper mapper;

    @MockBean
    private DepartmentsService departmentsService;

    @Test
    public void testMap_nullSourceSupplied() {
        assertThat(mapper.map(null), aMapWithSize(0));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        Map<String, Object> source = new HashMap<>();

        assertThat(mapper.map(source), aMapWithSize(0));
    }

    @Test
    public void testMap_vacancyNotLive() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData("/jobRequestResponses/nonLiveJobRequested.json");

        assertThat(mapper.map(source), aMapWithSize(0));
    }

    @Test
    public void testMap_tooManyVacanciesRequested() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData("/jobRequestResponses/tooManyJobsRequested.json");

        assertThat(mapper.map(source), aMapWithSize(0));
    }

    @Test
    @Ignore
    public void testMap_validJobRequest() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData(VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE);
        Map<String, Object> expected = getMappedVacancyAsJson(VALID_OVERSEAS_CSHR_VACANCY_RESPONSE);

        Map<String, Object> actual = mapper.map(source);

        for (String key : expected.keySet()) {
            assertThat("The values for key :" + key + " did not match", actual.get(key), is(equalTo(expected.get(key))));
        }

        assertThat(actual.size(), is(equalTo(expected.size())));
    }
}
