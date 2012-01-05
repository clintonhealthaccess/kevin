package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.SumValue;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.value.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class InfoServiceSpec extends IntegrationTests {

	def infoService
	
	def "get info for normalized data element"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, DataEntity.findByCode(BUTARO), v("1"))
		def normalizedDataEement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		refreshNormalizedDataElement()
				
		when:
		def normalizedDataElementInfo = infoService.getNormalizedDataElementInfo(normalizedDataEement, DataEntity.findByCode(BUTARO), period)
		
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
		def normalizedDataElementValue = newNormalizedDataElementValue(normalizedDataElement, DataEntity.findByCode(BUTARO), period, Status.VALID, v("1"))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def sumPartialValue = newSumPartialValue(sum, period, DataEntity.findByCode(BUTARO), DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		
		when:
		def calculationInfo = infoService.getCalculationInfo(sum, LocationEntity.findByCode(BURERA), period, s([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		calculationInfo != null
		s(calculationInfo.getOrganisations()).equals(s([DataEntity.findByCode(KIVUYE), DataEntity.findByCode(BUTARO)]))
		calculationInfo.getDataElements().equals([normalizedDataElement])
		calculationInfo.getValue(DataEntity.findByCode(BUTARO)).equals(new SumValue([sumPartialValue], sum, period, DataEntity.findByCode(BUTARO)))
		calculationInfo.getValue(DataEntity.findByCode(BUTARO), normalizedDataElement).equals(normalizedDataElementValue)
		
	}
	
}
