package org.chai.kevin

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
		def normalizedDataEement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		
		when:
		def normalizedDataElementInfo = infoService.getNormalizedDataElementInfo(normalizedDataEement, getOrganiation(BUTARO), period)
		
		then:
		normalizedDataElementInfo.getRawDataElements().equals([rawDataElement])
		normalizedDataElementInfo.getRawDataElementValue(getOrganiation(BUTARO), rawDataElement).equals(rawDataElementValue)
	}
	
	def "get info for calculation"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def normalizedDataElementValue = newNormalizedDataElementValue(normalizedDataElement, OrganisationUnit.findByName(BUTARO), Status.VALID, v("1"))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def sumPartialValue = newSumPartialValue(sum, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, v("1"))
		
		when:
		def calculationInfo = infoService.getCalculationInfo(sum, getOrganisation(BURERA), period)
		
		then:
		calculationInfo.getOrganisations().equals([getOrganisation(BUTARO), getOrganisation(KIVUYE)])
		calculationInfo.getDataElements().equals([normalizedDataElement])
		calculationInfo.getValue(getOrganisation(BUTARO)).equals(new SumValue([sumPartialValue], sum, period, OrganisationUnit.findByName(BUTARO)))
		calculationInfo.getValue(getOrganisation(BUTARO), normalizedDataElement).equals(normalizedDataElementValue)
		
	}
	
}
