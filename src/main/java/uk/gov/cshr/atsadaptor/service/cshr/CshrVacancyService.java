package uk.gov.cshr.atsadaptor.service.cshr;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.cshr.model.ProcessStatistics;

/**
 * This implementation is for meeting the Applicant Tracking System's constraint of only allowing a  maximum request of
 * 100 vacancies at a time.
 * <p>
 * <p>This implementation batches the work into units of that size or smaller
 */
@Service
@Slf4j
public class CshrVacancyService implements VacancyService {
    private static final String IDENTIFIER = "identifier";

    private DepartmentsService departmentsService;
    private VacancyProcessor vacancyProcessor;
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${ats.request.batch.size:100}")
    private int atsRequestBatchSize;
    @Value("${cshr.ats.vendor.id}")
    private String atsVendorId;
    @Value("${cshr.api.service.vacancy.save.username}")
    private String cshrApiUsername;
    @Value("${cshr.api.service.vacancy.save.password}")
    private String cshrApiPassword;
    @Value("${cshr.api.service.vacancy.findAll.endpoint}")
    private String findAllVacanciesEndpoint;

    private RestTemplate cshrRestTemplate;

    public CshrVacancyService(VacancyProcessor vacancyProcessor, RestTemplateBuilder restTemplateBuilder, DepartmentsService departmentsService) {
        this.vacancyProcessor = vacancyProcessor;
        this.restTemplateBuilder = restTemplateBuilder;
        this.departmentsService = departmentsService;
    }

    @PostConstruct
    public void init() {
        if (atsRequestBatchSize > 100) {
            atsRequestBatchSize = 100;
        }

        this.cshrRestTemplate = restTemplateBuilder.basicAuthorization(cshrApiUsername, cshrApiPassword).build();
    }

    @Override
    public List<String> processChangedVacancies(List<VacancyListData> changedVacancies, List<String> jobsNoLongerActive,
                                                Path auditFilePath, ProcessStatistics processStatistics) {
        log.info("Processing batches of vacancies that have changed since the last run.");

        Iterables.partition(changedVacancies, atsRequestBatchSize)
                .forEach(batch -> vacancyProcessor.process(batch, jobsNoLongerActive, auditFilePath, processStatistics));

        departmentsService.evictCache();

        return jobsNoLongerActive;
    }

    /**
     * This method is responsible for performing a soft delete of vacancies that are not active.
     *
     * The list of liveJobs only contains those that are in the ATS that are active/live.
     *
     * To determine if a job should be closed it must meet the following rules:
     * <pre>
     * <ul>
     *     <li>Vacancy with an identifier and atsVendorId for this ATS is active in CSHR data store</li>
     *     <li>The identifier for the vacancy is not in the list of live jobs</li>
     * </ul>
     * </pre>
     *
     * The method will set the active flag for each vacancy that meets the above rules to <code>false</code> and save
     * it in the CSHR data store.
     * @param liveJobs the list of jobs that are live in the ATS.
     */
    public void deleteNonActiveVacancies(List<VacancyListData> liveJobs, List<String> jobsNoLongerActive,
                                         Path auditFilePath, ProcessStatistics processStatistics) {
        log.info("Starting to search for vacancies that are no longer active in the ATS");
        boolean inProgress = true;

        Set<String> liveVacancyIdentifiers = liveJobs.stream().map(VacancyListData :: getJcode).collect(Collectors.toSet());
        liveVacancyIdentifiers.removeAll(jobsNoLongerActive);

        Map<String, String> params = new HashMap<>();
        params.put("size", String.valueOf(atsRequestBatchSize));
        int pageNumber = -1;

        while (inProgress) {
            pageNumber++;
            params.put("page", String.valueOf(pageNumber));

            ResponseEntity<Map> response =
                    cshrRestTemplate.exchange(findAllVacanciesEndpoint, HttpMethod.GET, buildRequest(), Map.class, params);

            Map<String, Object> body = response.getBody();

            inProgress = elementsExistToProcess(body);
            if (inProgress) {
                List<Map<String, Object>> vacancies = (List<Map<String, Object>>) body.get("content");
                List<String> vacanciesToDelete = vacancies
                        .stream()
                        .filter(v -> checkIfNoLongerLive(v, liveVacancyIdentifiers))
                        .map(this :: extractIdentifier)
                        .collect(Collectors.toList());

                log.debug("Found " + vacanciesToDelete.size() + " vacancies to delete from page number " + pageNumber +
                        " of vacancies retrieved from the CSHR data model");
                if (!vacanciesToDelete.isEmpty()) {
                    vacancyProcessor.deleteVacancies(vacanciesToDelete, auditFilePath, processStatistics);
                }
            }
        }
    }

    private HttpEntity<?> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity<>(headers);
    }

    private boolean elementsExistToProcess(Map<String, Object> body) {
        Double numberOfElements = (Double) body.get("numberOfElements");

        return numberOfElements.intValue() > 0;
    }

    private boolean checkIfNoLongerLive(Map<String, Object> vacancy, Set<String> liveAtsIdentifiers) {
        String vendorIdentifier = (String) vacancy.get("atsVendorIdentifier");
        boolean active = (Boolean) vacancy.get("active");
        Double tmp = (Double) vacancy.get(IDENTIFIER);
        String vacancyIdentifier = String.valueOf(tmp.intValue());
        String closingDate = (String) vacancy.get("closingDate");
        closingDate = closingDate.replace("T", " ");
        ZonedDateTime zonedDate = ZonedDateTime.parse(closingDate, ofPattern("yyyy-MM-dd HH:mm:ss.SSSZ"));

        return !liveAtsIdentifiers.contains(vacancyIdentifier)
                && zonedDate.toLocalDateTime().isAfter(LocalDateTime.now())
                && active
                && atsVendorId.equals(vendorIdentifier);
    }

    private String extractIdentifier(Map<String, Object> vacancy) {
        Double tmp = (Double) vacancy.get("id");

        return String.valueOf(tmp.intValue());
    }
}
