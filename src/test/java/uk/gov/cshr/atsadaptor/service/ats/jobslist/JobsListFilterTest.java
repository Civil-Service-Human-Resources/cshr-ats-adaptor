package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
    private static final String HISTORY_DIRECTORY = "./";
    private static final String HISTORY_FILENAME = "jobRunHistory.txt";

    private List<VacancyListData> source;
    private JobsListFilter jobsListFilter = new JobsListFilter(HISTORY_DIRECTORY, HISTORY_FILENAME);

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
    public void testFilter_firstTimeListIsUsed() throws IOException {
        Path path = FileSystems.getDefault().getPath(HISTORY_DIRECTORY, HISTORY_FILENAME);

        Files.deleteIfExists(path);

        assertThat(jobsListFilter.filter(source).size(), is(equalTo(source.size())));
    }

    @Test
    public void testFilter_nothingChangedSinceLastRun() throws Exception {
        Path historyFile = prepareHistoryFile(new Date().getTime());

        assertThat(jobsListFilter.filter(source), is(empty()));

        Files.delete(historyFile);
    }

    private Path prepareHistoryFile(Long lastAccessedTime) throws IOException {
        Path path = FileSystems.getDefault().getPath(HISTORY_DIRECTORY, HISTORY_FILENAME);

        Files.deleteIfExists(path);

        Path historyFile = Files.createFile(path);

        LocalDateTime date = Instant.ofEpochMilli(lastAccessedTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String timestamp = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        FileUtils.write(path.toFile(), timestamp, Charset.forName("UTF-8"), true);

        return historyFile;
    }

    @Test
    public void testFilter_everythingHasChangedSinceLastRun() throws Exception {
        Path historyFile = prepareHistoryFile(prepareADate(1, 1).getTime());

        assertThat(jobsListFilter.filter(source).size(), is(equalTo(source.size())));

        Files.delete(historyFile);
    }

    private Date prepareADate(int month, int day) {
        return Date.from(LocalDateTime.of(2018, month, day, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testFilter_twoItemsHaveChangedSinceLastRun() throws Exception {
        Path historyFile = prepareHistoryFile(prepareADate(3, 10).getTime());

        List<VacancyListData> expected = source.subList(0, 3);
        expected.sort(Comparator.comparing(VacancyListData :: getVacancyTimestamp));
        List<VacancyListData> actual = jobsListFilter.filter(source);

        assertThat(actual, is(equalTo(expected)));

        Files.delete(historyFile);
    }
}
