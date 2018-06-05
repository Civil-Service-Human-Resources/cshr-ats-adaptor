package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests {@link AtsToCshrDataMapper}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AtsToCshrDataMapperTest extends AbstractMappingTest {
    @Inject
    private AtsToCshrDataMapper mapper;

    @Test
    public void testMap_nullSourceSupplied() {
        assertThat(mapper.map(null, true), aMapWithSize(0));
    }

    @Test
    public void testMap_emptySourceSupplied() {
        Map<String, Object> source = new HashMap<>();

        assertThat(mapper.map(source, true), aMapWithSize(0));
    }

    @Test
    public void testMap_vacancyNotLive() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData("/jobRequestResponses/nonLiveJobRequested.json");

        assertThat(mapper.map(source, true), aMapWithSize(0));
    }

    @Test
    public void testMap_tooManyVacanciesRequested() throws IOException {
        Map<String, Object> source = getAtsSourceResponseData("/jobRequestResponses/tooManyJobsRequested.json");

        assertThat(mapper.map(source, true), aMapWithSize(0));
    }
}
