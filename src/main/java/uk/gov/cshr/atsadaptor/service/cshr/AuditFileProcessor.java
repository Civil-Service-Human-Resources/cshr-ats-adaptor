package uk.gov.cshr.atsadaptor.service.cshr;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.cshr.atsadaptor.controller.ResponseBuilder;
import uk.gov.cshr.atsadaptor.service.cshr.model.ProcessStatistics;
import uk.gov.cshr.atsadaptor.service.util.PathUtil;
import uk.gov.cshr.status.CSHRServiceStatus;

@Component
@Slf4j
public class AuditFileProcessor {
    private static final String HYPHEN = " - ";
    private static final String NA = "N/a";
    private static final String SEPARATOR = StringUtils.repeat("=", 138);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String UNKNOWN = "unknown";

    @Value("${cshr.jobrun.audit.directory}")
    private String auditFileDirectory;
    @Value("${cshr.jobrun.audit.basefilename}")
    private String auditFileBaseName;

    public Path createInitialAuditEntry() {
        Path path = createAuditFile();

        String entry = SEPARATOR
                + System.lineSeparator()
                + "Date\t\t\tJobRef\t\tResult\t\tHttp Status\tCSHR Service Status code\tCSHR Service Status Summary"
                + System.lineSeparator()
                + SEPARATOR
                + System.lineSeparator();

        writeAuditFileEntry(path, entry);

        return path;
    }

    public void addAuditFileEntry(Path path, String jobRef, ResponseEntity<CSHRServiceStatus> response) {
        StringBuilder output = new StringBuilder();

        output.append(createTimestampFragment())
                .append("\t")
                .append(jobRef)
                .append("\t\tSUCCESS\t\t")
                .append(response.getStatusCodeValue())
                .append("\t\t")
                .append(response.getBody().getCode())
                .append("\t\t\t\t")
                .append(response.getBody().getSummary());

        if (response.getBody().getDetail() != null && !response.getBody().getDetail().isEmpty()) {
            output.append(HYPHEN);

            response.getBody().getDetail().forEach(output :: append);
        }

        output.append(System.lineSeparator()).append(System.lineSeparator());

        writeAuditFileEntry(path, jobRef, output.toString());
    }

    private String createTimestampFragment() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    public void addExceptionEntry(Path path, String jobRef, CSHRServiceStatus serviceStatus) {
        StringBuilder output = new StringBuilder();

        output.append(createTimestampFragment()).append("\t");

        if (jobRef != null) {
            output.append(jobRef);
        } else {
            output.append(UNKNOWN);
        }

        output.append("\t\tERROR\t\tN/a\t\t")
                .append(serviceStatus.getCode())
                .append("\t\t\t")
                .append(serviceStatus.getSummary())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        writeAuditFileEntry(path, jobRef, output.toString());
    }

    public void addExceptionEntry(Path path, String jobRef, String summary) {
        StringBuilder output = new StringBuilder();

        output.append(createTimestampFragment()).append("\t");

        if (jobRef != null) {
            output.append(jobRef);
        } else {
            output.append(UNKNOWN);
        }

        output.append("\t\tERROR\t\tN/a\t\t")
                .append(summary)
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        writeAuditFileEntry(path, jobRef, output.toString());
    }

    public void addExceptionEntry(Path path, String jobRef, HttpClientErrorException hcee) {
        StringBuilder output = new StringBuilder();

        output.append(createTimestampFragment()).append("\t");

        if (jobRef != null) {
            output.append(jobRef);
        } else {
            output.append(UNKNOWN);
        }

        output.append("\t\tERROR\t\t")
                .append(hcee.getRawStatusCode())
                .append("\t\t");

        CSHRServiceStatus serviceStatus = extractServiceStatusInfo(hcee);
        output.append(serviceStatus.getCode())
                .append("\t\t\t")
                .append(serviceStatus.getSummary());

        output.append(System.lineSeparator())
            .append(System.lineSeparator());

        writeAuditFileEntry(path, jobRef, output.toString());
    }

    private CSHRServiceStatus extractServiceStatusInfo(HttpClientErrorException hcee) {
        CSHRServiceStatus serviceStatus = null;

        if (hcee.getResponseBodyAsString() != null) {
            try {
                serviceStatus = new Gson().fromJson(hcee.getResponseBodyAsString(), CSHRServiceStatus.class);
            } catch (Exception ex) {
                serviceStatus = CSHRServiceStatus.builder().code(NA).summary(hcee.getResponseBodyAsString()).build();
            }
        }

        if (serviceStatus == null) {
            serviceStatus = CSHRServiceStatus.builder().code(NA).summary(hcee.getMessage()).build();
        }

        return serviceStatus;
    }

    public void addLoadVacancyErrorEntry(Path path, String id, ResponseEntity<Map> response) {
        StringBuilder output = new StringBuilder();

        output.append(createTimestampFragment())
                .append("\t")
                .append(id)
                .append("\t\tERROR\t\t")
                .append(response.getStatusCodeValue())
                .append("\t\tThe vacancy could not be loaded from the CSHR Data store.");

        if (response.getStatusCode().getReasonPhrase() != null) {
            output.append(" ")
                    .append(response.getStatusCode().getReasonPhrase())
                    .append(". ");
        }

        output.append(System.lineSeparator()).append(System.lineSeparator());

        writeAuditFileEntry(path, id, output.toString());
    }

    public void writeAuditFileEntry(Path auditFile, String jobRef, String auditFileEntry) {
        try {
            FileUtils.write(auditFile.toFile(), auditFileEntry, Charset.forName("UTF-8"), true);
        } catch (IOException e) {
            log.error("Error writing auditFileEntry for jobRef " + jobRef + " to a file called "
                    + auditFile.getFileName() + ". The content of the entry was: " + auditFileEntry, e);
        }
    }

    private void writeAuditFileEntry(Path auditFile, String auditFileEntry) {
        writeAuditFileEntry(auditFile, null, auditFileEntry);
    }

    public void addFinalAuditEntry(Path path, ProcessStatistics processStatistics) {
        Integer numProcessed = processStatistics.getNumProcessed();
        Integer numDeleted = processStatistics.getNumDeleted();

        processStatistics.setNumProcessed(numProcessed + numDeleted);

        processStatistics.setEndTime(System.nanoTime());

        String entry = ResponseBuilder.buildLogEntry(processStatistics);

        writeAuditFileEntry(path, entry);
    }

    private Path createAuditFile() {
        String fileName = auditFileBaseName
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + ".log";

        Path path = FileSystems.getDefault().getPath(auditFileDirectory, fileName);

        PathUtil.createFileIfRequired(path);

        return path;
    }

    public void addDeleteHeaderSummary(Path path) {
        String entry = SEPARATOR
                + System.lineSeparator()
                + "Vacancies That Are No Longer Active And Have Been Marked as Active = false in the CSHR Vacancy Data Store"
                + System.lineSeparator()
                + SEPARATOR
                + System.lineSeparator();

        writeAuditFileEntry(path, "N/a", entry);
    }
}
