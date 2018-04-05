package uk.gov.cshr.atsadaptor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.gov.cshr.atsadaptor.service.ats.jobslist.VacancyListData;

public final class VacancyListDataBuilder {
    private static final VacancyListDataBuilder INSTANCE = new VacancyListDataBuilder();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private VacancyListDataBuilder() {
    }

    public static VacancyListDataBuilder getInstance() {
        return INSTANCE;
    }

    public List<VacancyListData> buildExepctedVacancyListData() throws ParseException {
        List<VacancyListData> data = new ArrayList<>();

        Date tmp = sdf.parse("2018-03-13T09:48:07+0000");
        data.add(VacancyListData.builder().jcode("1234567").vacancyTimestamp(new java.sql.Timestamp(tmp.getTime())).build());

        tmp = sdf.parse("2018-03-12T10:48:06+0000");
        data.add(VacancyListData.builder().jcode("1544568").vacancyTimestamp(new java.sql.Timestamp(tmp.getTime())).build());

        tmp = sdf.parse("2018-03-11T17:53:12+0000");
        data.add(VacancyListData.builder().jcode("1634587").vacancyTimestamp(new java.sql.Timestamp(tmp.getTime())).build());

        tmp = sdf.parse("2018-03-09T13:09:12+0000");
        data.add(VacancyListData.builder().jcode("1533291").vacancyTimestamp(new java.sql.Timestamp(tmp.getTime())).build());

        return data;
    }
}
