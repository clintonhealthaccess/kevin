package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.value.SumPartialValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SumControllerSpec extends IntegrationTests {

	def sumController
	
	def "save works"() {
		setup:
		sumController = new SumController()
		
		when:
		sumController.params.code = CODE(1)
		sumController.params.expression = "1"
		sumController.saveWithoutTokenCheck()
		
		then:
		Sum.count() == 1
		Sum.list()[0].code == CODE(1)
		Sum.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		sumController = new SumController()
		
		when:
		sumController.params.code = CODE(1)
		sumController.saveWithoutTokenCheck()
		
		then:
		Sum.count() == 0
	}
	
	def "delete sum deletes values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def sum = newSum("1", CODE(1))
		newSumPartialValue(sum, period, OrganisationUnit.findByName(RWANDA), DISTRICT_HOSPITAL_GROUP, v("1")) 
		sumController = new SumController()
		
		when:
		sumController.params.id = sum.id
		sumController.delete()
		
		then:
		Sum.count() == 0
		SumPartialValue.count() == 0 
	}

	def "save sum deletes values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def sum = newSum("1", CODE(1))
		newSumPartialValue(sum, period, OrganisationUnit.findByName(RWANDA), DISTRICT_HOSPITAL_GROUP, v("1"))
		sumController = new SumController()
		
		when:
		sumController.params.id = sum.id
		sumController.save()
		
		then:
		Sum.count() == 1
		SumPartialValue.count() == 0
	}	
	
	def "save sum updates timestamp"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def sum = newSum("1", CODE(1))
		sumController = new SumController()
		def time1 = sum.timestamp
		
		when:
		sumController.params.id = sum.id
		sumController.save()
		
		then:
		Sum.count() == 1
		!Sum.list()[0].timestamp.equals(time1)
	}	
}
