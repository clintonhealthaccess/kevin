package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.SumValue;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.value.Status;

class InfoServiceSpec extends IntegrationTests {

	def infoService
	
	def "get info for normalized data element"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, DataLocationEntity.findByCode(BUTARO), v("1"))
		def normalizedDataEement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		refreshNormalizedDataElement()
				
		when:
		def normalizedDataElementInfo = infoService.getNormalizedDataElementInfo(normalizedDataEement, DataLocationEntity.findByCode(BUTARO), period)
		
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
		def normalizedDataElementValue = newNormalizedDataElementValue(normalizedDataElement, DataLocationEntity.findByCode(BUTARO), period, Status.VALID, v("1"))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def sumPartialValue = newSumPartialValue(sum, period, DataLocationEntity.findByCode(BUTARO), DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		
		when:
		def calculationInfo = infoService.getCalculationInfo(sum, LocationEntity.findByCode(BURERA), period, s([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		calculationInfo != null
		s(calculationInfo.getLocations()).equals(s([DataLocationEntity.findByCode(KIVUYE), DataLocationEntity.findByCode(BUTARO)]))
		calculationInfo.getDataElements().equals([normalizedDataElement])
		calculationInfo.getValue(DataLocationEntity.findByCode(BUTARO)).equals(new SumValue([sumPartialValue], sum, period, DataLocationEntity.findByCode(BUTARO)))
		calculationInfo.getValue(DataLocationEntity.findByCode(BUTARO), normalizedDataElement).equals(normalizedDataElementValue)
		
	}
	
}
