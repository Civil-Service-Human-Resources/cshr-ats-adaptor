package uk.gov.cshr.atsadaptor.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.cshr.error.CSHRServiceStatus;

/**
 * This class provides global exception handling capability for this microservice
 */
@ControllerAdvice
@Slf4j
public class CshrRestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Handle exceptions that represent internal server errors
     *
     * @param ex      exception that was raised
     * @param request {@link WebRequest}
     * @return CSHRServiceStatus details of the error
     */
    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CSHRServiceStatus handleException(ExternalApplicantTrackingSystemException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return ex.getCshrServiceStatus();
    }
}
