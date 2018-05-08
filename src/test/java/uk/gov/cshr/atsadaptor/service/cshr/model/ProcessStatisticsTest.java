package uk.gov.cshr.atsadaptor.service.cshr.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link ProcessStatistics}
 */
public class ProcessStatisticsTest {
    private ProcessStatistics processStatistics;

    @Before
    public void setup() {
        processStatistics = new ProcessStatistics();
    }

    @After
    public void tearDown() {
        processStatistics = null;
    }

    @Test(expected = NullPointerException.class)
    public void formattedElapsedTime_startTimeIsNull() {

        processStatistics.setEndTime(System.nanoTime());

        processStatistics.formattedElapsedTime();
    }

    @Test(expected = NullPointerException.class)
    public void formattedElapsedTime_endTimeIsNull() {

        processStatistics.setStartTime(System.nanoTime());

        processStatistics.formattedElapsedTime();
    }

    @Test
    public void formattedElapsedTime_timesExist() {
        processStatistics.setStartTime(System.nanoTime());
        processStatistics.setEndTime(processStatistics.startTime + 7655678538765L);

        assertThat(processStatistics.formattedElapsedTime(), is(equalTo("2 hours 7 minutes 35 seconds")));
    }
}
