package org.chai.kevin

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.cost.CostObjective;
import org.chai.kevin.cost.CostTarget;
import org.chai.kevin.dashboard.DashboardObjectiveService;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrObjectiveService
import org.chai.kevin.maps.MapsTarget;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

abstract class AbstractReportController {

	DashboardObjectiveService dashboardObjectiveService;
	OrganisationService organisationService;
	DsrObjectiveService dsrObjectiveService;
	
	protected def getObjective() {
		Translatable objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = DashboardObjective.get(params['objective']);
				if (objective == null) {
					objective = DashboardTarget.get(params['objective']);
				}
			}
			if (objective == null) {
				objective = dashboardObjectiveService.getRootObjective()
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}
	
	protected def getCostObjective() {
		CostObjective objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = CostObjective.get(params['objective']);
			}
			if (objective == null) {
				// TODO what if there are no objectives ?
				objective = CostObjective.list()[0]
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}
	
	protected def getCostTarget() {
		CostTarget target = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				target = CostTarget.get(params['objective']);
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return target
	}
	
	protected def getMapsTarget() {
		MapsTarget target = null
		try {
			if (NumberUtils.isNumber(params['target'])) {
				target = MapsTarget.get(params['target']);
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return target
	}
	
	protected def getStrategicObjective() {
		Translatable objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = DashboardObjective.get(params['objective']);
			}
			if (objective == null) {
				objective = dashboardObjectiveService.getRootObjective()
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}
	
	protected def getOrganisation(def defaultIfNull) {
		Organisation organisation = null;
		try {
			if (NumberUtils.isNumber(params['organisation'])) {
				organisation = organisationService.getOrganisation(new Integer(params['organisation']))
			}
			if (organisation == null && defaultIfNull) {
				organisation = organisationService.getRootOrganisation();
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return organisation
	}
	
	protected def getOrganisationUnitLevel() {
		OrganisationUnitLevel level = null;
		try {
			if (NumberUtils.isNumber(params['level'])) {
				level = OrganisationUnitLevel.findByLevel(new Integer(params['level']))
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return level
	}
	
	protected def getPeriod() {
		Period period = null;
		try {
			if (NumberUtils.isNumber(params['period'])) {
				period = Period.get(params['period'])
			}
			if (period == null) {
				period = Period.findAll()[Period.count()-1]
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return period
	}
	
	protected def getStrategicObjectiveDsr() {
		Translatable objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = DsrObjective.get(params['objective']);
			}
			
			if (objective == null) {
				// TODO what if there are no objectives ?
				objective = DsrObjective.list()[0]
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}
	
	
}
