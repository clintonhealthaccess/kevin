package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.value.AggregationPartialValue;

class AggregationControllerSpec extends IntegrationTests {

	def aggregationController
	
	def "save works"() {
		setup:
		aggregationController = new AggregationController()
		
		when:
		aggregationController.params.code = CODE(1)
		aggregationController.params.expression = "1"
		aggregationController.saveWithoutTokenCheck()
		
		then:
		Aggregation.count() == 1
		Aggregation.list()[0].code == CODE(1)
		Aggregation.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		aggregationController = new AggregationController()
		
		when:
		aggregationController.params.code = CODE(1)
		aggregationController.saveWithoutTokenCheck()
		
		then:
		Aggregation.count() == 0
	}
	
	def "delete validation deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def aggregation = newAggregation("1", CODE(1))
		newAggregationPartialValue(aggregation, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), "", v("1"))
		aggregationController = new AggregationController()
		
		when:
		aggregationController.params.id = aggregation.id
		aggregationController.delete()
		
		then:
		Aggregation.count() == 0
		AggregationPartialValue.count() == 0
	}
		
	def "save aggregation deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def aggregation = newAggregation("1", CODE(1))
		newAggregationPartialValue(aggregation, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), "", v("1"))
		aggregationController = new AggregationController()
		
		when:
		aggregationController.params.id = aggregation.id
		aggregationController.save()
		
		then:
		Aggregation.count() == 1
		AggregationPartialValue.count() == 0
	}
	
	def "save aggregation updates timestamp"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def aggregation = newAggregation("1", CODE(1))
		aggregationController = new AggregationController()
		def time1 = aggregation.timestamp
		
		when:
		aggregationController.params.id = aggregation.id
		aggregationController.save()
		
		then:
		Aggregation.count() == 1
		!Aggregation.list()[0].timestamp.equals(time1)
	}
}
