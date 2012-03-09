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

	def fctService;
	
	public FctTarget getFctTarget(def program){
		def fctTarget = null
		if(params.int('fctTarget') != null)
			fctTarget = FctTarget.get(params.int('fctTarget'))
		else{
			def targets = reportService.getReportTargets(FctTarget.class, program)
			if(targets != null && !targets.empty)
				fctTarget = targets.first()
		}
		return fctTarget
	}
	
//	def getLevel(){
//		LocationLevel level = null
//		level = LocationLevel.get(params.int('level'));
//		if(level == null){
//			 rootLocation = locationService.getRootLocation().getLevelAfter()
//			 level = locationService.getLevelAfter(rootLocation.getLevel())
//		}
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
		FctTarget fctTarget = getFctTarget()		
		
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
			currentFctTarget: fctTarget,
			fctTargets: reportService.getReportTargets(FctTarget.class, program),		
			skipLevels: skipLevels			
		]
	}
}
