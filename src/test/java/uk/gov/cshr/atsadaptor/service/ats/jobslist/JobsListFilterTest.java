package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.util.VacancyListDataBuilder;

/**
 * Tests {@link JobsListFilter}
 */
public class JobsListFilterTest {
    private List<VacancyListData> source;
    private JobsListFilter jobsListFilter = new JobsListFilter("./");

    @Before
    public void setup() throws ParseException {
        //Will contain four vacancies. See fourVacanciesList.json for actual content
        source = VacancyListDataBuilder.getInstance().buildExepctedVacancyListData();
    }

    @After
    public void tearDown() {
        source = null;
    }

    @Test
    public void testFilter_nullListSupplied() {
        assertThat(jobsListFilter.filter(null), is(empty()));
    }

    @Test
    public void testFilter_emptyListSupplied() {
        assertThat(jobsListFilter.filter(new ArrayList<>()), is(empty()));
    }

    @Test
    public void testFilter_firstTimeListIsUsed() {
        File historyFile = new File("./jobRunHistory.txt");
        FileUtils.deleteQuietly(historyFile);

        assertThat(jobsListFilter.filter(source).size(), is(equalTo(source.size())));
    }

    @Test
    public void testFilter_nothingChangedSinceLastRun() throws Exception {
        prepareHistoryFile(new Date().getTime());

        assertThat(jobsListFilter.filter(source), is(empty()));
    }

    private void prepareHistoryFile(Long lastAccessedTime) throws IOException {
        File historyFile = new File("./jobRunHistory.txt");

        if (historyFile.exists()) {
            FileUtils.deleteQuietly(historyFile);
        }

        historyFile.createNewFile();
        historyFile.setLastModified(lastAccessedTime);

        historyFile.deleteOnExit();
    }

    @Test
    public void testFilter_everythingHasChangedSinceLastRun() throws Exception {
        prepareHistoryFile(prepareADate(2018, 1, 1).getTime());

        assertThat(jobsListFilter.filter(source).size(), is(equalTo(source.size())));
    }

    private Date prepareADate(int year, int month, int day) {
        return Date.from(LocalDateTime.of(year, month, day, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testFilter_twoItemsHaveChangedSinceLastRun() throws Exception {
        prepareHistoryFile(prepareADate(2018, 3, 10).getTime());

        List<VacancyListData> expected = source.subList(0, 3);
        List<VacancyListData> actual = jobsListFilter.filter(source);
        assertThat(actual, is(equalTo(expected)));
    }
}
