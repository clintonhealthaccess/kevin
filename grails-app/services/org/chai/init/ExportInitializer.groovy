package org.chai.init

import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.exports.DataElementExport;
import org.chai.location.Location;

class ExportInitializer {

	static def createDataElementExports() {
		if (!DataElementExport.count()) {
			new DataElementExport(code: 'hr_rwanda_export', date: new Date(), typeCodes: ['district_hospital', 'health_center'])
			.addToLocations(Location.findByCode('rwanda'))
			.addToPeriods(Period.findByCode('period1'))
			.addToPeriods(Period.findByCode('period2'))
			.addToDataElements(RawDataElement.findByCode('human_resources'))
			.save(failOnError: true)
		}
	}
	
}
