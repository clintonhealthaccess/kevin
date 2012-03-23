package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.SumValue;

class InfoServiceSpec extends IntegrationTests {

	def infoService
	
	def "get info for normalized data element"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("1"))
		def normalizedDataEement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		refreshNormalizedDataElement()
				
		when:
		def normalizedDataElementInfo = infoService.getNormalizedDataElementInfo(normalizedDataEement, DataLocation.findByCode(BUTARO), period)
		
		then:
		normalizedDataElementInfo != null
		normalizedDataElementInfo.getRawDataElements().equals([rawDataElement])
		normalizedDataElementInfo.getRawDataElementValue(rawDataElement).equals(rawDataElementValue)
	}
	
	def "get info for calculation"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def normalizedDataElementValue = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def sumPartialValue = newSumPartialValue(sum, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		
		when:
		def calculationInfo = infoService.getCalculationInfo(sum, Location.findByCode(BURERA), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		calculationInfo != null
		s(calculationInfo.getLocations()).equals(s([DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)]))
		calculationInfo.getDataElements().equals([normalizedDataElement])
		calculationInfo.getValue(DataLocation.findByCode(BUTARO)).equals(new SumValue([sumPartialValue], sum, period, DataLocation.findByCode(BUTARO)))
		calculationInfo.getValue(DataLocation.findByCode(BUTARO), normalizedDataElement).equals(normalizedDataElementValue)
		
	}
	
}
