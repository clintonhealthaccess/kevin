package org.chai.kevin.data;

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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.util.Utils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

class DataService {

	static transactional = true
	
	def languageService;
	def valueService;
	def sessionFactory;
	
	public Enum getEnum(Long id) {
		return (Enum)sessionFactory.getCurrentSession().get(Enum.class, id);
	}
	
	public Enum findEnumByCode(String code) {
		return Enum.findByCode(code, [cache: true])
	}
	
	public <T extends Data<?>> T getData(Long id, Class<T> clazz) {
		if (id == null) return null;
		return (Data)sessionFactory.getCurrentSession().get(clazz, id);
	}
	
	public <T extends Data<?>> T getDataByCode(String code, Class<T> clazz) {		
		return (T) sessionFactory.getCurrentSession().createCriteria(clazz)
		.add(Restrictions.eq("code", code)).uniqueResult();
	}
	
	public <T extends Data<?>> T save(T data) {
		// we bypass validation in case there's something
		// it should be saved anyway
		data.save(validate: false, flush: true)
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException if the data element has values associated to it
	 * @param element
	 */
	public void delete(Data data) {
		if (!getReferencingData(data).isEmpty()) throw new IllegalArgumentException("other data are still referencing the element being deleted")
		if (valueService.getNumberOfValues(data) != 0) throw new IllegalArgumentException("there are still values associated to the element being deleted");
		else data.delete();
	}
	
	public Set<Data<?>> getReferencingData(Data data) {
		def result = []
		result.addAll(getReferencingNormalizedDataElements(data))
		result.addAll(getReferencingCalculations(data))
		return result
	}
	
	public List<NormalizedDataElement> getReferencingNormalizedDataElements(Data data) {
		def criteria = sessionFactory.currentSession.createCriteria(NormalizedDataElement.class);
		def list = criteria.add(Restrictions.like("expressionMapString", "\$"+data.id, MatchMode.ANYWHERE)).list()
		return list.findAll { result ->
			return !result.expressions.findAll { expression ->
				return Utils.containsId(expression, data.id)
			}.isEmpty()
		}
	}
	
	public List<Calculation> getReferencingCalculations(Data data) {
		def criteria = sessionFactory.currentSession.createCriteria(Calculation.class);
		def list = criteria.add(Restrictions.like("expression", "\$"+data.id, MatchMode.ANYWHERE)).list()
		return list.findAll { result ->
			return Utils.containsId(result.expression, data.id)
		}
	}
	
	public Integer countData(Class<Data> clazz, String text, List<String> allowedTypes) {
		return getSearchCriteria(clazz, text, allowedTypes).setProjection(Projections.count("id")).uniqueResult()
	}
	
    public <T extends Data> List<T> searchData(Class<T> clazz, String text, List<String> allowedTypes, Map<String, String> params) {
		def criteria = getSearchCriteria(clazz, text, allowedTypes)
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		else criteria.setMaxResults(500)
		
		def data = criteria.addOrder(Order.asc("id")).list()
		
		if (!allowedTypes.isEmpty()) {
			data.retainAll { element ->
				element.getType().type.name().toLowerCase() in allowedTypes 
			}
		}
		
		return data
    }
	
	private def getSearchCriteria(Class<Data> clazz, String text, List<String> allowedTypes) {
		def criteria = sessionFactory.currentSession.createCriteria(clazz)
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			
			// we look in "info" if it is a data element
			if (clazz.equals(RawDataElement.class)) disjunction.add(Restrictions.ilike("info", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("code", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("names_"+languageService.currentLanguage, chunk, MatchMode.ANYWHERE))
			if (NumberUtils.isNumber(chunk)) disjunction.add(Restrictions.eq("id", Long.parseLong(chunk)))
			
			textRestrictions.add(disjunction)
		}
		
		if (!allowedTypes.isEmpty()) {
			def typeRestrictions = Restrictions.disjunction()
			allowedTypes.each { type ->
				typeRestrictions.add(Restrictions.ilike("typeString", type, MatchMode.ANYWHERE))
			}
			criteria.add(Restrictions.and(textRestrictions, typeRestrictions))
		}
		else {
			criteria.add(textRestrictions)
		}
		
		return criteria
	}
	    
}
