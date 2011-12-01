package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.SumValue;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class InfoServiceSpec extends IntegrationTests {

	def infoService
	
	def "get info for normalized data element"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, OrganisationUnit.findByName(BUTARO), v("1"))
		def normalizedDataEement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		refreshNormalizedDataElement()
				
		when:
		def normalizedDataElementInfo = infoService.getNormalizedDataElementInfo(normalizedDataEement, getOrganisation(BUTARO), period)
		
		then:
		normalizedDataElementInfo != null
		normalizedDataElementInfo.getRawDataElements().equals([rawDataElement])
		normalizedDataElementInfo.getRawDataElementValue(rawDataElement).equals(rawDataElementValue)
	}
	
	def "get info for calculation"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def normalizedDataElementValue = newNormalizedDataElementValue(normalizedDataElement, OrganisationUnit.findByName(BUTARO), period, Status.VALID, v("1"))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def sumPartialValue = newSumPartialValue(sum, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, v("1"))
		
		when:
		def calculationInfo = infoService.getCalculationInfo(sum, getOrganisation(BURERA), period, s([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		calculationInfo != null
		calculationInfo.getOrganisations().equals([getOrganisation(KIVUYE), getOrganisation(BUTARO)])
		calculationInfo.getDataElements().equals([normalizedDataElement])
		calculationInfo.getValue(getOrganisation(BUTARO)).equals(new SumValue([sumPartialValue], sum, period, OrganisationUnit.findByName(BUTARO)))
		calculationInfo.getValue(getOrganisation(BUTARO), normalizedDataElement).equals(normalizedDataElementValue)
		
	}
	
}
