package org.chai.kevin.fct

import org.chai.kevin.Organisation;
import org.chai.kevin.data.Type;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

class FctServiceSpec extends FctIntegrationTests { 

	def fctService
	
	def "test happy flow"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def objective = newFctObjective(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		refresh()
		
		when:
		def fctTable = fctService.getFct(getOrganisation(RWANDA), objective, period, OrganisationUnitLevel.findByLevel(3))
		
		then:
		fctTable.organisations.equals([getOrganisation(BURERA)])
		fctTable.getFct(getOrganisation(BURERA), target).value == "2.0"
		fctTable.getTotal(target).value == "2.0"
		
		when:
		fctTable = fctService.getFct(getOrganisation(BURERA), objective, period, OrganisationUnitLevel.findByLevel(1))
		
		then:
		fctTable.organisations.isEmpty()
		
		when:
		def dummy = newOrganisationUnit("dummy", OrganisationUnit.findByName(NORTH))
		refresh()
		fctTable = fctService.getFct(getOrganisation(RWANDA), objective, period, OrganisationUnitLevel.findByLevel(3))
		
		then:
		fctTable.organisations.equals([getOrganisation(BURERA), getOrganisation("dummy")])
		fctTable.getTotal(target).value == "2.0"
		fctTable.getFct(getOrganisation("dummy"), target).value == "0.0"
		fctTable.getFct(getOrganisation(BURERA), target).value == "2.0"
				
	}
	
	
	
}
