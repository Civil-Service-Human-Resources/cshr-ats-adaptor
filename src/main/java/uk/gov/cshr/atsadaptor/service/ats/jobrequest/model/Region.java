package uk.gov.cshr.atsadaptor.service.ats.jobrequest.model;

/**
 * This enum is used to map the Application Tracking System value for a region to the CSHR
 * equivalent friendly text.
 */
public enum Region {
    REGION_EA("Eastern"),
    REGION_EM("East Midlands"),
    REGION_LO("London"),
    REGION_NE("North East"),
    REGION_NI("Northern Ireland"),
    REGION_NW("North West"),
    REGION_SC("Scotland"),
    REGION_SE("South East"),
    REGION_SW("South West"),
    REGION_WA("Wales"),
    REGION_WM("West Midlands"),
    REGION_YH("Yorkshire and the Humber");

    private String description;

    Region(String description) {
        this.description = description;
    }

    /**
     * Looks up the value of the description of an enum based on a given code.
     * <p>
     * <p>It will return null if no valid enum can be found for the given code.
     *
     * @param code value used to get a value representing this enum
     * @return description or null if no valid enum can be found for the given code.
     */
    public static String lookupDescription(String code) {
        String description;

        try {
            description = Region.valueOf(code).description;
        } catch (IllegalArgumentException | NullPointerException ex) {
            description = null;
        }

        return description;
    }
}
