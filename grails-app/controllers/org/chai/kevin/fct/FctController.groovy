package org.chai.kevin.fct

import org.chai.kevin.AbstractController
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.period.Period
import org.hisp.dhis.period.Period;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class FctController extends AbstractController {

	FctService fctService;
	
//	public FctTargetCategory getFctTargetCategory(def program){
//		def fctTargetCategory = null
//		if(params.int('fctCategory') != null)
//			fctTargetCategory = FctTargetCategory.get(params.int('fctCategory'))
//		else{
//			def categories = fctService.getTargetCategories(program)
//			if(categories != null && !categories.empty)
//				fctTargetCategory = categories.first()
//		}
//		return fctTargetCategory
//	}
	
//	def getLevel(){
//		LocationLevel level = null
//		level = LocationLevel.get(params.int('level'));
//		if(level == null) level = LocationLevel.findByCode(ConfigurationHolder.config.site.level)
//		return level
//	}
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("fct.view, params:"+params)

		Period period = getPeriod()
		ReportProgram program = getProgram()
		LocationEntity location = getLocation()
		Set<DataEntityType> locationTypes = getLocationTypes()
//		LocationLevel level = getLevel()		
		
		def skipLevels = fctService.getSkipLocationLevels()
		
		FctTable fctTable = null;

//		if (period != null && program != null && location != null && locationTypes != null && level != null) {
//			fctTable = fctService.getFctTable(location, program, period, level, locationTypes);
//		}

		if (period != null && program != null && location != null && locationTypes != null) {
			fctTable = fctService.getFctTable(location, program, period, null, locationTypes);
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" root program: "+program)				
		
		[
			fctTable: fctTable,
			currentPeriod: period,
			currentProgram: program,
			currentTarget: FctTarget.class,
			currentLocation: location,
//			currentLevel: level,
			currentLocationTypes: locationTypes,
			skipLevels: skipLevels			
		]
	}
}
