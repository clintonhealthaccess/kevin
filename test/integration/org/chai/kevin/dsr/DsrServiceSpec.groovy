package org.chai.kevin.dsr

import org.chai.kevin.data.Type

class DsrServiceSpec extends DsrIntegrationTests {

	def reportService
	
	def "test normal dsr service"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def objective = newDsrObjective(CODE(1))
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(2), dataElement, [], objective)
		def organisation = getOrganisation(BURERA)
		def dsrTable = null
		refresh()
		
		when:
		dsrTable = reportService.getDsrTable(organisation, objective, period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		dsrTable.getDsrValue(getOrganisation(BUTARO), target) != null
		dsrTable.getOrganisationMap().get(getOrganisation(BURERA)).equals([getOrganisation(BUTARO), getOrganisation(KIVUYE)])

		when:
		dsrTable = reportService.getDsrTable(organisation, objective, period, new HashSet([DISTRICT_HOSPITAL_GROUP]))
		
		then:
		dsrTable.getOrganisationMap().get(getOrganisation(BURERA)).equals([getOrganisation(BUTARO)])

	}
	
	def "test dsr formatting"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def objective = newDsrObjective(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, format, [(DISTRICT_HOSPITAL_GROUP), (HEALTH_CENTER_GROUP)], objective)
		refreshNormalizedDataElement()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = reportService.getDsrTable(organisation, objective, period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		dsrTable.getDsrValue(getOrganisation(BUTARO), target).value == value
		
		where:
		format	| value
		"#"		| "10"
		""		| "10"
		"#.0"	| "10.0"
		
	}

	def "test dsr with no groups should return no value"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def objective = newDsrObjective(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [], objective)
		refreshNormalizedDataElement()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = reportService.getDsrTable(organisation, objective, period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		dsrTable.getDsrValue(getOrganisation(organisationName), target).value == null
		
		where:
		organisationName << [BUTARO, KIVUYE]
		
	}
	
	def "test dsr with groups"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def objective = newDsrObjective(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [(DISTRICT_HOSPITAL_GROUP)], objective)
		refreshNormalizedDataElement()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = reportService.getDsrTable(organisation, objective, period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		dsrTable.getDsrValue(getOrganisation(BUTARO), target).value == "10"		
		dsrTable.getDsrValue(getOrganisation(KIVUYE), target).value == null
		
	}
	
}
