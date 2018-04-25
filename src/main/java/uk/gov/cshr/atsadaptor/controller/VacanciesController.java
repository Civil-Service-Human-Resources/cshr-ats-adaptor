package uk.gov.cshr.atsadaptor.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListFilter;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.cshr.VacancyService;
import uk.gov.cshr.status.CSHRServiceStatus;

@RestController
@Slf4j
public class VacanciesController implements VacanciesApi {
    private VacancyService cshrVacancyService;
    private JobsListFilter jobsListFilter;
    private JobsListRetriever jobsListRetriever;

    public VacanciesController(VacancyService cshrVacancyService, JobsListFilter jobsListFilter,
                               JobsListRetriever jobsListRetriever) {
        this.cshrVacancyService = cshrVacancyService;
        this.jobsListFilter = jobsListFilter;
        this.jobsListRetriever = jobsListRetriever;
    }

    @Override
    public ResponseEntity<CSHRServiceStatus> getVacancies() {
        log.info("STARTED: Processing jobs from Applicant Tracking System into the CSHR Vacancy Data Store");
        List<VacancyListData> liveJobs = jobsListRetriever.getLiveVacancies();

        List<VacancyListData> changedJobs = jobsListFilter.filter(liveJobs);

        cshrVacancyService.processChangedVacancies(changedJobs);

        log.info("COMPLETED: Processing jobs from Applicant Tracking System into the CSHR Vacancy Data Store");

        return ResponseEntity.ok(
                CSHRServiceStatus.builder().summary("Request to load vacancies received").build());
    }
}
