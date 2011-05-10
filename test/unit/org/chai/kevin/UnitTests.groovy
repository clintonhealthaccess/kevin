package org.chai.kevin

import grails.plugin.spock.UnitSpec;

import java.util.Date;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;

class UnitTests extends UnitSpec {

	static Date mar01 = Initializer.getDate( 2005, 3, 1 );
	static Date mar31 = Initializer.getDate( 2005, 3, 31 );
	
	def addBasicData() {
		def monthly = new MonthlyPeriodType();
		mockDomain(MonthlyPeriodType, [monthly])
		def period = new Period(periodType: monthly, startDate: mar01, endDate: mar31)
		mockDomain(Period, [period])
		
		// organisations
		def burera = new OrganisationUnit(name: "Burera", shortName:"RW,N,BU")
		def butaro = new OrganisationUnit(name: "Butaro DH", shortName:"RW,N,BU,BUDH", parent: burera)
		def kivuye = new OrganisationUnit(name: "Kivuye HC", shortName:"RW,N,BU,KIHC", parent: burera)
		burera.children = [butaro, kivuye]
				
		// organisation groups
		def dh = new OrganisationUnitGroup(name: "District Hospital", uuid: "District Hospital", members: [butaro])
		def hc = new OrganisationUnitGroup(name: "Health Center", uuid: "Health Center", members: [kivuye])
		butaro.groups = [dh]
		kivuye.groups = [hc]
		mockDomain(OrganisationUnit, [burera, butaro, kivuye])
		mockDomain(OrganisationUnitGroup, [dh, hc])
		
		def level1 = new OrganisationUnitLevel(level: 0, name: "Country")
		def level2 = new OrganisationUnitLevel(level: 1, name: "Province")
		def level3 = new OrganisationUnitLevel(level: 2, name: "District")
		def level4 = new OrganisationUnitLevel(level: 3, name: "Facility")
		mockDomain(OrganisationUnitLevel, [level1, level2, level3, level4])
	}
	
}
