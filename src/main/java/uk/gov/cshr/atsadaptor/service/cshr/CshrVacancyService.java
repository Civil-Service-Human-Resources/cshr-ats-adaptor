package uk.gov.cshr.atsadaptor.service.cshr;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.util.PathUtil;

/**
 * This implementation is for meeting the Applicant Tracking System's constraint of only allowing a
 * maximum request of 100 vacancies at a time.
 * <p>
 * <p>This implementation batches the work into units of that size or smaller
 */
@Service
@Slf4j
public class CshrVacancyService implements VacancyService {
    private static final String NUMBER_CREATED = "numberCreated";
    private static final String NUMBER_OF_ERRORS = "numberOfErrors";
    private static final String NUMBER_PROCESSED = "numberProcessed";
    private static final String NUMBER_SAVED = "numberSaved";

    private int atsRequestBatchSize;
    private String auditFileDirectory;
    private String auditFileBaseName;
    private String historyFileDirectory;
    private String historyFileName;

    private VacancyProcessor vacancyProcessor;

    public CshrVacancyService(VacancyProcessor vacancyProcessor,
                              @Value("${ats.request.batch.size:100}") String batchSize,
                              @Value("${cshr.jobrun.audit.directory}") String auditFileDirectory,
                              @Value("${cshr.jobrun.audit.basefilename}") String auditFileBaseName,
                              @Value("${ats.jobrun.history.directory}") String historyFileDirectory,
                              @Value("${ats.jobrun.history.file}") String historyFileName) {
        this.vacancyProcessor = vacancyProcessor;

        atsRequestBatchSize = Integer.valueOf(batchSize);
        if (atsRequestBatchSize > 100) {
            atsRequestBatchSize = 100;
        }

        this.auditFileBaseName = auditFileBaseName;
        this.auditFileDirectory = auditFileDirectory;

        this.historyFileDirectory = historyFileDirectory;
        this.historyFileName = historyFileName;
    }

    @Override
    public void processChangedVacancies(List<VacancyListData> changedVacancies) {
        log.info("Processing batches of vacancies that have changed since the last run.");
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put(NUMBER_PROCESSED, changedVacancies.size());
        statistics.put(NUMBER_CREATED, 0);
        statistics.put(NUMBER_SAVED, 0);
        statistics.put(NUMBER_OF_ERRORS, 0);

        Path path = createAuditFile();

        Iterables.partition(changedVacancies, atsRequestBatchSize)
                .forEach(batch -> vacancyProcessor.process(batch, path, statistics));

        createAuditEntry(path, statistics);

        updateLastJobHistoryFile();
    }

    private Path createAuditFile() {
        String fileName = auditFileBaseName
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + ".log";

        Path path = FileSystems.getDefault().getPath(auditFileDirectory, fileName);


        PathUtil.createFile(path);

        return path;
    }

    private void createAuditEntry(Path path, Map<String, Integer> statistics) {
        String entry = "A total of of "
                + statistics.get(NUMBER_PROCESSED)
                + " vacancies have been processed."
                + System.lineSeparator()
                + statistics.get(NUMBER_CREATED)
                + " vacancies were created in the CSHR data store."
                + System.lineSeparator()
                + statistics.get(NUMBER_SAVED)
                + " vacancies were saved in the CSHR data store."
                + System.lineSeparator()
                + statistics.get(NUMBER_OF_ERRORS)
                + " errors were reported.  See the log files for further details.";
        try {
            FileUtils.write(path.toFile(), entry, Charset.forName("UTF-8"), true);
        } catch (IOException e) {
            log.error("Error writing auditFileEntry for the results of processing those vacancies that have changed. The entry was " + entry, e);
        }
    }

    private void updateLastJobHistoryFile() {
        Path path = FileSystems.getDefault().getPath(historyFileDirectory, historyFileName);

        PathUtil.createFile(path);

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            FileUtils.write(path.toFile(), timestamp, Charset.forName("UTF-8"), true);
        } catch (IOException e) {
            log.error("Error writing last processed timestamp to " + path.getFileName(), e);
        }
    }
}
