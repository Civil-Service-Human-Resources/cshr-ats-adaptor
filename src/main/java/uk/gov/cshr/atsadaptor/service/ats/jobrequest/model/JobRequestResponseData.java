package uk.gov.cshr.atsadaptor.service.ats.jobrequest.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequestResponseData {
    private String responseDataTimestamp;
    private List<Map<String, Object>> vacancy;
}
