package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;

/**
 * This class is responsible for filtering the list of live jobs to produce a list of those that have changed since the
 * last time the active jobs list was obtained from an Applicant Tracking System (ATS).
 */
@Component
@Slf4j
public class JobsListFilter {
    private String jobRunHistoryDirectory;

    public JobsListFilter(@Value("ats.jobrun.history.directory") String jobRunHistoryDirectory) {
        this.jobRunHistoryDirectory = jobRunHistoryDirectory;
    }

    /**
     * This method is responsible for filtering the list of vacancies to return those that have changed since the last
     * time the process was run.  The method will obtain the timestamp of the last run from a file on the file system.
     * <p>
     * If the file, called jobRunHistory.txt, does not exist the method treats this as having never been run before and
     * all the vacancies in the source list will be returned.
     * <p>
     * Each vacancy contains a timestamp of when it was last changed.
     *
     * @param source the vacancies to be filtered.
     * @return List<VacancyListData> vacancies whose timestamp is greater than the timestamp logged the last time the
     * process was run.
     */
    public List<VacancyListData> filter(List<VacancyListData> source) {
        log.debug("Starting to filter list for those vacancies that have changed since the last time this process was run");
        List<VacancyListData> filtered = new ArrayList<>();

        if (source != null && !source.isEmpty()) {
            File historyFile = new File(jobRunHistoryDirectory + "/jobRunHistory.txt");

            if (historyFile.exists()) {
                filtered = filterVacancies(source, historyFile);
            } else {
                filtered = source;
            }
        }


        return filtered;
    }

    private List<VacancyListData> filterVacancies(List<VacancyListData> source, File historyFile) {
        Timestamp lastRunDate = new Timestamp(historyFile.lastModified());

        if (log.isDebugEnabled()) {
            log.debug("The last time this process ran was on :" + lastRunDate.toString());
        }

        return source.stream().filter(v -> v.getVacancyTimestamp().after(lastRunDate)).collect(Collectors.toList());
    }
}
