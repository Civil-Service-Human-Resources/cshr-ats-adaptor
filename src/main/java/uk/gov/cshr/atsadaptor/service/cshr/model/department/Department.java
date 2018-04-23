package uk.gov.cshr.atsadaptor.service.cshr.model.department;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a cached version of the department to look up its id by name.
 */
@Builder
@Data
public class Department {
    private Integer id;
    private String name;
}
