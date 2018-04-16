package uk.gov.cshr.atsadaptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@EnableCaching
public class AtsAdaptorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AtsAdaptorApplication.class, args);
    }
}
