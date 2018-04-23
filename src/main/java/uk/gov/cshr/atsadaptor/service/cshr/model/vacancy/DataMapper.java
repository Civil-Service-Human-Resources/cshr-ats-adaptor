package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.Map;

/**
 * Provides some common behaviour for implementations of data mappers.
 */
abstract class DataMapper {
    static final String BR = "<br>";
    static final String CLOSE_BOLD = "</b>";
    static final String CLOSE_PARA = "</p>";
    static final String FIELD = "field";
    static final String OPEN_BOLD = "<b>";
    static final String OPEN_PARA = "<p>";
    static final String VALUE = "value";

    boolean mainFieldCanBeMapped(Map<String, Object> source, String fieldName) {
        return source != null && !source.isEmpty() && displayValue(source, fieldName);
    }

    /**
     * Gets the value of the field's display property.
     *
     * @param source    data from the Applicant Tracking System containing the fields to use to map the
     *                  content.
     * @param fieldName name of field whose display property should be obtained
     * @return <code>true</code> if the field's display value is 'true' otherwise <code>false</code>
     */
    boolean displayValue(Map<String, Object> source, String fieldName) {
        Map<String, Object> fields = (Map<String, Object>) source.get(FIELD);

        return (Boolean) ((Map<String, Object>) fields.get(fieldName)).get("display");
    }

    /**
     * Gets the value of the field's label property.
     *
     * @param source    data from the Applicant Tracking System containing the fields to use to map the
     *                  content.
     * @param fieldName name of field whose label property should be obtained
     * @return The value of the field's label property
     */
    String getLabel(Map<String, Object> source, String fieldName) {
        Map<String, Object> fields = (Map<String, Object>) source.get(FIELD);

        return (String) ((Map<String, Object>) fields.get(fieldName)).get("label");
    }

    /**
     * Gets the value of the field's 'value' property.
     *
     * @param source    data from the Applicant Tracking System containing the fields to use to map the
     *                  content.
     * @param fieldName name of field whose 'value' property should be obtained
     * @return The value of the field's 'value' property
     */
    String getValue(Map<String, Object> source, String fieldName) {
        Map<String, Object> fields = (Map<String, Object>) source.get(FIELD);

        return (String) ((Map<String, Object>) fields.get(fieldName)).get(VALUE);
    }
}
