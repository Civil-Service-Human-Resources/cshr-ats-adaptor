package uk.gov.cshr.atsadaptor.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.gov.cshr.atsadaptor.service.cshr.model.StatisticsKeyNames;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class is responsible for building the response status when the process has completed successfully for a number
 * of different channels
 */
public final class ResponseBuilder {
    private ResponseBuilder() {}

    /**
     * Builds the instance of service status to be returned in a REST response.
     *
     * @param statistics totals of vacancies processed by category
     * @return nstance of service status to be returned in a REST response
     */
    static CSHRServiceStatus buildServiceStatus(Map<String, Integer> statistics) {
        String summary;
        StatusCode code;

        if (statistics.get(StatisticsKeyNames.NUMBER_OF_ERRORS).compareTo(0) > 0) {
            code = StatusCode.PROCESS_COMPLETED_WITH_ERRORS;
            summary = "A request to load vacancies was received and completed successfully with at lease one error reported.";
        } else {
            code = StatusCode.PROCESS_COMPLETED;
            summary = "A request to load vacancies was received and completed successfully with no errors reported.";
        }

        return CSHRServiceStatus.builder()
                .code(code.getCode())
                .summary(summary)
                .detail(createDetails(statistics))
                .build();
    }

    private static List<String> createDetails(Map<String, Integer> statistics) {
        List<String> details = new ArrayList<>();

        details.add("A total of of " +
                statistics.get(StatisticsKeyNames.NUMBER_PROCESSED) +
                " vacancies have changed or became inactive in the ATS since the last run and were processed of which:");
        details.add("Number of vacancies created in in the CSHR data store        : " +
                statistics.get(StatisticsKeyNames.NUMBER_CREATED));
        details.add("Number of vacancies saved in the CSHR data store             : " +
                statistics.get(StatisticsKeyNames.NUMBER_SAVED));
        details.add("Number of vacancies marked as deleted in the CSHR data store : " +
                statistics.get(StatisticsKeyNames.NUMBER_DELETED));
        details.add("Number of vacancies that had an error during processing      : " +
                statistics.get(StatisticsKeyNames.NUMBER_OF_ERRORS));

        return details;
    }

    static String buildLogEntry(Map<String, Integer> statistics) {
        String summary = statistics.get(StatisticsKeyNames.NUMBER_OF_ERRORS).compareTo(0) > 0
            ? "A request to load vacancies was received and completed successfully with at lease one error reported. Status code was " +
                StatusCode.PROCESS_COMPLETED_WITH_ERRORS.getCode()
            : "A request to load vacancies was received and completed successfully with no errors reported. Status code was " +
                StatusCode.PROCESS_COMPLETED.getCode();

        return summary
                + System.lineSeparator()
                + "A total of of "
                + statistics.get(StatisticsKeyNames.NUMBER_PROCESSED)
                + " vacancies have changed or became inactive in the ATS since the last run and were processed of which:"
                + System.lineSeparator()
                + "Number of vacancies created in in the CSHR data store        : "
                + statistics.get(StatisticsKeyNames.NUMBER_CREATED)
                + System.lineSeparator()
                + "Number of vacancies saved in the CSHR data store             : "
                + statistics.get(StatisticsKeyNames.NUMBER_SAVED)
                + System.lineSeparator()
                + "Number of vacancies marked as deleted in the CSHR data store : "
                + statistics.get(StatisticsKeyNames.NUMBER_DELETED)
                + System.lineSeparator()
                + "Number of vacancies that had an error during processing      : "
                + statistics.get(StatisticsKeyNames.NUMBER_OF_ERRORS);
    }
}
