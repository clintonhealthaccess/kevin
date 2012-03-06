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
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("fct.view, params:"+params)

		Period period = getPeriod();
		LocationEntity entity = LocationEntity.get(params.int('location'));
		ReportProgram program = ReportProgram.get(params.int('program'));
		LocationLevel level = LocationLevel.get(params.int('level'));
		Set<DataEntityType> locationTypes = getLocationTypes();
		
		FctTable fctTable = null;

		if (period != null && program != null && entity != null && level != null) {
			fctTable = fctService.getFctTable(entity, program, period, level, locationTypes);
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" root program: "+program)				
		
		[
			fctTable: fctTable,
			currentPeriod: period,
			currentProgram: program,
			currentLocation: entity,
			currentLevel: level,
			currentLocationTypes: locationTypes,
			locationTypes: locationService.listTypes(),
			programs: ReportProgram.list()
		]
	}
}
