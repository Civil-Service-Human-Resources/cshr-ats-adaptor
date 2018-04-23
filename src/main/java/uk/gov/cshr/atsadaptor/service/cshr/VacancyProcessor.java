package uk.gov.cshr.atsadaptor.service.cshr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.JobRetriever;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.model.JobRequestResponseWrapper;
import uk.gov.cshr.atsadaptor.service.ats.jobslist.model.VacancyListData;
import uk.gov.cshr.atsadaptor.service.cshr.model.vacancy.AtsToCshrDataMapper;

/**
 * This class is responsible for coordinating the work involved in retrieving the full details of
 * jobs from the Applicant Tracking System, mapping them to the CSHR model and posting them to the
 * CSHR API.
 */
@Component
@Slf4j
public class VacancyProcessor {
    private AtsToCshrDataMapper dataMapper;
    private JobRetriever jobRetriever;

    public VacancyProcessor(AtsToCshrDataMapper dataMapper, JobRetriever jobRetriever) {
        this.dataMapper = dataMapper;
        this.jobRetriever = jobRetriever;
    }

    /**
     * This method is responsible for processing a collection of vacancies, retrieving all the data
     * and mapping onto the csrh data model for submission to the data store.
     *
     * @param jobs jobs to be processed
     */
    public void process(List<VacancyListData> jobs) {
        log.info("Starting to process a batch of jobs");

        JobRequestResponseWrapper jobsData = jobRetriever.retrieveJob(jobs);

        jobsData.getVacancyResponse().getResponseData().getVacancy().forEach(this::processVacancy);
    }

    // TODO: Replace the guts of the writing to file with the actual call to the CSHR API in the next
    // branch to be worked on
    private void processVacancy(Map<String, Object> sourceVacancyData) {
        Map<String, Object> fields = (Map<String, Object>) sourceVacancyData.get("field");
        String jobRef = (String) ((Map<String, Object>) fields.get("job_reference")).get("value");

        log.info("Processing a vacancy with job Reference = " + jobRef);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String mappedVacancy = gson.toJson(dataMapper.map(sourceVacancyData), Map.class);

        try {
            FileUtils.write(new File(jobRef + ".txt"), mappedVacancy, Charset.forName("UTF-8"), false);
        } catch (IOException e) {
            log.error("Error writing mapped vacancy to file", e);
        }
    }
}
