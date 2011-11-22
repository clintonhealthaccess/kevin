package org.chai.kevin.maps

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;

abstract class MapsIntegrationTests extends IntegrationTests {

	def newMapsTarget(def code, def calculation) {
		return new MapsTarget(code: code, calculation: calculation).save(failOnError: true)
	}
	
		
}
