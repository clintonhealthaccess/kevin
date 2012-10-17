package org.chai.kevin

import org.chai.kevin.data.Type
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;

class FilterTagLibSpec extends IntegrationTests {

	def filterTagLib	
	
//	def "location sets missing level with null skip levels"() {
//		setup:
//		setupLocationTree()
//		filterTagLib = new FilterTagLib()
//		
//		expect:
//		filterTagLib.createLinkByFilter([
//			controller:'controller',
//			action:'action',
//			params: [location:Location.findByCode(RWANDA).id+'',filter: 'location'],
//			skipLevels: null], null) == 
//		"/controller/action?level="+LocationLevel.findByCode(PROVINCE).id+"&location="+Location.findByCode(RWANDA).id+"&filter=location"
//	}
//	
//	def "location sets missing level unaffected by skip levels"() {
//		setup:
//		setupLocationTree()
//		filterTagLib = new FilterTagLib()
//		
//		expect:		
//		filterTagLib.createLinkByFilter([
//			controller:'controller',
//			action:'action',
//			params: [location:Location.findByCode(RWANDA).id+'', filter:'location'],
//			skipLevels: new HashSet([LocationLevel.findByCode(SECTOR)])], null) == 
//		"/controller/action?level="+LocationLevel.findByCode(PROVINCE).id+"&location="+Location.findByCode(RWANDA).id+"&filter=location"
//	}
//	
//	def "location sets level with skip levels"() {
//		setup:
//		setupLocationTree()
//		filterTagLib = new FilterTagLib()
//		
//		expect:
//		filterTagLib.createLinkByFilter([
//			controller:'controller',
//			action:'action',
//			params: [location:Location.findByCode(RWANDA).id+'', level:LocationLevel.findByCode(NATIONAL).id+'', filter: 'location'],
//			locationSkipLevels: new HashSet([LocationLevel.findByCode(PROVINCE)])], null) == 
//		"/controller/action?level="+LocationLevel.findByCode(DISTRICT).id+"&location="+Location.findByCode(RWANDA).id+"&filter=location"
//	}
//	
//	def "level does not set missing location with null skip levels"() {
//		setup:
//		setupLocationTree()
//		filterTagLib = new FilterTagLib()
//				
//		expect:
//		filterTagLib.createLinkByFilter([
//			controller:'controller',
//			action:'action',
//			params: [level:LocationLevel.findByCode(PROVINCE).id+'', filter:'level'],
//			skipLevels: null], null) == 
//		"/controller/action?level="+LocationLevel.findByCode(PROVINCE).id+"&filter=level"		
//	}
//	
//	def "level does not set missing location unaffected by skip levels"() {
//		setup:
//		setupLocationTree()
//		filterTagLib = new FilterTagLib()
//		
//		expect:
//		filterTagLib.createLinkByFilter([
//			controller:'controller',
//			action:'action',
//			params: [level:LocationLevel.findByCode(PROVINCE).id+'', filter:'level'],
//			skipLevels: new HashSet([LocationLevel.findByCode(SECTOR)])], null) == 
//		"/controller/action?level="+LocationLevel.findByCode(PROVINCE).id+"&filter=level"
//	}
//	
//	def "level sets location with skip levels"() {
//		setup:
//		setupLocationTree()
//		filterTagLib = new FilterTagLib()
//		
//		expect:
//		filterTagLib.createLinkByFilter([
//			controller:'controller',
//			action:'action',
//			params: [location:Location.findByCode(BURERA).id+'', level:LocationLevel.findByCode(DISTRICT).id+'', filter:'level'],
//			locationSkipLevels: new HashSet([LocationLevel.findByCode(PROVINCE)])], null) == 
//		"/controller/action?level="+LocationLevel.findByCode(DISTRICT).id+"&location="+Location.findByCode(RWANDA).id+"&filter=level"
//	}
}