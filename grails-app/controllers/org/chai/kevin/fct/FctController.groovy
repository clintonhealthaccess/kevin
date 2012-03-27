package org.chai.kevin.fct

import org.chai.kevin.AbstractController
import org.chai.kevin.Period
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService

class FctController extends AbstractController {

	def fctService;
	
	public FctTarget getFctTarget(def program){
		def fctTarget = null
		if(params.int('fctTarget') != null)
			fctTarget = FctTarget.get(params.int('fctTarget'))
		else{
			def targets = reportService.getReportTargets(FctTarget.class, program)
			if(targets != null && !targets.empty && 
				targets.first().targetOptions != null && !targets.first().targetOptions.empty)
				fctTarget = targets.first()				
		}
		return fctTarget
	}
	
	def getFctTargets(def program){
		def targetsWithOptions = []
		def targets = reportService.getReportTargets(FctTarget.class, program)
		for(FctTarget target : targets){
			if(target.targetOptions != null && !target.targetOptions.empty)
				targetsWithOptions.add(target)
		}
		return targetsWithOptions
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
		ReportProgram program = getProgram(FctTarget.class)
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()

		FctTarget fctTarget = getFctTarget(program)			
		def skipLevels = fctService.getSkipLocationLevels()
		def locationTree = location.collectTreeWithDataLocations(skipLevels, dataLocationTypes).asList()
		LocationLevel level = locationService.getLevelAfter(location.getLevel(), skipLevels)
		
		FctTable fctTable = null;
		if (period != null && program != null && fctTarget != null && location != null && dataLocationTypes != null) {					
			fctTable = fctService.getFctTable(location, program, fctTarget, period, level, dataLocationTypes);
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" root program: "+program)				
		
		[
			fctTable: fctTable,
			currentPeriod: period,
			currentProgram: program,
			selectedTargetClass: FctTarget.class,
			currentLocation: location,
			locationTree: locationTree,
			currentLocationTypes: dataLocationTypes,
			currentTarget: fctTarget,
			fctTargets: getFctTargets(program),		
			skipLevels: skipLevels,
			currentChildLevel: level
		]
	}
}
