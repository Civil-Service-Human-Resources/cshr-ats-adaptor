package uk.gov.cshr.atsadaptor.service.cshr;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This is class is a temporary capability for notifying staff about the result of the scheduled process run.
 *
 * It will be replaced by some automatic scanning of files as they updated.
 */
@Service
@Slf4j
public class SlackNotificationService {
    private static final String GREEN = "#34f944";
    private static final String AMBER = "#ff5d00";
    private static final String RED = "#f93434";

    @Value("${slack.notification.channel}")
    private String slackNotificationChannel;
    @Value("${slack.notification.url}")
    private String slackNotificationUrl;

    private RestTemplate slackRestTemplate;

    public SlackNotificationService(RestTemplateBuilder restTemplateBuilder) {
        slackRestTemplate = restTemplateBuilder.build();
    }

    public void postResponseToSlack(CSHRServiceStatus message) {
        HttpEntity<Map<String, Object>> entity = buildEntity(message);

        try {
            slackRestTemplate.postForEntity(new URI(slackNotificationUrl), entity, String.class);
        } catch (Exception ex) {
            log.error("An error occurred trying to post the results of the scheduled process to the Slack channel called: "
                    + slackNotificationChannel
            + ".  The content of the message was: " + entity.getBody().toString());
        }
    }

    private HttpEntity<Map<String, Object>> buildEntity(CSHRServiceStatus message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(buildSlackMessage(message) ,headers);
    }

    private String convertMessage(CSHRServiceStatus status) {
        StringBuilder builder = new StringBuilder();

        builder.append("Code: ")
                .append(status.getCode())
                .append("\nSummary: ")
                .append(status.getSummary())
                .append("\nDetail: ");

        for(String detail : status.getDetail()) {
            builder.append(detail)
                    .append("\n");
        }

        return builder.toString();
    }

    private Map<String, Object> buildSlackMessage(CSHRServiceStatus serviceStatus) {
        Map<String, Object> slackMessage = new LinkedHashMap<>();

        slackMessage.put("channel", slackNotificationChannel);
        slackMessage.put("attachments", buildSlackAttachments(serviceStatus));


        return slackMessage;
    }

    private List<Map<String, Object>> buildSlackAttachments(CSHRServiceStatus serviceStatus) {
        List<Map<String, Object>> attachments = new ArrayList<>();

        Map<String, Object> attachment = new LinkedHashMap<>();

        attachment.put("fallback", "This is the result of the process run");
        attachment.put("color", getMessageColor(serviceStatus.getCode()));
        attachment.put("author_name", "ats-adaptor-service");
        attachment.put("title", "Result of Vacancy Load Process");
        attachment.put("text", convertMessage(serviceStatus));
        attachment.put("fields", buildAttachmentFields(serviceStatus));
        attachment.put("footer", "V9 Vacancy Load");
        attachment.put("ts", Instant.now().getEpochSecond());

        attachments.add(attachment);

        return attachments;
    }

    private List<Map<String, Object>> buildAttachmentFields(CSHRServiceStatus serviceStatus) {
        List<Map<String, Object>> fields = new ArrayList<>();
        Map<String, Object> field = new LinkedHashMap<>();

        field.put("title", "Priority");
        field.put("value", getMessagePriority(serviceStatus.getCode()));
        field.put("short", true);

        fields.add(field);

        return fields;
    }

    private String getMessageColor(String code) {
        String color;

        if (StatusCode.PROCESS_COMPLETED.getCode().equals(code)) {
            color = GREEN;
        } else if (StatusCode.PROCESS_COMPLETED_WITH_ERRORS.getCode().equals(code)) {
            color = AMBER;
        } else {
            color = RED;
        }

        return color;
    }

    private String getMessagePriority(String code) {
        String priority;

        if (StatusCode.PROCESS_COMPLETED.getCode().equals(code)) {
            priority = "Low";
        } else if (StatusCode.PROCESS_COMPLETED_WITH_ERRORS.getCode().equals(code)) {
            priority = "Medium";
        } else {
            priority = "High";
        }

        return priority;
    }
}
