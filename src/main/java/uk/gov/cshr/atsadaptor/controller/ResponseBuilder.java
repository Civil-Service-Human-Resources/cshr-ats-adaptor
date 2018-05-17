package uk.gov.cshr.atsadaptor.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import uk.gov.cshr.atsadaptor.service.cshr.model.ProcessStatistics;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class is responsible for building the response status when the process has completed successfully for a number
 * of different channels
 */
public final class ResponseBuilder {
    private static final String A_TOTAL_OF_OF = "A total of of ";
    private static final String NUMBER_OF_VACANCIES_CREATED = "Number of vacancies created in in the CSHR data store        : ";
    private static final String NUMBER_OF_VACANCIES_DELETED = "Number of vacancies marked as deleted in the CSHR data store : ";
    private static final String NUMBER_OF_VACANCIES_WITH_ERRORS = "Number of vacancies that had an error during processing      : ";
    private static final String NUMBER_OF_VACANCIES_SAVED = "Number of vacancies saved in the CSHR data store             : ";
    private static final String TOTAL_PROCESSING_TIME = "Total time taken to process the vacancies                    : ";
    private static final String VACANCIES_HAVE_CHANGED_TEXT = " vacancies have changed or became inactive in the ATS since the last run and were processed of which:";

    private ResponseBuilder() {}

    /**
     * Builds the instance of service status to be returned in a REST response.
     *
     * @param processStatistics totals of vacancies processed by category
     * @return nstance of service status to be returned in a REST response
     */
    static CSHRServiceStatus buildServiceStatus(ProcessStatistics processStatistics) {
        String summary;
        StatusCode code;

        Integer numberOfErrors = processStatistics.getNumErrors();
        if (numberOfErrors > 0) {
            code = StatusCode.PROCESS_COMPLETED_WITH_ERRORS;
            summary = "A request to load vacancies was received and completed successfully with at lease one error reported.";
        } else {
            code = StatusCode.PROCESS_COMPLETED;
            summary = "A request to load vacancies was received and completed successfully with no errors reported.";
        }

        return CSHRServiceStatus.builder()
                .code(code.getCode())
                .summary(summary)
                .detail(createDetails(processStatistics))
                .build();
    }

    private static List<String> createDetails(ProcessStatistics processStatistics) {
        List<String> details = new ArrayList<>();

        if (processStatistics.getNumProcessed().compareTo(0) > 0) {
            details.add(A_TOTAL_OF_OF + processStatistics.getNumProcessed() + VACANCIES_HAVE_CHANGED_TEXT);
            details.add(NUMBER_OF_VACANCIES_CREATED + processStatistics.getNumCreated());
            details.add(NUMBER_OF_VACANCIES_SAVED + processStatistics.getNumChanged());
            details.add(NUMBER_OF_VACANCIES_DELETED + processStatistics.getNumDeleted());
            details.add(NUMBER_OF_VACANCIES_WITH_ERRORS + processStatistics.getNumErrors());
        } else {
            details.add("No changes were found that required processing.");
        }

        details.add(TOTAL_PROCESSING_TIME + processStatistics.formattedElapsedTime());

        return details;
    }

    public static String buildLogEntry(ProcessStatistics processStatistics) {
        Integer numberOfErrors = processStatistics.getNumErrors();
        String separator = StringUtils.repeat("=", 138);
        String summary = numberOfErrors.compareTo(0) > 0
            ? "A request to load vacancies was received and completed successfully with at lease one error reported. Status code was " +
                StatusCode.PROCESS_COMPLETED_WITH_ERRORS.getCode()
            : "A request to load vacancies was received and completed successfully with no errors reported. Status code was " +
                StatusCode.PROCESS_COMPLETED.getCode();

        return separator
                + System.lineSeparator()
                + "SUMMARY OF VACANCY LOAD PROCESS"
                + System.lineSeparator()
                + separator
                + System.lineSeparator()
                + summary
                + System.lineSeparator()
                + System.lineSeparator()
                + A_TOTAL_OF_OF
                + processStatistics.getNumProcessed()
                + VACANCIES_HAVE_CHANGED_TEXT
                + System.lineSeparator()
                + NUMBER_OF_VACANCIES_CREATED
                + processStatistics.getNumCreated()
                + System.lineSeparator()
                + NUMBER_OF_VACANCIES_SAVED
                + processStatistics.getNumChanged()
                + System.lineSeparator()
                + NUMBER_OF_VACANCIES_DELETED
                + processStatistics.getNumDeleted()
                + System.lineSeparator()
                + NUMBER_OF_VACANCIES_WITH_ERRORS
                + numberOfErrors
                + System.lineSeparator()
                + TOTAL_PROCESSING_TIME
                + processStatistics.formattedElapsedTime();
    }
}
