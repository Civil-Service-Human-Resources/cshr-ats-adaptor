package uk.gov.cshr.atsadaptor.service.cshr;

import java.util.List;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;

/**
 * This implementation is for meeting the Applicant Tracking System's constraint of only allowing a
 * maximum request of 100 vacancies at a time.
 * <p>
 * <p>This implementation batches the work into units of that size or smaller
 */
@Service
@Slf4j
public class CshrVacancyService implements VacancyService {
    private int atsRequestBatchSize;

    private VacancyProcessor vacancyProcessor;

    public CshrVacancyService(
            VacancyProcessor vacancyProcessor, @Value("${ats.request.batch.size:100}") String batchSize) {
        atsRequestBatchSize = Integer.valueOf(batchSize);
        if (atsRequestBatchSize > 100) {
            atsRequestBatchSize = 100;
        }

        this.vacancyProcessor = vacancyProcessor;
    }

    @Override
    public void processChangedVacancies(List<VacancyListData> changedVacancies) {
        log.info("Processing batches of vacancies that have changed since the last run.");

        IntStream.range(0, (changedVacancies.size() + atsRequestBatchSize - 1) / atsRequestBatchSize)
                .mapToObj(
                        i ->
                                changedVacancies.subList(
                                        i * atsRequestBatchSize,
                                        Math.min(changedVacancies.size(), (i + 1) * atsRequestBatchSize)))
                .forEach(batch -> vacancyProcessor.process(batch));
    }
}
