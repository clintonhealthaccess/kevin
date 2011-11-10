package org.chai.kevin;

import grails.plugin.springcache.annotations.CacheFlush;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Expression;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class RefreshValueService {

	private final static Log log = LogFactory.getLog(RefreshValueService.class);
	
	private SessionFactory sessionFactory;
	private ExpressionService expressionService;
	private ValueService valueService;
	private GrailsApplication grailsApplication;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void refreshOutdatedExpressionsInTransaction(Expression expression) {
		refreshOutdatedExpressions(expression);
	}
	
	public void refreshOutdatedExpressions(Expression expression) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		Criteria criteria = sessionFactory.getCurrentSession()
		.createCriteria(NormalizedDataElementValue.class, "ev")
		.createAlias("expression", "e")
		.add(Restrictions.ltProperty("ev.timestamp", "e.timestamp"))
		.add(Restrictions.eq("expression", expression))
//		.setLockMode(LockMode.READ)
		.setFlushMode(FlushMode.COMMIT)
		.setCacheable(false);
		
		for (NormalizedDataElementValue expressionValue : (List<NormalizedDataElementValue>)criteria.list()) {
			NormalizedDataElementValue newValue = expressionService.calculate(expressionValue.getExpression(), expressionValue.getOrganisationUnit(), expressionValue.getPeriod());
			expressionValue.setStatus(newValue.getStatus());
			expressionValue.setValue(newValue.getValue());
			expressionValue.setTimestamp(new Date());
			valueService.save(expressionValue);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void refreshNonCalculatedExpressionsInTransaction(Expression expression) {
		refreshNonCalculatedExpressions(expression);
	}
	
	public void refreshNonCalculatedExpressions(Expression expression) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		Long numValues = (Long)sessionFactory.getCurrentSession().createCriteria(NormalizedDataElementValue.class).add(Restrictions.eq("expression", expression)).setProjection(Projections.rowCount()).uniqueResult();
		Long numOrganisations = getNumberOfOrganisations();
		Long numPeriods = getNumberOfPeriods();
		
		if (numValues == numOrganisations * numPeriods) {
			if (log.isInfoEnabled()) log.info("no non calculated expressions, skipping");
		}
		else {
//			if (log.isDebugEnabled()) log.debug("retrieving expression values");
			
//			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExpressionValue.class)
//			.add(Restrictions.eq("expression", expression)).setCacheable(false);			
//			List<ExpressionValue> allValues = criteria.list();
			
//			if (log.isDebugEnabled()) log.debug("retrieved expression values, found: "+allValues.size());
			if (log.isDebugEnabled()) log.debug("retrieving all possible expressions");
			Query query2 = sessionFactory.getCurrentSession().createQuery(
					"select organisationUnit, period " +
					"from OrganisationUnit organisationUnit, Period period"
			).setCacheable(false);
			if (log.isDebugEnabled()) log.debug("starting sorting non calculated expressions");
			for (Iterator<Object[]> iterator = query2.iterate(); iterator.hasNext();) {
				Object[] row = (Object[]) iterator.next();
				NormalizedDataElementValue value = new NormalizedDataElementValue(null, null, (OrganisationUnit)row[0], expression, (Period)row[1]);
				if (valueService.getValue(value.getExpression(), value.getOrganisationUnit(), value.getPeriod()) == null) {
					NormalizedDataElementValue newValue = expressionService.calculate(value.getExpression(), value.getOrganisationUnit(), value.getPeriod());
					newValue.setTimestamp(new Date());
					valueService.save(newValue);
				}
			}
		}
	}
	
	private Long getNumberOfOrganisations() {
		return (Long)sessionFactory.getCurrentSession().createCriteria(OrganisationUnit.class).setProjection(Projections.rowCount()).uniqueResult();
	}
	
	private Long getNumberOfPeriods() {
		return (Long)sessionFactory.getCurrentSession().createCriteria(Period.class).setProjection(Projections.rowCount()).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void refreshOutdatedCalculationsInTransaction(Calculation calculation) {
		refreshOutdatedCalculations(calculation);
	}
	
	public void refreshOutdatedCalculations(Calculation calculation) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		Criteria criteria = sessionFactory.getCurrentSession()
		.createCriteria(CalculationValue.class, "cv")
		.createAlias("calculation", "c")
		.createAlias("calculation.expressions", "e").add(
			Restrictions.or(
				Restrictions.ltProperty("cv.timestamp", "c.timestamp"), 
				Restrictions.ltProperty("cv.timestamp", "e.timestamp")
			))
		.add(Restrictions.eq("calculation", calculation))
//		.setLockMode(LockMode.READ)
		.setFlushMode(FlushMode.COMMIT)
		.setCacheable(false)
		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		for (CalculationValue calculationValue : (List<CalculationValue>)criteria.list()) {
			CalculationValue newValue = expressionService.calculate(calculationValue.getCalculation(), calculationValue.getOrganisationUnit(), calculationValue.getPeriod());
			calculationValue.setValue(newValue.getValue());
			calculationValue.setHasMissingExpression(newValue.getHasMissingExpression());
			calculationValue.setHasMissingValues(newValue.getHasMissingValues());
			calculationValue.setTimestamp(new Date());
			valueService.save(calculationValue);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void refreshNonCalculatedCalculationsInTransaction(Calculation calculation) {
		refreshNonCalculatedCalculations(calculation);
	}
	
	public void refreshNonCalculatedCalculations(Calculation calculation) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		Long numValues = (Long)sessionFactory.getCurrentSession().createCriteria(CalculationValue.class).add(Restrictions.eq("calculation", calculation)).setProjection(Projections.rowCount()).uniqueResult();
		Long numOrganisations = getNumberOfOrganisations();
		Long numPeriods = getNumberOfPeriods();
			
		if (numValues == numOrganisations * numPeriods) {
			if (log.isInfoEnabled()) log.info("no non calculated calculations, skipping");
		}
		else {
//			if (log.isDebugEnabled()) log.debug("retrieving calculation values");
//			
//			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CalculationValue.class)
//			.add(Restrictions.eq("calculation", calculation)).setCacheable(false);			
//			List<CalculationValue> allValues = criteria.list();
//			
//			if (log.isDebugEnabled()) log.debug("retrieved calculation values, found: "+allValues.size());
			if (log.isDebugEnabled()) log.debug("retrieving all possible calculations");
			Query query2 = sessionFactory.getCurrentSession().createQuery(
					"select organisationUnit, period " +
					"from OrganisationUnit organisationUnit, Period period"
			).setCacheable(false);
			if (log.isDebugEnabled()) log.debug("starting sorting non calculated calculations");
			for (Iterator<Object[]> iterator = query2.iterate(); iterator.hasNext();) {
				Object[] row = (Object[]) iterator.next();
				CalculationValue value = new CalculationValue(calculation, (OrganisationUnit)row[0], (Period)row[1]);
				if (valueService.getValue(value.getCalculation(), value.getOrganisationUnit(), value.getPeriod()) == null) {
					CalculationValue newValue = expressionService.calculate(value.getCalculation(), value.getOrganisationUnit(), value.getPeriod());
					newValue.setTimestamp(new Date());
					valueService.save(newValue);
				}
			}
		}
	}
	
	@Transactional(readOnly = true)
	@CacheFlush({"dsrCache", "dashboardCache"})
	public void refreshExpressions() {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		List<Expression> expressions = sessionFactory.getCurrentSession().createCriteria(Expression.class).list();
		
		for (Expression expression : expressions) {
			getMe().refreshOutdatedExpressionsInTransaction(expression);
			getMe().refreshNonCalculatedExpressionsInTransaction(expression);
			sessionFactory.getCurrentSession().clear();
		}
	}
	
	@Transactional(readOnly = true)
	@CacheFlush({"dsrCache", "dashboardCache"})
	public void refreshCalculations() {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		List<Calculation> calculations = sessionFactory.getCurrentSession().createCriteria(Calculation.class).list();
		
		for (Calculation calculation : calculations) {
			calculation = (Calculation)sessionFactory.getCurrentSession().load(calculation.getClass(), calculation.getId());
				
			getMe().refreshOutdatedCalculationsInTransaction(calculation);
			getMe().refreshNonCalculatedCalculationsInTransaction(calculation);
			sessionFactory.getCurrentSession().clear();
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setGrailsApplication(GrailsApplication grailsApplication) {
		this.grailsApplication = grailsApplication;
	}
	
	// for internal call through transactional proxy
	public RefreshValueService getMe() {
		return grailsApplication.getMainContext().getBean(RefreshValueService.class);
	}
	
	
}
