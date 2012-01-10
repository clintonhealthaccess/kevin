package org.chai.kevin.fct

import org.chai.kevin.LocationService
import org.chai.kevin.IntegrationTests
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

class FctControllerSpec extends FctIntegrationTests {

	def fctController
	
	def "test view action"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def objective = newReportObjective(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		
		when: "valid table"
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.organisation = LocationEntity.findByCode(RWANDA).id
		fctController.params.objective = objective.id
		fctController.params.level = LocationLevel.findByCode(DISTRICT).id
		def model = fctController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentOrganisation.equals(LocationEntity.findByCode(RWANDA))
		model.currentObjective.equals(objective)
		model.currentLevel.equals(LocationLevel.findByCode(DISTRICT))
		model.fctTable != null
	}
	
	
	def "test view action with no objective"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def objective = newReportObjective(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		
		when: "no objective"
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.organisation = LocationEntity.findByCode(RWANDA).id
		fctController.params.level = 3
		def model = fctController.view()
		
		then:
		model.fctTable == null
		
	}
	
	def "test view action with invalid paramters"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def objective = newReportObjective(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		
		when: "invalid parameters"
		fctController = new FctController()
		fctController.params.currentPeriod = period.id
		fctController.params.organisation = LocationEntity.findByCode(BURERA).id
		fctController.params.objective = objective.id
		fctController.params.level = LocationLevel.findByCode(COUNTRY).id
		fctController.params.filter = "organisation"
		def model = fctController.view()
		
		then:
		model.fctTable != null
		model.fctTable.organisations.isEmpty()
		
	}
	
}

