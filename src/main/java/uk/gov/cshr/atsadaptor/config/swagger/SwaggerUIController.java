package uk.gov.cshr.atsadaptor.config.swagger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is responsible for ensuring that the Swagger UI html page is only accessible when the
 * a specific profile is enabled on startup.
 */
@Profile("!swagger-dev")
@RestController
public class SwaggerUIController {
    /**
     * This method is responsible for preventing access to the swagger-ui.html page if a specific
     * profile is not enabled.
     *
     * @param httpResponse the response to be returned
     */
    @RequestMapping(value = "swagger-ui.html", method = RequestMethod.GET)
    public void getSwagger(HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
    }
}
