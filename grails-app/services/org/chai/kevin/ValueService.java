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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Sum;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.StoredValue;
import org.chai.kevin.value.ValueCalculator;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class ValueService {

	private static final Log log = LogFactory.getLog(ValueService.class);
	
	private SessionFactory sessionFactory;
	
	private Map<Class<?>, ValueCalculator<?>> calculatorMap = new HashMap<Class<?>, ValueCalculator<?>>();
	
	public ValueService() {
		calculatorMap.put(Expression.class, new ExpressionValueCalculator());
		calculatorMap.put(DataElement.class, new DataValueCalculator());
		calculatorMap.put(Sum.class, new CalculationValueCalculator());
		calculatorMap.put(Average.class, new CalculationValueCalculator());
	}
	
	@Transactional(readOnly=true)
	public <T extends StoredValue> T getValue(Data<T> data, OrganisationUnit organisationUnit, Period period) {
		// TODO make a registry class with this code
		Class<?> clazz = data.getClass();
		while (!calculatorMap.containsKey(clazz)) {
			clazz = clazz.getSuperclass();
		}
		ValueCalculator<T> calculator = (ValueCalculator<T>)calculatorMap.get(clazz);
		return data.getValue(calculator, organisationUnit, period);
	}
	
	@Transactional(readOnly=false)
	public void deleteValues(Expression expression) {
		sessionFactory.getCurrentSession()
		.createQuery("delete from ExpressionValue where expression = :expression")
		.setParameter("expression", expression).executeUpdate();
	}
	
	@Transactional(readOnly=false)
	public void deleteValues(Calculation calculation) {
		sessionFactory.getCurrentSession()
		.createQuery("delete from CalculationValue where calculation = :calculation")
		.setParameter("calculation", calculation).executeUpdate();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOfValues(DataElement dataElement) {
		return (Long)sessionFactory.getCurrentSession().createCriteria(DataValue.class)
		.add(Restrictions.eq("dataElement", dataElement))
		.setProjection(Projections.count("id"))
		.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOfValues(Expression expression) {
		return (Long)sessionFactory.getCurrentSession().createCriteria(ExpressionValue.class)
		.add(Restrictions.eq("expression", expression))
		.setProjection(Projections.count("id"))
		.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOfValues(Calculation calculation) {
		return (Long)sessionFactory.getCurrentSession().createCriteria(CalculationValue.class)
		.add(Restrictions.eq("calculation", calculation))
		.setProjection(Projections.count("id"))
		.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOfValues(DataElement dataElement, Period period) {
		return (Long)sessionFactory.getCurrentSession().createCriteria(DataValue.class)
		.add(Restrictions.eq("dataElement", dataElement))
		.add(Restrictions.eq("period", period))
		.setProjection(Projections.count("id"))
		.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	@SuppressWarnings("unchecked")
	public List<DataValue> getValues(DataElement dataElement, Period period) {
		return (List<DataValue>)sessionFactory.getCurrentSession().createCriteria(DataValue.class)
		.add(Restrictions.eq("dataElement", dataElement))
		.add(Restrictions.eq("period", period))
		.list();
	}
	
	private class ExpressionValueCalculator implements ValueCalculator<ExpressionValue> {
		
		@Transactional(readOnly=true)
		public ExpressionValue getValue(Data<ExpressionValue> expression, OrganisationUnit organisationUnit, Period period) {
			return (ExpressionValue)sessionFactory.getCurrentSession().createCriteria(ExpressionValue.class)
				.add(Restrictions.naturalId()
					.set("period", period)
					.set("organisationUnit", organisationUnit)
					.set("expression", expression)
				)
				.uniqueResult();
		}

	}
		
	private class CalculationValueCalculator implements ValueCalculator<CalculationValue> {
	
		@Transactional(readOnly=true)
		public CalculationValue getValue(Data<CalculationValue> calculation, OrganisationUnit organisationUnit, Period period) {
			return (CalculationValue)sessionFactory.getCurrentSession().createCriteria(CalculationValue.class)
				.add(Restrictions.naturalId()
					.set("period", period)
					.set("organisationUnit", organisationUnit)
					.set("calculation", calculation)
				)
				.uniqueResult();
		}
		
	}
	
	private class DataValueCalculator implements ValueCalculator<DataValue> {
		
		@Transactional(readOnly=true)
		public DataValue getValue(Data<DataValue> dataElement, OrganisationUnit organisation, Period period) {
			if (log.isDebugEnabled()) log.debug("getDataValue(dataElement="+dataElement+", period="+period+", organisation="+organisation+")");
			
			 Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DataValue.class)
	         .add(Restrictions.naturalId()
	        		 .set("dataElement", dataElement)
	        		 .set("period", period)
	        		 .set("organisationUnit", organisation)
	         );
	
			 DataValue value = (DataValue)criteria.uniqueResult();
			 if (log.isDebugEnabled()) log.debug("getDataValue = "+value);
			 return value;
		}
	}

	@Transactional(readOnly=false)
	public <T extends StoredValue> T save(T value) {
		log.debug("save(value="+value+")");
//		value.setTimestamp(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(value);
		return value;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
