package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cshr.atsadaptor.exception.ExternalApplicantTrackingSystemException;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class is responsible for filtering the list of live jobs to produce a list of those that
 * have changed since the last time the active jobs list was obtained from an Applicant Tracking
 * System (ATS).
 */
@Component
@Slf4j
public class JobsListFilter {
    private String historyFileDirectory;
    private String historyFileName;

    public JobsListFilter(
            @Value("${ats.jobrun.history.directory}") String historyFileDirectory,
            @Value("${ats.jobrun.history.file}") String historyFileName) {
        this.historyFileDirectory = historyFileDirectory;
        this.historyFileName = historyFileName;
    }

    /**
     * This method is responsible for filtering the list of vacancies to return those that have
     * changed since the last time the process was run. The method will obtain the timestamp of the
     * last run from a file on the file system.
     * <p>
     * <p>If the file, called jobRunHistory.txt, does not exist the method treats this as having never
     * been run before and all the vacancies in the source list will be returned.
     * <p>
     * <p>Each vacancy contains a timestamp of when it was last changed.
     *
     * @param source the vacancies to be filtered.
     * @return List<VacancyListData> vacancies whose timestamp is greater than the timestamp logged
     * the last time the process was run.
     */
    public List<VacancyListData> filter(List<VacancyListData> source) {
        log.info(
                "Filtering vacancies that have changed since the last time this process was run");
        List<VacancyListData> filtered = new ArrayList<>();

        if (source != null && !source.isEmpty()) {
            Path path = FileSystems.getDefault().getPath(historyFileDirectory, historyFileName);

            if (path.toFile().exists()) {
                filtered = filterVacancies(source, path);
            } else {
                filtered = source;
            }
        }

        log.debug(
                "Found a total of " + filtered.size() + " jobs whose details are: " + filtered.toString());

        return filtered;
    }

    private List<VacancyListData> filterVacancies(List<VacancyListData> source, Path historyFile) {
        Timestamp lastRunDate;

        try {
            lastRunDate = new Timestamp(Files.getLastModifiedTime(historyFile).toMillis());
        } catch (IOException e) {
            CSHRServiceStatus status =
                    CSHRServiceStatus.builder()
                            .code(StatusCode.FILE_SYSTEM_ERROR.getCode())
                            .summary(
                                    "An error occurred trying to obtain the last modified time of "
                                            + historyFile.getFileName())
                            .build();

            throw new ExternalApplicantTrackingSystemException(status);
        }

        if (log.isDebugEnabled()) {
            log.debug("The last time this process ran was on :" + lastRunDate.toString());
        }

        return source
                .stream()
                .filter(v -> v.getVacancyTimestamp().after(lastRunDate))
                .collect(Collectors.toList());
    }
}
