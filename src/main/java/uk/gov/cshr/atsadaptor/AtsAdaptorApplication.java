package uk.gov.cshr.atsadaptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@EnableCaching
@EnableScheduling
public class AtsAdaptorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AtsAdaptorApplication.class, args);
    }
}
