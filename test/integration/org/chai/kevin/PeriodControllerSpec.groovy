package org.chai.kevin

import org.chai.kevin.data.Type;
import org.chai.kevin.planning.PlanningIntegrationTests;
import org.chai.kevin.survey.SurveyIntegrationTests;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;
import org.chai.location.Location;

class PeriodControllerSpec extends IntegrationTests {

	def periodController
	
	def "create survey with active flag resets active flag on other surveys"() {
		setup:
		def period = newPeriod(true)
		periodController = new PeriodController()

		when:
		periodController.params.code = 'new period'
		periodController.params['startDate_day'] = '1'
		periodController.params['startDate_month'] = '1'
		periodController.params['startDate_year'] = '2009'
		periodController.params['endDate_day'] = '31'
		periodController.params['endDate_month'] = '1'
		periodController.params['endDate_year'] = '2009'
		periodController.params.defaultSelected = true
		periodController.saveWithoutTokenCheck()
		
		then:
		Period.count() == 2
		Period.list()[1].defaultSelected == true
		Period.list()[0].defaultSelected == false
	}
	
	def "cannot delete period with survey"() {
		setup:
		def period = newPeriod(true)
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		periodController = new PeriodController()
		
		when:
		periodController.params.id = period.id
		periodController.delete()
		
		then:
		Period.count() == 1
	}
	
	def "cannot delete period with planning"() {
		setup:
		def period = newPeriod(true)
		def planning = PlanningIntegrationTests.newPlanning(period)
		periodController = new PeriodController()
		
		when:
		periodController.params.id = period.id
		periodController.delete()
		
		then:
		Period.count() == 1
	}
	
	def "cannot delete period with values"() {
		setup:
		setupLocationTree()
		def period = newPeriod(true)
		def data = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		newRawDataElementValue(data, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		periodController = new PeriodController()
		
		when:
		periodController.params.id = period.id
		periodController.delete()
		
		then:
		Period.count() == 1
	}
	
	def "can delete period"() {
		setup:
		def period = newPeriod(true)
		periodController = new PeriodController()
		
		when:
		periodController.params.id = period.id
		periodController.delete()
		
		then:
		Period.count() == 0
	}
	
	def "can delete period with export"() {
		setup:
		setupLocationTree()
		def period = newPeriod(true)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def export = newDataElementExport(CODE(1), [:], s([period]), s([DISTRICT_HOSPITAL_GROUP]), s([Location.findByCode(RWANDA)]), s([dataElement]))
		periodController = new PeriodController()
		
		when:
		periodController.params.id = period.id
		periodController.delete()
		
		then:
		Period.count() == 0
	}
	
}
