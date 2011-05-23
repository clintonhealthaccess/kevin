package org.chai.kevin.dashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Translatable;
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.NaturalId;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class PercentageService {
	
	private static final Log log = LogFactory.getLog(PercentageService.class);
	
	private SessionFactory sessionFactory;
	
	@Transactional(readOnly=true)
	public DashboardPercentage getPercentage(OrganisationUnit organisationUnit, DashboardEntry entry, Period period) {
		return (DashboardPercentage)sessionFactory.getCurrentSession().createCriteria(DashboardPercentage.class)
			.add(Restrictions.naturalId()
				.set("period", period)
				.set("organisationUnit", organisationUnit)
				.set("entry", entry)
			).setCacheable(true).setCacheRegion("org.hibernate.cache.DashboardPercentageQueryCache").uniqueResult();
	}
	
	@Transactional(readOnly=false)
	public DashboardPercentage updatePercentage(DashboardPercentage percentage) {
		assert percentage != null;
		if (log.isInfoEnabled()) log.info("updatePercentage(Percentage="+percentage+")");
		
		DashboardPercentage current = getPercentage(percentage.getOrganisationUnit(), percentage.getEntry(), percentage.getPeriod());
		if (current == null) {
			current = percentage;
		}
		else {
			current.setValue(percentage.getValue());
			current.setHasMissingExpression(percentage.isHasMissingExpression());
			current.setHasMissingValue(percentage.isHasMissingValue());
			current.setStatus(percentage.getStatus());
		}
		sessionFactory.getCurrentSession().save(current);
//		sessionFactory.getCurrentSession().flush();
		return current;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
}
