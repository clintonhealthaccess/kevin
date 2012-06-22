package org.chai.kevin.fct

import java.util.Collections;

import org.chai.kevin.AbstractController
import org.chai.kevin.LanguageService
import org.chai.kevin.Period
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.reports.ReportEntity
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService

class FctController extends AbstractController {

	def fctService;
	
	public FctTarget getFctTarget(def program){
		def fctTarget = null
		if(params.int('fctTarget') != null){
			fctTarget = FctTarget.get(params.int('fctTarget'))
			if(fctTarget != null){
				if(!fctTarget.program.equals(program))
					fctTarget = null
			}			
		}
		
		if(fctTarget == null){
			def targets = fctService.getFctTargets(program)			
			if(targets != null && !targets.empty){
				Collections.sort(targets);
				fctTarget = targets.first()			
			}	
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
		ReportProgram program = getProgram(FctTarget.class)
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		FctTarget fctTarget = getFctTarget(program)
		
		def skipLevels = fctService.getSkipLocationLevels()
		def locationTree = location.collectTreeWithDataLocations(skipLevels, dataLocationTypes).asList()
		LocationLevel level = locationService.getLevelAfter(location.getLevel(), skipLevels)
		
		FctTable fctTable = null;
		def fctDescriptions = null;
		if (period != null && program != null && fctTarget != null && location != null && dataLocationTypes != null) {

			def reportParams = ['period':period.id, 'program':program.id, 'location':location.id,
				'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(), 'fctTarget':fctTarget?fctTarget.id:null]
			def redirectParams = getRedirectParams(reportParams)
			def newParams = redirectIfDifferent(redirectParams)
			if(newParams != null && !newParams.empty)
				redirect(controller: 'fct', action: 'view', params: newParams)
						
			fctTable = fctService.getFctTable(location, program, fctTarget, period, level, dataLocationTypes);
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+", root program: "+program+", root location: "+location)				
		
		[
			fctTable: fctTable,
			currentTarget: fctTarget,
			currentPeriod: period,
			currentProgram: program,
			selectedTargetClass: FctTarget.class,
			currentLocation: location,
			locationTree: locationTree,
			currentLocationTypes: dataLocationTypes,		
			skipLevels: skipLevels,
			currentChildLevel: level
		]
	}		
}
