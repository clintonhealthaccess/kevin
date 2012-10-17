package org.chai.kevin.fct

import java.util.Collections;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.AbstractController
import org.chai.kevin.LanguageService
import org.chai.kevin.Period
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.kevin.reports.ReportEntity
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService
import org.chai.kevin.util.Utils.ReportType;

class FctController extends AbstractController {

	def fctService;
	
	public FctTarget getFctTarget(def program){
		def fctTarget = null
		if(params.int('fctTarget') != null){
			fctTarget = FctTarget.get(params.int('fctTarget'))
			
			// reset the target if it doesn't belong to the right program
			if(fctTarget != null){
				if(!fctTarget.program.equals(program))
					fctTarget = null
			}			
		}
		
		// set the target to the first of the program if null
		if(fctTarget == null){
			def targets = fctService.getFctTargetsWithOptions(program)			
			if(targets != null && !targets.empty){
				Collections.sort(targets);
				fctTarget = targets.first()			
			}
		}
		return fctTarget
	}	
	
	public Set<FctTargetOption> getFctIndicators(def target, def program){
		Set<FctTargetOption> fctIndicators = new HashSet<FctTargetOption>()
			
		if(params.list('indicators') != null && !params.list('indicators').empty){
			def indicators = params.list('indicators')
			fctIndicators.addAll(indicators.collect{ NumberUtils.isNumber(it as String) ? FctTargetOption.get(it) : null } - null)
			
			// reset the indicators if any of them don't belong to the right target
			if(fctIndicators != null){
				for(FctTargetOption fctIndicator in fctIndicators){
					if(!fctIndicator.target.equals(target)){
						fctIndicators = null
						break;
					}
				}
			}
		}				
		
		// set the indicators to the target options if null
		if(fctIndicators == null || fctIndicators.empty){
			if(target == null) target = getFctTarget(program);
			if(target != null){
				def targetOptions = target.targetOptions
				if(targetOptions != null && !targetOptions.empty)
					fctIndicators.addAll(targetOptions)
			}
		}
		
		return fctIndicators
	}
	
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
		Set<FctTargetOption> fctIndicators = getFctIndicators(fctTarget, program)
		
		ReportType reportType = getReportType()
		def viewSkipLevels = []
		
		def locationSkipLevels = fctService.getSkipLocationLevels()
		def locationTree = location.collectLocationTreeWithData(locationSkipLevels, dataLocationTypes, false).asList()

		def reportParams = ['period':period.id, 'program':program.id, 'location':location.id,
							'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(), 
							'fctTarget':fctTarget?.id,
							//'indicators':fctIndicators != null ? fctIndicators.collect{ it.id }.sort() : null,
							'reportType':reportType.toString().toLowerCase()]
		def newParams = redirectIfDifferent(reportParams)
		
		if(newParams != null && !newParams.empty) redirect(controller: 'fct', action: 'view', params: newParams)
		
		else {
			FctTable fctTable = null
			if (fctTarget != null)		
				fctTable = fctService.getFctTable(location, program, fctTarget, period, dataLocationTypes, reportType);
			
			if (log.isDebugEnabled()) log.debug('fct: '+fctTable+", root program: "+program+", root location: "+location)				
			[
				fctTable: fctTable,
				currentTarget: fctTarget,
				currentIndicators: fctIndicators,
				currentPeriod: period,
				currentProgram: program,
				selectedTargetClass: FctTarget.class,
				currentLocation: location,
				locationTree: locationTree,
				currentLocationTypes: dataLocationTypes,		
				locationSkipLevels: locationSkipLevels,
				currentView: reportType,
				viewSkipLevels: viewSkipLevels
			]
		}
	}		
}
