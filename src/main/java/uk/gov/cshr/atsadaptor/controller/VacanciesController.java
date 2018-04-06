package uk.gov.cshr.atsadaptor.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListFilter;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.error.CSHRServiceStatus;

@RestController
@Slf4j
public class VacanciesController implements VacanciesApi {
    private JobsListFilter jobsListFilter;
    private JobsListRetriever jobsListRetriever;

    public VacanciesController(JobsListFilter jobsListFilter, JobsListRetriever jobsListRetriever) {
        this.jobsListFilter = jobsListFilter;
        this.jobsListRetriever = jobsListRetriever;
    }

    @Override
    public ResponseEntity<CSHRServiceStatus> getVacancies() {
        log.info("A request to process jobs from an Applicant Tracking System was received.");
        List<VacancyListData> liveJobs = jobsListRetriever.getLiveVacancies();

        jobsListFilter.filter(liveJobs);

        return ResponseEntity.ok(CSHRServiceStatus.builder().summary("Request to load vacancies received").build());
    }
}
