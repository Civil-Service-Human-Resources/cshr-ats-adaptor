package uk.gov.cshr.atsadaptor.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.JobsListRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.VacancyListData;
import uk.gov.cshr.error.CSHRServiceStatus;

@RestController
@Slf4j
public class VacanciesController implements VacanciesApi {
    private JobsListRetriever jobsListRetriever;

    public VacanciesController(JobsListRetriever jobsListRetriever) {
        this.jobsListRetriever = jobsListRetriever;
    }

    @Override
    public ResponseEntity<CSHRServiceStatus> getVacancies() {
        List<VacancyListData> liveJobs = jobsListRetriever.getLiveVacancies();

        log.info(liveJobs.toString());

        return ResponseEntity.ok(CSHRServiceStatus.builder().summary("Request to load vacancies received and found the following live vacancies: " + liveJobs.toString()).build());
    }
}
