package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.value.AveragePartialValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class AverageControllerSpec extends IntegrationTests {

	def averageController
	
	def "save works"() {
		setup:
		averageController = new AverageController()
		
		when:
		averageController.params.code = CODE(1)
		averageController.params.expression = "1"
		averageController.saveWithoutTokenCheck()
		
		then:
		Average.count() == 1
		Average.list()[0].code == CODE(1)
		Average.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		averageController = new AverageController()
		
		when:
		averageController.params.code = CODE(1)
		averageController.saveWithoutTokenCheck()
		
		then:
		Average.count() == 0
	}
		
	def "delete validation deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def average = newAverage("1", CODE(1))
		newAveragePartialValue(average, period, OrganisationUnit.findByName(RWANDA), DISTRICT_HOSPITAL_GROUP, 1, v("1"))
		averageController = new AverageController()
		
		when:
		averageController.params.id = average.id
		averageController.delete()
		
		then:
		Average.count() == 0
		AveragePartialValue.count() == 0
	}
	
	def "save average deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def average = newAverage("1", CODE(1))
		newAveragePartialValue(average, period, OrganisationUnit.findByName(RWANDA), DISTRICT_HOSPITAL_GROUP, "1", v("1"))
		averageController = new AverageController()
		
		when:
		averageController.params.id = average.id
		averageController.save()
		
		then:
		Average.count() == 1
		AveragePartialValue.count() == 0
	}
	
	def "save average updates timestamp"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def average = newAverage("1", CODE(1))
		averageController = new AverageController()
		def time1 = average.timestamp
		
		when:
		averageController.params.id = average.id
		averageController.save()
		
		then:
		Average.count() == 1
		!Average.list()[0].timestamp.equals(time1)
	}
}
