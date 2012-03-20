package org.chai.kevin

import org.chai.kevin.data.Type
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.hisp.dhis.period.Period;

class FilterTagLibSpec extends IntegrationTests {

	def filterTagLib
	
	def "location type param value size sets location type param value"(){
		setup:
		filterTagLib = new FilterTagLib()
		
		def oneLocationType = ["2343"]
		def twoLocationTypes = ["23", "43"]
		
		expect:
		filterTagLib.updateLinkParams(['locationTypes':"2343"]).equals( 
										['locationTypes':oneLocationType])
		filterTagLib.updateLinkParams(['locationTypes':["23","43"]]).equals(
										['locationTypes':twoLocationTypes])
	}
	
	def "location sets missing level"() {
		setup:
		setupLocationTree()
		filterTagLib = new FilterTagLib()
		
		expect:
		filterTagLib.createLinkByFilter([
			controller:'controller',
			action:'action',
			params: [location: LocationEntity.findByCode(RWANDA).id+'', filter: 'location']
		], null) == "/controller/action?level="+LocationLevel.findByCode(PROVINCE).id+"&location="+LocationEntity.findByCode(RWANDA).id+"&filter=location"
	}
	
	def "dsr-fct tab sets dashboard-specific program to root program"() {
		setup:
		setupProgramTree()
		def period = Period.list()[0]
		def dashboardProgram = ReportProgram.findByCode(PROGRAM1)
		filterTagLib = new FilterTagLib()
		refresh()
		
		expect:
		filterTagLib.createLinkByTab([
			controller:'dsr',
			action:'action',
			params: [program: dashboardProgram.id+'']]) == "/dsr/action?program="+ReportProgram.findByCode(ROOT).id
		filterTagLib.createLinkByTab([
			controller:'fct',
			action:'action',
			params: [program: dashboardProgram.id+'']]) == "/fct/action?program="+ReportProgram.findByCode(ROOT).id
	}
	
	def "dashboard tab sets dsr-fct-specific program to root program"(){
		setup:
		setupProgramTree()
		def period = Period.list()[0]
		
		def fctProgram = newReportProgram(CODE("fctProgram"), ReportProgram.findByCode(PROGRAM1))
		def fctNormalizedDataElement = newNormalizedDataElement(CODE(7), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))		
		def fctSum = newSum("\$"+fctNormalizedDataElement.id, CODE(8))
		def fctTarget = newFctTarget(CODE("fctTarget"), fctSum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], fctProgram)
		
		def dsrProgram = newReportProgram(CODE("dsrProgram"), ReportProgram.findByCode(PROGRAM1))
		def dsrNormalizedDataElement = newNormalizedDataElement(CODE(9), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def dsrTarget = newDsrTarget(CODE("dsrTarget"), dsrNormalizedDataElement, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], dsrProgram)
		
		filterTagLib = new FilterTagLib()
		refresh()
		
		expect:
		filterTagLib.createLinkByTab([
			controller:'dashboard',
			action:'action',
			params: [program: fctProgram.id+'']]) == "/dashboard/action?program="+ReportProgram.findByCode(ROOT).id
		filterTagLib.createLinkByTab([
			controller:'dashboard',
			action:'action',
			params: [program: dsrProgram.id+'']]) == "/dashboard/action?program="+ReportProgram.findByCode(ROOT).id
	}

}