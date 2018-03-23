package uk.gov.cshr.atsadaptor.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.cshr.atsadaptor.AtsAdaptorApplication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = AtsAdaptorApplication.class)
@ContextConfiguration
@WebAppConfiguration
public class VacanciesControllerTest extends AbstractJUnit4SpringContextTests {
    private static final String ACCESS_TOKEN = "access_token";
    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private static final String AUTHORIZATION = "Authorization";
    private static final String BAD_CLIENT = "badClient";
    private static final String BEARER = "Bearer ";
    private static final String CLIENT = "client";
    private static final String CLIENT_ID = "client_id";
    private static final String GRANT_TYPE = "grant_type";
    private static final String OAUTH2_PATH = "/oauth/token";
    private static final String PASSWORD = "password";
    private static final String REQUEST_PATH = "/vacancies";
    private static final String USER = "user";
    private static final String USERNAME = "username";

    @Inject
    private FilterChainProxy springSecurityFilterChain;
    @Inject
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void testVacancies_invalidVerbAndNoAuthentication() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(REQUEST_PATH)
                .contentType(APPLICATION_JSON_UTF8)
                .content(""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testVacancies_validVerbAndNoAuthentication() throws Exception {
        mvc.perform(get(REQUEST_PATH)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testVacancies_invalidVerbAndValidAuthentication() throws Exception {
        String token = getAccessToken();

        mvc.perform(MockMvcRequestBuilders.post(REQUEST_PATH)
                .contentType(APPLICATION_JSON_UTF8)
                .header(AUTHORIZATION, BEARER + token))
                .andExpect(status().isMethodNotAllowed());
    }

    private String getAccessToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE, PASSWORD);
        params.add(USERNAME, USER);
        params.add(PASSWORD, USER);
        params.add(CLIENT_ID, CLIENT);

        String response = mvc.perform(MockMvcRequestBuilders.post(OAUTH2_PATH)
                .contentType(APPLICATION_JSON_UTF8)
                .params(params)
                .with(httpBasic(CLIENT, CLIENT)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new JacksonJsonParser().parseMap(response).get(ACCESS_TOKEN).toString();
    }

    @Test
    public void testVacancies_invalidUserCredentialsToGetToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE, PASSWORD);
        params.add(USERNAME, "foo");
        params.add(PASSWORD, "dog");
        params.add(CLIENT_ID, CLIENT);

        mvc.perform(MockMvcRequestBuilders.post(OAUTH2_PATH)
                .contentType(APPLICATION_JSON_UTF8)
                .params(params)
                .with(httpBasic(CLIENT, CLIENT)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testVacancies_invalidClientCredentialsToGetToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE, PASSWORD);
        params.add(USERNAME, USER);
        params.add(PASSWORD, USER);
        params.add(CLIENT_ID, BAD_CLIENT);

        mvc.perform(MockMvcRequestBuilders.post(OAUTH2_PATH)
                .contentType(APPLICATION_JSON_UTF8)
                .params(params)
                .with(httpBasic(BAD_CLIENT, "badSecret")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testVacancies_validVerbAndAuthentication() throws Exception {
        String token = getAccessToken();

        mvc.perform(MockMvcRequestBuilders.get(REQUEST_PATH)
                .header(AUTHORIZATION, BEARER + token))
                .andExpect(status().isOk());
    }
}