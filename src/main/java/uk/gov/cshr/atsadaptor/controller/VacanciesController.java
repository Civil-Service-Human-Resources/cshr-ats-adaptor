package uk.gov.cshr.atsadaptor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.error.CSHRServiceStatus;

/**
 * This class provides the REST services to request vacancies from an external applicant tracking system to be processed into the CSHR data store.
 */
@RestController
public class VacanciesController implements VacanciesApi {
    @Override
    public ResponseEntity<CSHRServiceStatus> getVacancies() {
        return ResponseEntity.ok(CSHRServiceStatus.builder().summary("Request to load vacancies received.").build());
    }
}
