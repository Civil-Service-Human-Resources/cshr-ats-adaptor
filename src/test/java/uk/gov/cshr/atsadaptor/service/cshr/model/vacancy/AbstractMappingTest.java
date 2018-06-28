package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import uk.gov.cshr.atsadaptor.service.ats.jobrequest.model.JobRequestResponseWrapper;

abstract class AbstractMappingTest {
    final static String VALID_ATS_OVERSEAS_JOB_REQUEST_RESPONSE = "/jobRequestResponses/validJobRequestResponseForOverseas.json";

    Map<String, Object> getAtsSourceResponseData(java.lang.String responseName) throws IOException {
        InputStream inputStream = AtsToCshrDataMapper.class.getResourceAsStream(responseName);
        java.lang.String content = new java.lang.String(ByteStreams.toByteArray(inputStream));

        JobRequestResponseWrapper wrapper = new Gson().fromJson(content, JobRequestResponseWrapper.class);

        return wrapper.getVacancyResponse().getResponseData().getVacancy().get(0);
    }

    Map<String, Object> getField(Map<String, Object> source, String fieldName) {
        Map<String, Object> fields = (Map<String, Object>) source.get("field");

        return (Map<String, Object>) fields.get(fieldName);
    }

    void toggleDisplayFieldValue(Map<String, Object> source, String fieldName, boolean display) {
        Map<String, Object> field = getField(source, fieldName);

        field.put("display", display);
    }
}
