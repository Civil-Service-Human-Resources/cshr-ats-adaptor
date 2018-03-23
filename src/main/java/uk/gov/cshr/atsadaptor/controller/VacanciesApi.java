package uk.gov.cshr.atsadaptor.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.cshr.error.CSHRServiceStatus;

/**
 * Defines the REST services to request vacancies from an external applicant tracking system to be processed into the CSHR data store.
 */
@RequestMapping(value = "/vacancies", produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
@Api(value = "vacancies")
public interface VacanciesApi {
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "get vacancies", nickname = "getVacancies")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Request to load vacancies received.", response = CSHRServiceStatus.class),
            @ApiResponse(code = 500, message = "An unexpected error occurred processing your request. Please contact the system administrator.", response = CSHRServiceStatus.class),
            @ApiResponse(code = 502, message = "The service is unable to request vacancies from the external ats system. Please contact the system administrator.", response = CSHRServiceStatus.class),
            @ApiResponse(code = 503, message = "The service is currently unavailable. This may be a temporary condition and if it persists please contact the system administrator", response = CSHRServiceStatus.class),
            @ApiResponse(code = 504, message = "The request to the external ats system has timed out. Please contact the system administrator.", response = CSHRServiceStatus.class)})
    ResponseEntity<CSHRServiceStatus> getVacancies();
}
