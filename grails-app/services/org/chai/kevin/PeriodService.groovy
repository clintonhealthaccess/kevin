package org.chai.kevin

class PeriodService {

	static transactional = true
	
	def sessionFactory
	
	List<Period> listPeriods() {
		List<Period> periods = new ArrayList(Period.list([cache: true]))
		return periods.sort {
			a, b -> a.startDate.compareTo b.startDate
		}
	}
	
	Period getPeriodByCode(def code){
		return Period.findByCode(code);
	}
	
}
