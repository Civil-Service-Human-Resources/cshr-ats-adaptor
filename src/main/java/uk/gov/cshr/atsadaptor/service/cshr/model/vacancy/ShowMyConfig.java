package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by cshr on 25/04/2018.
 */

@Component
@Slf4j
public class ShowMyConfig {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void print() {
        

        log.info("Saurabh Testing");

        log.info("spring.security.service.username:" + environment.getProperty("spring.security.service.username"));
        log.info("spring.security.service.password:" + environment.getProperty("spring.security.service.password"));
        log.info("ats.request.endpoint:" + environment.getProperty("ats.request.endpoint"));
        log.info("ats.authentication.token:" + environment.getProperty("ats.authentication.token"));
        log.info("ats.client.id:" + environment.getProperty("ats.client.id"));
        log.info("ats.jobrun.history.directory:" + environment.getProperty("ats.jobrun.history.directory"));
        log.info("ats.jobrun.history.file:" + environment.getProperty("ats.jobrun.history.file"));
        log.info("cshr.api.service.department.findAll.endpoint:" + environment.getProperty("cshr.api.service.department.findAll.endpoint"));
        log.info("cshr.api.service.search.username:" + environment.getProperty("cshr.api.service.search.username"));
        log.info("cshr.api.service.search.password:" + environment.getProperty("cshr.api.service.search.password"));
        log.info("cshr.api.service.vacancy.save.username:" + environment.getProperty("cshr.api.service.vacancy.save.username"));
        log.info("cshr.api.service.vacancy.save.password:" + environment.getProperty("cshr.api.service.vacancy.save.password"));
        log.info("cshr.api.service.vacancy.save.endpoint:" + environment.getProperty("cshr.api.service.vacancy.save.endpoint"));
        log.info("cshr.ats.vendor.id:" + environment.getProperty("cshr.ats.vendor.id"));
        log.info("ats.request.batch.size:" + environment.getProperty("ats.request.batch.size"));
        log.info("cshr.jobrun.audit.directory:" + environment.getProperty("cshr.jobrun.audit.directory"));
        log.info("cshr.jobrun.audit.basefilename:" + environment.getProperty("cshr.jobrun.audit.basefilename"));
        log.info("ats.jobrun.history.file:" + environment.getProperty("ats.jobrun.history.file"));

    }
}
