package uk.gov.cshr.atsadaptor.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import uk.gov.cshr.atsadaptor.service.cshr.model.StatisticsKeyNames;
import uk.gov.cshr.status.CSHRServiceStatus;

/**
 * Tests {@link ResponseBuilder}
 */
public class ResponseBuilderTest {
    @Test
    public void testBuildServiceStatus_noErrors() {
        Map<String, Integer> stats = createStats(5, 0);
        List<String> details = new ArrayList<>();
        details.add("A total of of 11 vacancies have changed or became inactive in the ATS since the last run and were processed of which:");
        details.add("Number of vacancies created in in the CSHR data store        : 5");
        details.add("Number of vacancies saved in the CSHR data store             : 5");
        details.add("Number of vacancies marked as deleted in the CSHR data store : 1");
        details.add("Number of vacancies that had an error during processing      : 0");

        CSHRServiceStatus expected = CSHRServiceStatus.builder()
                .code("CSHR_20")
                .summary("A request to load vacancies was received and completed successfully with no errors reported.")
                .detail(details)
                .build();

        assertThat(ResponseBuilder.buildServiceStatus(stats), is(equalTo(expected)));
    }

    private Map<String, Integer> createStats(int numSaved, int numErrors) {
        Map<String, Integer> stats = new HashMap<>();

        stats.put(StatisticsKeyNames.NUMBER_PROCESSED, 11);
        stats.put(StatisticsKeyNames.NUMBER_CREATED, 5);
        stats.put(StatisticsKeyNames.NUMBER_SAVED, numSaved);
        stats.put(StatisticsKeyNames.NUMBER_DELETED, 1);
        stats.put(StatisticsKeyNames.NUMBER_OF_ERRORS, numErrors);

        return stats;
    }

    @Test
    public void testBuildServiceStatus_withErrors() {
        Map<String, Integer> stats = createStats( 4, 1);
        List<String> details = new ArrayList<>();
        details.add("A total of of 11 vacancies have changed or became inactive in the ATS since the last run and were processed of which:");
        details.add("Number of vacancies created in in the CSHR data store        : 5");
        details.add("Number of vacancies saved in the CSHR data store             : 4");
        details.add("Number of vacancies marked as deleted in the CSHR data store : 1");
        details.add("Number of vacancies that had an error during processing      : 1");

        CSHRServiceStatus expected = CSHRServiceStatus.builder()
                .code("CSHR_21")
                .summary("A request to load vacancies was received and completed successfully with at lease one error reported.")
                .detail(details)
                .build();

        assertThat(ResponseBuilder.buildServiceStatus(stats), is(equalTo(expected)));
    }

    @Test
    public void testBuildLogEntry_noErrors() {
        Map<String, Integer> stats = createStats(5, 0);

        String expected = "A request to load vacancies was received and completed successfully with no errors reported. Status code was CSHR_20\n" +
                "A total of of 11 vacancies have changed or became inactive in the ATS since the last run and were processed of which:\n" +
                "Number of vacancies created in in the CSHR data store        : 5\n" +
                "Number of vacancies saved in the CSHR data store             : 5\n" +
                "Number of vacancies marked as deleted in the CSHR data store : 1\n" +
                "Number of vacancies that had an error during processing      : 0";

        assertThat(ResponseBuilder.buildLogEntry(stats), is(equalTo(expected)));
    }

    @Test
    public void testBuildLogEntry_withErrors() {
        Map<String, Integer> stats = createStats(4, 1);

        String expected = "A request to load vacancies was received and completed successfully with at lease one error reported. Status code was CSHR_21\n" +
                "A total of of 11 vacancies have changed or became inactive in the ATS since the last run and were processed of which:\n" +
                "Number of vacancies created in in the CSHR data store        : 5\n" +
                "Number of vacancies saved in the CSHR data store             : 4\n" +
                "Number of vacancies marked as deleted in the CSHR data store : 1\n" +
                "Number of vacancies that had an error during processing      : 1";

        assertThat(ResponseBuilder.buildLogEntry(stats), is(equalTo(expected)));
    }
}
