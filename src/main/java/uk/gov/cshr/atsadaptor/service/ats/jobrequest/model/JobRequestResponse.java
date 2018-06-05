package uk.gov.cshr.atsadaptor.service.ats.jobrequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequestResponse {
    private JobRequestResponseData responseData;
    private String responseType;
    private String serviceVersion;
    private String statusCode;
    private String statusMessage;
}
