package uk.gov.cshr.atsadaptor.service.cshr.model;

import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessStatistics {
    Integer numChanged;
    Integer numCreated;
    Integer numDeleted;
    Integer numErrors;
    Integer numProcessed;
    Long endTime;
    Long startTime;

    /**
     * This method is responsible for formatting the elapsed time derived from startTime and endTime in friendly text
     *
     * @return elapsed time derived from startTime and endTime in friendly text
     */
    public String formattedElapsedTime() {
        Long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        return DurationFormatUtils.formatDurationWords(duration, true, true);
    }
}
