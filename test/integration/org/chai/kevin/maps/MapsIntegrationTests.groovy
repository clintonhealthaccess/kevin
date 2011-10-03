package org.chai.kevin.maps

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.maps.MapsTarget.MapsTargetType;

abstract class MapsIntegrationTests extends IntegrationTests {

	def newMapsTarget(def code, def type, def expressionOrCalculation) {
		if (type == MapsTargetType.AVERAGE) {
			return new MapsTarget(code: code, type: type, calculation: expressionOrCalculation).save(failOnError: true)
		}
		if (type == MapsTargetType.AGGREGATION) {
			return new MapsTarget(code: code, type: type, expression: expressionOrCalculation).save(failOnError: true)
		}
	}
	
		
}
