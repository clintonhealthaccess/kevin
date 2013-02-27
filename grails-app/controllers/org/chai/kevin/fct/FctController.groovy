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
import org.chai.kevin.reports.ReportExportService
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService
import org.chai.kevin.reports.ReportService.ReportType;
import org.chai.kevin.util.Utils;

class FctController extends AbstractController {

	def fctService;
	def reportExportService;
	
	/**
	* This returns the fct target passed as a parameter if it belongs to the
	* given program. Otherwise, it returns the first fct target that has target options for the program.
	*
	* @param program
	* @return
	*/
	public FctTarget getFctTarget(def program){
		def fctTarget = null
		
		if (program == null) return fctTarget
		
		if(params.int('fctTarget') != null){
			try {
				fctTarget = FctTarget.get(params.int('fctTarget'))
			} catch (Exception e) {
				fctTarget = null
			}
			
			// reset the target if it doesn't belong to the right program
			if (fctTarget != null) {
				if (!fctTarget.program.equals(program)) fctTarget = null
			}
		}
		
		// set the target to the first of the program if null
		// TODO this crashes if there are no fct targets in the system
		if(fctTarget == null){
			def targets = fctService.getFctTargetsWithOptions(program)			
			if(targets != null && !targets.empty){
				targets.sort({it.order})
				fctTarget = targets.first()			
			}
			else{
				if (log.isDebugEnabled()) {
					log.debug("fct.view, program:"+program+", fctTarget:"+fctTarget+" must have at least 1 fctTargetOption")
				}
			}
		}

		if (log.isDebugEnabled()) log.debug("fct.view, program:"+program+", getFctTarget:"+fctTarget)

		return fctTarget
	}	
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("fct.view, params:"+params)

		// entities form params
		Period period = getPeriod()
		ReportProgram program = getProgram(FctTarget.class)
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		FctTarget fctTarget = getFctTarget(program)
		ReportType reportType = getReportType()
		
		// skip levels
		def locationSkipLevels = fctService.getSkipLocationLevels()
		
		def redirected = false
		// we check if we need to redirect, but only when some of the high level filters are null
		if (period != null && program != null && location != null && fctTarget != null) {
			
			// building params for redirection checks
			def reportParams = ['period':period.id, 'program':program.id, 'location':location.id,
								'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(), 
								'fctTarget':fctTarget?.id,
								'reportType':reportType.toString().toLowerCase()]
			
			// we check if we should redirect
			def newParams = redirectIfDifferent(reportParams)
			if(newParams != null && !newParams.empty) {
				redirected = true
				if (log.isDebugEnabled()) log.debug('fct.view, redirecting, params: '+newParams);
				redirect(controller: 'fct', action: 'view', params: newParams)
			}
		}
		
		if (!redirected) {
			def fctTable = null
			if (fctTarget != null) {	
				fctTable = fctService.getFctTable(location, fctTarget, period, dataLocationTypes, reportType);
			}	
		
			def locationTree = null
			if (location != null) {	
				// entire location tree to filter stuff that has no data for tree table
				locationTree = location.collectTreeWithDataLocations(locationSkipLevels, dataLocationTypes).asList()
			}
				
			if (log.isDebugEnabled()) log.debug('fct: '+fctTable+", root program: "+program+", root location: "+location)				
			[
				currentTarget: fctTarget,
				currentPeriod: period,
				currentProgram: program,
				currentLocation: location,
				currentLocationTypes: dataLocationTypes,
				
				fctTable: fctTable,
				selectedTargetClass: FctTarget.class,
				locationTree: locationTree,
				locationSkipLevels: locationSkipLevels,
				currentView: reportType,
			]
		}
	}
	
	def export = {
		if (log.isDebugEnabled()) log.debug("fct.export, params:"+params)

		Period period = getPeriod()
		ReportProgram program = getProgram(FctTarget.class)
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		FctTarget fctTarget = getFctTarget(program)
		ReportType reportType = getReportType()
		
		def reportParams = ['period':period.id, 'program':program.id, 'location':location.id,
							'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(),
							'fctTarget':fctTarget?.id,
							'reportType':reportType.toString().toLowerCase()]
		def newParams = redirectIfDifferent(reportParams)
		
		if(newParams != null && !newParams.empty){
			 redirect(controller: 'fct', action: 'view', params: newParams)
		}
		else {
			def fctTable = null
			if (fctTarget != null)
				fctTable = fctService.getFctTable(location, fctTarget, period, dataLocationTypes, reportType);
			
			if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" program: "+program+", location: "+location)
			
			String report = message(code:'fct.title');
			String filename = reportExportService.getReportExportFilename(report, location, program, period);
			File csvFile = reportExportService.getReportExportFile(filename, fctTable, location);
			def zipFile = Utils.getZipFile(csvFile, filename)
				
			if(zipFile.exists()){
				response.setHeader("Content-disposition", "attachment; filename=" + zipFile.getName());
				response.setContentType("application/zip");
				response.setHeader("Content-length", zipFile.length().toString());
				response.outputStream << zipFile.newInputStream()
			}
		}
	}
}
