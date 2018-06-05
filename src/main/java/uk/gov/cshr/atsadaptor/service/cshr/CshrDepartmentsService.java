package uk.gov.cshr.atsadaptor.service.cshr;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.atsadaptor.exception.LoadDepartmentsException;
import uk.gov.cshr.atsadaptor.service.cshr.model.department.Department;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class is responsible for loading the departments from the CSHR data store for use in looking
 * up a departement's id based on its name
 */
@Service
@Slf4j
public class CshrDepartmentsService implements DepartmentsService {
    private RestTemplate cshrRestTemplate;
    private String findAllDepartmentsEndpoint;

    public CshrDepartmentsService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${cshr.api.service.department.findAll.endpoint}") String findAllDepartmentsEndpoint,
            @Value("${cshr.api.service.search.username}") String cshrApiUsername,
            @Value("${cshr.api.service.search.password}") String cshrApiPassword) {
        this.cshrRestTemplate = restTemplateBuilder.basicAuthorization(cshrApiUsername, cshrApiPassword).build();
        this.findAllDepartmentsEndpoint = findAllDepartmentsEndpoint;
    }

    @Override
    @Cacheable(value = "departments")
    public List<Department> getDepartments() {
        log.info("Starting to load departments from the cshr-api endpoint for caching");

        ResponseEntity<Map> response =
                cshrRestTemplate.exchange(buildUrl(), HttpMethod.GET, buildRequest(), Map.class);

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            Map<String, Object> content = response.getBody();
            List<Map<String, Object>> departments = (List<Map<String, Object>>) content.get("content");

            log.debug(
                    "Retrieved " + departments.size() + " departments and starting to create the cache");

            return departments
                    .stream()
                    .map(
                            d ->
                                    Department.builder()
                                            .id(((Double) (d.get("id"))).intValue())
                                            .name((String) d.get("name"))
                                            .build())
                    .collect(Collectors.toList());
        } else {
            throw new LoadDepartmentsException(
                    CSHRServiceStatus.builder()
                            .code(StatusCode.THIRD_PARTY_SERVICE_ERROR.getCode())
                            .summary(
                                    "An error occurred trying to load all the departments from the CSHR vacancy data store.")
                            .build());
        }
    }

    private String buildUrl() {
        return UriComponentsBuilder.fromHttpUrl(findAllDepartmentsEndpoint)
                .queryParam("size", 300)
                .toUriString();
    }

    private HttpEntity<?> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity<>(headers);
    }
}
