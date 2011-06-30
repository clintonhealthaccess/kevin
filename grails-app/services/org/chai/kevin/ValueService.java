package org.chai.kevin;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueCalculator;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class ValueService {

	private static final Log log = LogFactory.getLog(ValueService.class);
	
	private SessionFactory sessionFactory;
	
	public <T extends Value> T getValue(Data<T> data, OrganisationUnit organisationUnit, Period period) {
		return data.getValue(new CacheValueCalculator(), organisationUnit, period);
	}
	
	private class CacheValueCalculator implements ValueCalculator {
		
		@Transactional(readOnly=true)
		public ExpressionValue getValue(Expression expression, OrganisationUnit organisationUnit, Period period) {
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
		public CalculationValue getValue(Calculation calculation, OrganisationUnit organisationUnit, Period period) {
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
		public DataValue getValue(DataElement dataElement, OrganisationUnit organisation, Period period) {
			if (log.isDebugEnabled()) log.debug("getDataValue(dataElement="+dataElement+", period="+period+", organisation="+organisation+")");
			
			 Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DataValue.class)
	         .add(Restrictions.naturalId()
	        		 .set("dataElement", dataElement)
	        		 .set("period", period)
	        		 .set("organisationUnit", organisation)
	         )
	         .setCacheRegion("org.hibernate.cache.DataValueQueryCache")
	         .setFlushMode(FlushMode.MANUAL)
	         .setCacheable(true);
	
			 DataValue value = (DataValue)criteria.uniqueResult();
			 if (log.isDebugEnabled()) log.debug("getDataValue = "+value);
			 return value;
		}
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
		
		Integer numValues = (Integer)sessionFactory.getCurrentSession().createCriteria(ExpressionValue.class).setProjection(Projections.rowCount()).uniqueResult();
		Integer numExpressions = (Integer)sessionFactory.getCurrentSession().createCriteria(Expression.class).setProjection(Projections.rowCount()).uniqueResult();
		Integer numOrganisations = getNumberOfOrganisations();
		Integer numPeriods = getNumberOfPeriods();
		
		if (numValues == numOrganisations * numPeriods * numExpressions) {
			log.info("no non calculated expressions, skipping");
		}
		else {
			if (log.isDebugEnabled()) log.debug("retrieving expression values");
			Set<ExpressionValue> allValues = new HashSet<ExpressionValue>();
			Query query1 = sessionFactory.getCurrentSession().createQuery(
					"select ev.expression, ev.organisationUnit, ev.period from ExpressionValue as ev"
			).setCacheable(false);
			for (Iterator iterator = query1.iterate(); iterator.hasNext();) {
				Object[] row = (Object[]) iterator.next();
				ExpressionValue value = new ExpressionValue(null, null, (OrganisationUnit)row[1], (Expression)row[0], (Period)row[2]);
				allValues.add(value);
			}
			if (log.isDebugEnabled()) log.debug("retrieved expression values, found: "+allValues.size());
			if (log.isDebugEnabled()) log.debug("retrieving all possible expressions");
			Query query2 = sessionFactory.getCurrentSession().createQuery(
					"select expression, organisationUnit, period " +
					"from Expression expression, OrganisationUnit organisationUnit, Period period"
			).setCacheable(false);
			if (log.isDebugEnabled()) log.debug("starting sorting non calculated expressions");
			for (Iterator iterator = query2.iterate(); iterator.hasNext();) {
				Object[] row = (Object[]) iterator.next();
				ExpressionValue newValue = new ExpressionValue(null, null, (OrganisationUnit)row[1], (Expression)row[0], (Period)row[2]);
				if (!allValues.contains(newValue)) result.add(newValue);
			}
			if (log.isDebugEnabled()) log.debug("done sorting non calculated expressions, found: "+result.size());
		}
		return result;
	}
	
	private Integer getNumberOfOrganisations() {
		return (Integer)sessionFactory.getCurrentSession().createCriteria(OrganisationUnit.class).setProjection(Projections.rowCount()).uniqueResult();
	}
	
	private Integer getNumberOfPeriods() {
		return (Integer)sessionFactory.getCurrentSession().createCriteria(Period.class).setProjection(Projections.rowCount()).uniqueResult();
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
		
		Integer numValues = (Integer)sessionFactory.getCurrentSession().createCriteria(CalculationValue.class).setProjection(Projections.rowCount()).uniqueResult();
		Integer numCalculations = (Integer)sessionFactory.getCurrentSession().createCriteria(Calculation.class).setProjection(Projections.rowCount()).uniqueResult();
		Integer numOrganisations = getNumberOfOrganisations();
		Integer numPeriods = getNumberOfPeriods();
		
		if (numValues == numOrganisations * numPeriods * numCalculations) {
			log.info("no non calculated calculations, skipping");
		}
		else {
			if (log.isDebugEnabled()) log.debug("retrieving calculation values");
			Set<CalculationValue> allValues = new HashSet<CalculationValue>();
			Query query1 = sessionFactory.getCurrentSession().createQuery(
					"select cv.calculation, cv.organisationUnit, cv.period from CalculationValue as cv"
			).setCacheable(false);
			for (Iterator iterator = query1.iterate(); iterator.hasNext();) {
				Object[] row = (Object[]) iterator.next();
				CalculationValue value = new CalculationValue((Calculation)row[0], (OrganisationUnit)row[1], (Period)row[2], new HashMap<Organisation, ExpressionValue>());
				allValues.add(value);
			}
			if (log.isDebugEnabled()) log.debug("retrieved calculation values, found: "+allValues.size());
			if (log.isDebugEnabled()) log.debug("retrieving all possible calculations");
			Query query2 = sessionFactory.getCurrentSession().createQuery(
					"select calculation, organisationUnit, period " +
					"from Calculation calculation, OrganisationUnit organisationUnit, Period period"
			).setCacheable(false);
			if (log.isDebugEnabled()) log.debug("starting sorting non calculated calculations");
			for (Iterator iterator = query2.iterate(); iterator.hasNext();) {
				Object[] row = (Object[]) iterator.next();
				CalculationValue newValue = new CalculationValue((Calculation)row[0], (OrganisationUnit)row[1], (Period)row[2], new HashMap<Organisation, ExpressionValue>());
				if (!allValues.contains(newValue)) result.add(newValue);
			}
			if (log.isDebugEnabled()) log.debug("done sorting non calculated calculations, found: "+result.size());
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
