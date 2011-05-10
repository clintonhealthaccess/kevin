package org.chai.kevin.cost

import org.chai.kevin.Organisation;
import org.chai.kevin.UnitTests;
import org.gmock.WithGMock;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.UnitSpec;

@WithGMock
class CostTableSpec extends UnitTests {

	def costTableService
	
	def setup() {
		addBasicData()
	}
	
	def "get cost table with null objective"() {
		setup:
		def costTableService = new CostTableService()
		def costService = mock(CostService)
		costService.getYears().returns([1,2,3,4,5])
		costTableService.costService = costService
		
		when:
		def period = Period.list()[0]
		def organisation = new Organisation(OrganisationUnit.findByName("Burera"))
		def costTable;
		play {
			costTable = costTableService.getCostTable(period, null, organisation)
		}
		
		then:
		costTable.years == [1,2,3,4,5]
		costTable.currentOrganisation == organisation
		costTable.currentObjective == null
		costTable.currentPeriod == period
		costTable.targets == []
	}	
}
