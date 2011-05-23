package org.chai.kevin

import java.util.Date;

import grails.plugin.spock.IntegrationSpec;
import groovy.sql.Sql;

import org.apache.commons.logging.LogFactory;
import org.chai.kevin.dashboard.DashboardCalculation;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.DataElement;
import org.chai.kevin.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;

abstract class IntegrationTests extends IntegrationSpec {
	
	def cleanup() {

	}
	
	static def getOrganisationUnitLevels(def levels) {
		def result = []
		for (def level : levels) {
			result.add OrganisationUnitLevel.findByLevel(new Integer(level).intValue())
		}
		return result;
	}
	
	static def getOrganisation(def name) {
		return new Organisation(OrganisationUnit.findByName(name))
	}
	
	static def getOrganisations(def names) {
		def result = []
		for (String name : names) {
			result.add(getOrganisation(name))
		}
		return result
	}
}
