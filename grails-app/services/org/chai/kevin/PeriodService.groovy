package org.chai.kevin

import org.hisp.dhis.period.Period;

class PeriodService {

	def sessionFactory
	
	List<Period> getPeriods() {
		List<Period> periods = new ArrayList(Period.list())
		return periods.sort {
			a, b -> a.startDate.compareTo b.startDate
		}
	}
	
	def getDefaultPeriodType() {
		return sessionFactory.currentSession.createQuery(
			"from PeriodType"	
		).setMaxResults(1).uniqueResult()
	}
	
}
