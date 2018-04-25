package uk.gov.cshr.atsadaptor.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.cshr.atsadaptor.AtsAdaptorApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = AtsAdaptorApplication.class)
@ContextConfiguration
@WebAppConfiguration
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class VacanciesControllerTest extends AbstractJUnit4SpringContextTests {
    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private static final String REQUEST_PATH = "/vacancies/";

    @Inject
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getVacancies_invalidHttpVerb() throws Exception {
        mvc.perform(post(REQUEST_PATH)
                .contentType(APPLICATION_JSON_UTF8).content(""))
                .andExpect(status().is(HttpStatus.METHOD_NOT_ALLOWED.value()));
    }
}
