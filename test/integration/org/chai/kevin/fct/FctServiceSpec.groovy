package org.chai.kevin.fct

import org.chai.kevin.Organisation;
import org.chai.kevin.data.Type;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

class FctServiceSpec extends FctIntegrationTests { 

	def reportService
	
	def "test normal fct service"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def objective = newFctObjective(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		def fctTable = null
		refresh()
		
		when:
		fctTable = reportService.getFctTable(getOrganisation(RWANDA), objective, period, OrganisationUnitLevel.findByLevel(3), new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		fctTable.getOrganisationMap().get(getOrganisation(NORTH)).equals([getOrganisation(BURERA)])
		fctTable.getReportValue(getOrganisation(BURERA), target).value == "2.0"
		fctTable.getTotalValue(target).value == "2.0"
		
		when:
		fctTable = reportService.getFctTable(getOrganisation(RWANDA), objective, period, OrganisationUnitLevel.findByLevel(3), new HashSet([DISTRICT_HOSPITAL_GROUP]))
		
		then:
		fctTable.getOrganisationMap().get(getOrganisation(NORTH)).equals([getOrganisation(BURERA)])
		fctTable.getReportValue(getOrganisation(BURERA), target).value == "1.0"
		fctTable.getTotalValue(target).value == "1.0"
		
		when:
		fctTable = reportService.getFctTable(getOrganisation(BURERA), objective, period, OrganisationUnitLevel.findByLevel(1), new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		fctTable.organisations.isEmpty()
	}
		
	def "test normal fct service with dummy organisation"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def objective = newFctObjective(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		def fctTable = null
		
		when:
		def dummy = newOrganisationUnit("dummy", OrganisationUnit.findByName(NORTH))
		refresh()
		fctTable = reportService.getFctTable(getOrganisation(RWANDA), objective, period, OrganisationUnitLevel.findByLevel(3), new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		fctTable.getOrganisationMap().get(getOrganisation(NORTH)).equals([getOrganisation(BURERA), getOrganisation("dummy")])
		fctTable.getTotalValue(target).value == "2.0"
		fctTable.getReportValue(getOrganisation("dummy"), target).value == "0.0"
		fctTable.getReportValue(getOrganisation(BURERA), target).value == "2.0"
				
	}
	
	
	
}
