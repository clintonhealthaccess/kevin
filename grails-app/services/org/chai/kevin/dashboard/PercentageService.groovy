package org.chai.kevin.dashboard

import org.chai.kevin.Objective;
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class PercentageService {
	
    static transactional = true

	DashboardPercentage getPercentage(OrganisationUnit organisationUnit, DashboardEntry entry, Period period) {
		return DashboardPercentage.withCriteria(uniqueResult: true) {
			and {
				eq('period', period)
				eq('organisationUnit', organisationUnit)
				eq('entry', entry)
			}
		}
	}
	
	DashboardPercentage updatePercentage(DashboardPercentage percentage) {
		if (log.isInfoEnabled()) log.info('updatePercentage(Percentage='+percentage+')')
		
		DashboardPercentage current = getPercentage(percentage.organisationUnit, percentage.entry, percentage.period);
		if (current == null) {
			current = percentage
		}
		else {
			current.value = percentage.value
			current.hasMissingExpression = percentage.hasMissingExpression
			current.hasMissingValue	= percentage.hasMissingValue
			current.status = percentage.status
		}
		if (current.value == Double.NaN) {
			if (log.isWarnEnabled()) log.warn('trying to store NaN: '+percentage)
			current.value = -1
		}
		current.save(flush: true)
		
		DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP.get().clear()
		return current;
	}
	
}
