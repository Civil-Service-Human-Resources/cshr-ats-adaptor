package uk.gov.cshr.atsadaptor.service.ats.jobslist;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import org.junit.Test;
import uk.gov.cshr.atsadaptor.util.VacancyListDataBuilder;

/**
 * Tests {@link VacancyListResponseWrapper}
 */
public class VacancyListResponseWrapperTest {
    @Test
    public void testDemarshall() throws IOException, ParseException {
        VacancyListResponseWrapper expected = buildExpectedWrapper();

        try (InputStream inputStream = VacancyListResponseWrapperTest.class.getResourceAsStream("/listRequestResponses/fourVacanciesList.json")) {

            byte[] source = ByteStreams.toByteArray(inputStream);
            Gson gson = new Gson();
            System.out.println(new String(source));
            VacancyListResponseWrapper actual = gson.fromJson(new String(source), VacancyListResponseWrapper.class);

            assertThat(actual, is(equalTo(expected)));
        }
    }

    private VacancyListResponseWrapper buildExpectedWrapper() throws ParseException {
        return VacancyListResponseWrapper.builder().vacancyResponse(buildExpectedListResponse()).build();
    }

    private VacancyListResponse buildExpectedListResponse() throws ParseException {
        return VacancyListResponse
                .builder()
                .statusCode("1")
                .statusMessage("success")
                .responseData(buildExpectedResponseData())
                .build();
    }

    private ResponseListData buildExpectedResponseData() throws ParseException {
        return ResponseListData
                .builder()
                .responseDataTimestamp("2018-03-13T11:50:01Z")
                .vacancyJobRequestLimit(100)
                .vacancyListCount(4)
                .vacancyList(VacancyListDataBuilder.getInstance().buildExepctedVacancyListData())
                .build();
    }
}
