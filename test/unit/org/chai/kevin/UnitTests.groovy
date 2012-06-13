package org.chai.kevin

import org.chai.kevin.util.JSONUtils;

import grails.plugin.spock.UnitSpec;

abstract class UnitTests extends UnitSpec {

	static j(def map) {
		return new Translation(jsonText: JSONUtils.getJSONFromMap(map));
	}
	
}
