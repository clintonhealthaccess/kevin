package org.chai.kevin.value;

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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

public class ValueService {

	private static final Log log = LogFactory.getLog(ValueService.class);
	
	private DataService dataService;
	private SessionFactory sessionFactory;
	
	@Transactional(readOnly=false)
	public <T extends StoredValue> T save(T value) {
		log.debug("save(value="+value+")");
		
		// set timestamp of all referencing data
		// if saving value of RawDataElement
		// TODO replace by a messaging system i.e. RabbitMQ
		if (value instanceof RawDataElementValue) {
			for (NormalizedDataElement normalizedDataElement : dataService.getReferencingNormalizedDataElements(value.getData())) {
				normalizedDataElement.setTimestamp(new Date());
				dataService.save(normalizedDataElement);
			}
			for (Calculation<?> calculation : dataService.getReferencingCalculations(value.getData())) {
				calculation.setTimestamp(new Date());
				dataService.save(calculation);
			}
		}
		
		value.setTimestamp(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(value);
		return value;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public <T extends DataValue> T getDataElementValue(DataElement<T> data, DataLocationEntity entity, Period period) {
		if (log.isDebugEnabled()) log.debug("getDataElementValue(data="+data+", period="+period+", entity="+entity+")");
		T result = (T)sessionFactory.getCurrentSession().createCriteria(data.getValueClass())
		.add(Restrictions.eq("period", period))
		.add(Restrictions.eq("entity", entity))
		.add(Restrictions.eq("data", data)).uniqueResult();
		if (log.isDebugEnabled()) log.debug("getDataElementValue(...)="+result);
		return result;
	}
	
	@Transactional(readOnly=true)
	public <T extends CalculationPartialValue> CalculationValue<T> getCalculationValue(Calculation<T> calculation, CalculationEntity entity, Period period, Set<DataEntityType> types) {
		if (log.isDebugEnabled()) log.debug("getCalculationValue(calculation="+calculation+", period="+period+", entity="+entity+", types="+types+")");
		CalculationValue<T> result = calculation.getCalculationValue(getPartialValues(calculation, entity, period, types), period, entity);
		if (log.isDebugEnabled()) log.debug("getCalculationValue(...)="+result);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public <T extends CalculationPartialValue> List<T> getPartialValues(Calculation<T> calculation, CalculationEntity entity, Period period) {
		return (List<T>)sessionFactory.getCurrentSession().createCriteria(calculation.getValueClass())
		.add(Restrictions.eq("period", period))
		.add(Restrictions.eq("entity", entity))
		.add(Restrictions.eq("data", calculation)).list();
	}
	
	@SuppressWarnings("unchecked")
	private <T extends CalculationPartialValue> List<T> getPartialValues(Calculation<T> calculation, CalculationEntity entity, Period period, Set<DataEntityType> types) {
		return (List<T>)sessionFactory.getCurrentSession().createCriteria(calculation.getValueClass())
		.add(Restrictions.eq("period", period))
		.add(Restrictions.eq("entity", entity))
		.add(Restrictions.eq("data", calculation))
		.add(Restrictions.in("type", types)).list();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOfValues(Data<?> data, Period period) {
		return (Long)sessionFactory.getCurrentSession().createCriteria(data.getValueClass())
		.add(Restrictions.eq("data", data))
		.add(Restrictions.eq("period", period))
		.setProjection(Projections.count("id"))
		.uniqueResult();
	}
	
	// if this is set readonly, it triggers an error when deleting a
	// data element through DataElementController.deleteEntity
	@Transactional(readOnly=true)
	public Long getNumberOfValues(Data<?> data) {
		return (Long)sessionFactory.getCurrentSession().createCriteria(data.getValueClass())
		.add(Restrictions.eq("data", data))
		.setProjection(Projections.count("id"))
		.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public Long getNumberOfValues(Data<?> data, Status status, Period period) {
		// TODO allow Calculation here
		if (!(data instanceof NormalizedDataElement)) {
			throw new IllegalArgumentException("wrong data type");
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(data.getValueClass())
			.add(Restrictions.eq("data", data))
			.add(Restrictions.eq("status", status));
		if (period != null) criteria.add(Restrictions.eq("period", period));
		return (Long)criteria	
			.setProjection(Projections.count("id"))
			.uniqueResult();
	}
	
	@Transactional(readOnly=true)
	@SuppressWarnings("unchecked")
	public <T extends DataValue> List<T> getValues(Data<T> data, Period period) {
		return (List<T>)sessionFactory.getCurrentSession().createCriteria(data.getValueClass())
		.add(Restrictions.eq("data", data))
		.add(Restrictions.eq("period", period))
		.list();
	}
	
	@Transactional(readOnly=false)
	public void deleteValues(Data<?> data, CalculationEntity entity, Period period) {
		String queryString = "delete from "+data.getValueClass().getAnnotation(Entity.class).name()+" where data = :data";
		if (entity != null) queryString += " and entity = :entity";
		if (period != null) queryString += " and period = :period";
		Query query = sessionFactory.getCurrentSession()
		.createQuery(queryString)
		.setParameter("data", data);
		if (entity != null) query.setParameter("entity", entity);
		if (period != null) query.setParameter("period", period);
		query.executeUpdate();
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

}
