package org.chai.kevin.exports

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Summ;
import org.chai.kevin.data.Type;
import org.chai.location.Location;

class DataExportDomainSpec extends IntegrationTests {

	def "deleting a DataElementExport does not cascade"() {
		
		setup:
		setupLocationTree();
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER());
		
		def exporter = newDataElementExport(CODE(1), ["en":"Testing Seach One"], 
			s([period]), [HEALTH_CENTER_GROUP, DISTRICT_HOSPITAL_GROUP], 
			s([Location.findByCode(RWANDA)]), s([dataElement]));
		
		when:
		exporter.delete()
		
		then:
		Period.count() == 1
		Location.count() == 3
		RawDataElement.count() == 1
		
	}
	
	def "deleting a CalculationExport does not cascade"() {
		
		setup:
		setupLocationTree();
		def period = newPeriod()
		def sum = newSum("1", CODE(1))
		
		def exporter = newCalculationExport(CODE(1), ["en":"Testing Seach One"],
			s([period]), [HEALTH_CENTER_GROUP, DISTRICT_HOSPITAL_GROUP],
			s([Location.findByCode(RWANDA)]), s([sum]));
		
		when:
		exporter.delete()
		
		then:
		Period.count() == 1
		Location.count() == 3
		Summ.count() == 1
		
	}
	
}
