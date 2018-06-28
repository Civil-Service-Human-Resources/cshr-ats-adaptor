package uk.gov.cshr.atsadaptor.service.cshr.model.vacancy;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping the Applicant Tracking System's data model to the CSHR data
 * model.
 */
@Component
@Slf4j
public class AtsToCshrDataMapper {
    private static final int SUCCESS = 1;

    @Value("${cshr.ats.vendor.id}")
    private String atsVendorId;
    @Inject
    private BenefitsMapper benefitsMapper;
    @Inject
    private DateMapper dateMapper;
    @Inject
    private DefaultMapper defaultMapper;
    @Inject
    private DepartmentMapper departmentMapper;
    @Inject
    private DisplayCscContentMapper displayCscContentMapper;
    @Inject
    private LinkToApplyMapper linkToApplyMapper;
    @Inject
    private LocationsMapper locationsMapper;
    @Inject
    private LookupMapper lookupMapper;
    @Inject
    private MaximumSalaryMapper maximumSalaryMapper;
    @Inject
    private MinimumSalaryMapper minimumSalaryMapper;
    @Inject
    private NationalStatementMapper nationalStatementMapper;
    @Inject
    private OverseasJobMapper overseasJobMapper;
    @Inject
    private PersonalSpecificationMapper personalSpecificationMapper;
    @Inject
    private RegionsMapper regionsMapper;
    @Inject
    private SalaryOverrideMapper salaryOverrideMapper;
    @Inject
    private SelectionProcessDetailsMapper selectionProcessDetailsMapper;
    @Inject
    private MultiLookupMapper multiLookupMapper;

    /**
     * This method maps the data in the source map to new keys in the resulting map.
     * <p>
     * <p>The method will perform any specified rules for mapping the data.
     *
     * @param atsVacancy raw data from Applicant Tracking System
     * @return mapped data in the format of the CSHR data model
     */
    public Map<String, Object> map(Map<String, Object> atsVacancy, boolean active) {
        log.info("Mapping data from ATS data model into the CSHR data model");

        Map<String, Object> result = new HashMap<>();

        if (atsVacancy != null && !atsVacancy.isEmpty() && responseIsSuccessful(atsVacancy)) {
            result.put("identifier", defaultMapper.map(atsVacancy, "job_reference"));
            result.put("active", active);
            result.put("atsVendorIdentifier", atsVendorId);
            result.put("applyURL", linkToApplyMapper.map(atsVacancy));
            result.put("closingDate", dateMapper.map(atsVacancy, "closingdate", false));
            result.put("contactDepartment", "");
            result.put("contactEmail", "");
            result.put("contactName", defaultMapper.map(atsVacancy, "154_5070000"));
            result.put("contactTelephone", "");
            result.put("contractTypes", multiLookupMapper.map(atsVacancy, "nghr_emp_type"));
            result.put("department", departmentMapper.map(atsVacancy));
            result.put("description", defaultMapper.map(atsVacancy, "015_5070000"));
            result.put("displayCscContent", displayCscContentMapper.map(atsVacancy));
            String eligibility = defaultMapper.map(atsVacancy, "190_5070000");
            result.put("eligibility", eligibility != null ? eligibility : "");
            result.put("governmentOpeningDate", dateMapper.map(atsVacancy, "startdate_across_gov", true));
            result.put("grade", multiLookupMapper.map(atsVacancy, "nghr_grade"));
            result.put("internalOpeningDate", dateMapper.map(atsVacancy, "startdate_live", true));
            result.put("locationOverride", defaultMapper.map(atsVacancy, "nghr_site"));
            result.put("nationalityStatement", nationalStatementMapper.map(atsVacancy));
            String numVacancies = lookupMapper.map(atsVacancy, "070_5070000");
            Integer numberOfVacancies =
                    StringUtils.isNumeric(numVacancies) ? Integer.valueOf(numVacancies) : 0;
            result.put("numberVacancies", numberOfVacancies);
            result.put("overseasJob", overseasJobMapper.map(atsVacancy));
            result.put("personalSpecification", personalSpecificationMapper.map(atsVacancy));
            result.put("publicOpeningDate", dateMapper.map(atsVacancy, "startdate_external", true));
            result.put("regions", regionsMapper.map(atsVacancy));
            result.put("responsibilities", "");
            result.put("salaryMax", maximumSalaryMapper.map(atsVacancy));
            result.put("salaryMin", minimumSalaryMapper.map(atsVacancy));
            result.put("salaryOverrideDescription", salaryOverrideMapper.map(atsVacancy));
            result.put("selectionProcessDetails", selectionProcessDetailsMapper.map(atsVacancy));
            result.put("title", defaultMapper.map(atsVacancy, "vactitle"));
            result.put("vacancyLocations", locationsMapper.map(atsVacancy));
            result.put("whatWeOffer", benefitsMapper.map(atsVacancy));
            result.put("workingHours", defaultMapper.map(atsVacancy, "085_5070000"));
            result.put("workingPatterns", multiLookupMapper.map(atsVacancy, "038_5070000"));
        }

        return result;
    }

    private boolean responseIsSuccessful(Map<String, Object> atsVacancy) {
        return ((Double) atsVacancy.get("vacancyResponseStatus")).intValue() == SUCCESS;
    }
}
