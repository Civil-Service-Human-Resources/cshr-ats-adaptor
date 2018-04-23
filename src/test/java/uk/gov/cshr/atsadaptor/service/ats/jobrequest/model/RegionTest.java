package uk.gov.cshr.atsadaptor.service.ats.jobrequest.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Tests {@link Region}
 */
public class RegionTest {
    @Test
    public void testLookupDescription_invalidCodeSupplied() {
        assertThat(Region.lookupDescription("FOO"), is(nullValue()));
    }

    @Test
    public void testLookupDescription_REGION_EA() {
        assertThat(Region.lookupDescription("REGION_EA"), is(equalTo("Eastern")));
    }

    @Test
    public void testLookupDescription_REGION_EM() {
        assertThat(Region.lookupDescription("REGION_EM"), is(equalTo("East Midlands")));
    }

    @Test
    public void testLookupDescription_REGION_LO() {
        assertThat(Region.lookupDescription("REGION_LO"), is(equalTo("London")));
    }

    @Test
    public void testLookupDescription_REGION_NE() {
        assertThat(Region.lookupDescription("REGION_NE"), is(equalTo("North East")));
    }

    @Test
    public void testLookupDescription_REGION_NI() {
        assertThat(Region.lookupDescription("REGION_NI"), is(equalTo("Northern Ireland")));
    }

    @Test
    public void testLookupDescription_REGION_NW() {
        assertThat(Region.lookupDescription("REGION_NW"), is(equalTo("North West")));
    }

    @Test
    public void testLookupDescription_REGION_SC() {
        assertThat(Region.lookupDescription("REGION_SC"), is(equalTo("Scotland")));
    }

    @Test
    public void testLookupDescription_REGION_SE() {
        assertThat(Region.lookupDescription("REGION_SE"), is(equalTo("South East")));
    }

    @Test
    public void testLookupDescription_REGION_SW() {
        assertThat(Region.lookupDescription("REGION_SW"), is(equalTo("South West")));
    }

    @Test
    public void testLookupDescription_REGION_WA() {
        assertThat(Region.lookupDescription("REGION_WA"), is(equalTo("Wales")));
    }

    @Test
    public void testLookupDescription_REGION_WM() {
        assertThat(Region.lookupDescription("REGION_WM"), is(equalTo("West Midlands")));
    }

    @Test
    public void testLookupDescription_REGION_YH() {
        assertThat(Region.lookupDescription("REGION_YH"), is(equalTo("Yorkshire and the Humber")));
    }
}
