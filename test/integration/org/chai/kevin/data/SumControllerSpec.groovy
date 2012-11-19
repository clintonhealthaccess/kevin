package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.kevin.dsr.DsrIntegrationTests;
import org.chai.kevin.value.SumPartialValue;

class SumControllerSpec extends IntegrationTests {

	def sumController
	
	def "save works"() {
		setup:
		sumController = new SummController()
		
		when:
		sumController.params.code = CODE(1)
		sumController.params.expression = "1"
		sumController.saveWithoutTokenCheck()
		
		then:
		Summ.count() == 1
		Summ.list()[0].code == CODE(1)
		Summ.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		sumController = new SummController()
		
		when:
		sumController.params.code = CODE(1)
		sumController.saveWithoutTokenCheck()
		
		then:
		Summ.count() == 0
	}
	
	def "delete sum deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def sum = newSum("1", CODE(1))
		newSumPartialValue(sum, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1")) 
		sumController = new SummController()
		
		when:
		sumController.params.id = sum.id
		sumController.delete()
		
		then:
		Summ.count() == 0
		SumPartialValue.count() == 0 
	}
	
	def "cannot delete sum if there are associated targets"() {
		setup:
		def program = newReportProgram(CODE(1))
		def targetCategory = DsrIntegrationTests.newDsrTargetCategory(CODE(2), program, 1)
		def sum = newSum("1", CODE(1))
		def target = DsrIntegrationTests.newDsrTarget(CODE(3), sum, targetCategory)
		sumController = new SummController()
		
		when:
		sumController.params.id = sum.id
		sumController.delete()
		
		then:
		Summ.count() == 1
	}

	def "save sum deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def sum = newSum("1", CODE(1))
		newSumPartialValue(sum, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		sumController = new SummController()
		
		when:
		sumController.params.id = sum.id
		sumController.save()
		
		then:
		Summ.count() == 1
		SumPartialValue.count() == 0
	}	
	
	def "save sum updates timestamp"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def sum = newSum("1", CODE(1))
		sumController = new SummController()
		def time1 = sum.timestamp
		
		when:
		sumController.params.id = sum.id
		sumController.save()
		
		then:
		Summ.count() == 1
		!Summ.list()[0].timestamp.equals(time1)
	}	
}
