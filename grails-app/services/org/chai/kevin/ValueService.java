package org.chai.kevin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.Value;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class ValueService {

	private static final Log log = LogFactory.getLog(ValueService.class);
	
	private SessionFactory sessionFactory;
	
	@Transactional(readOnly=true)
	public ExpressionValue getExpressionValue(OrganisationUnit organisationUnit, Expression expression, Period period) {
		return (ExpressionValue)sessionFactory.getCurrentSession().createCriteria(ExpressionValue.class)
			.add(Restrictions.naturalId()
				.set("period", period)
				.set("organisationUnit", organisationUnit)
				.set("expression", expression)
			)
			.setCacheable(true)
			.setFlushMode(FlushMode.MANUAL)
			.setCacheRegion("org.hibernate.cache.ExpressionValueQueryCache")
			.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public CalculationValue getCalculationValue(OrganisationUnit organisationUnit, Calculation calculation, Period period) {
		return (CalculationValue)sessionFactory.getCurrentSession().createCriteria(CalculationValue.class)
			.add(Restrictions.naturalId()
				.set("period", period)
				.set("organisationUnit", organisationUnit)
				.set("calculation", calculation)
			)
			.setCacheable(true)
			.setFlushMode(FlushMode.MANUAL)
			.setCacheRegion("org.hibernate.cache.CalculationValueQueryCache")
			.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public DataValue getDataValue(DataElement dataElement, Period period, Organisation organisation) {
		if (log.isDebugEnabled()) log.debug("getDataValue(dataElement="+dataElement+", period="+period+", organisation="+organisation+")");
		
		 Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DataValue.class)
         .add(Restrictions.naturalId()
        		 .set("dataElement", dataElement)
        		 .set("period", period)
        		 .set("organisationUnit", organisation.getOrganisationUnit())
         )
         .setCacheRegion("org.hibernate.cache.DataValueQueryCache")
         .setFlushMode(FlushMode.MANUAL)
         .setCacheable(true);

		 DataValue value = (DataValue)criteria.uniqueResult();
		 if (log.isDebugEnabled()) log.debug("getDataValue = "+value);
		 return value;
	}
	
	@Transactional(readOnly=true)
	public List<ExpressionValue> getOutdatedExpressions() {
		Criteria criteria = sessionFactory.getCurrentSession()
		.createCriteria(ExpressionValue.class, "ev")
		.createAlias("expression", "e").add(Restrictions.ltProperty("ev.timestamp", "e.timestamp"))
		.setCacheable(false);
		return criteria.list();
	}
	
	@Transactional(readOnly=true)
	public List<ExpressionValue> getNonCalculatedExpressions() {
		List<ExpressionValue> result = new ArrayList<ExpressionValue>();
		Query query = sessionFactory.getCurrentSession()
		.createQuery(
				"select expression, organisationUnit, period " +
				"from Expression expression, OrganisationUnit organisationUnit, Period period " +
				"where (expression, organisationUnit, period) not in (" +
				"	select ev.expression, ev.organisationUnit, ev.period from ExpressionValue as ev" +
				")"
		);
		for (Iterator iterator = query.iterate(); iterator.hasNext();) {
			Object[] row = (Object[]) iterator.next();
			result.add(new ExpressionValue(null, null, (OrganisationUnit)row[1], (Expression)row[0], (Period)row[2]));
		}
		return result;
	}
	
	@Transactional(readOnly=true)
	public List<CalculationValue> getOutdatedCalculations() {
		Criteria criteria = sessionFactory.getCurrentSession()
		.createCriteria(CalculationValue.class, "cv")
		.createAlias("calculation", "c")
		.createAlias("calculation.expressions", "e").add(
			Restrictions.or(
				Restrictions.ltProperty("cv.timestamp", "c.timestamp"), 
				Restrictions.ltProperty("cv.timestamp", "e.timestamp")
			))
		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}
	
	@Transactional(readOnly=true)
	public List<CalculationValue> getNonCalculatedCalculations() {
		List<CalculationValue> result = new ArrayList<CalculationValue>();
		Query query = sessionFactory.getCurrentSession()
		.createQuery(
				"select calculation, organisationUnit, period " +
				"from Calculation calculation, OrganisationUnit organisationUnit, Period period " +
				"where (calculation, organisationUnit, period) not in (" +
				"	select cv.calculation, cv.organisationUnit, cv.period from CalculationValue as cv" +
				")"
		);
		for (Iterator iterator = query.iterate(); iterator.hasNext();) {
			Object[] row = (Object[]) iterator.next();
			result.add(new CalculationValue((Calculation)row[0], (OrganisationUnit)row[1], (Period)row[2], new HashMap<Organisation, ExpressionValue>()));
		}
		return result;
	}
	
	@Transactional(readOnly=false)
	public <T extends Value> T save(T value) {
		value.setTimestamp(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(value);
		return value;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
