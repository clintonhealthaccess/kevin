/**
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
package org.chai.kevin.survey

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.LanguageService;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.PrefixPredicate;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyService {

	static transactional = true
	
	def languageService
	def dataService
	def organisationService
	def sessionFactory

	SurveyElement getSurveyElement(Long id) {
		return SurveyElement.get(id)
	}

	SurveyQuestion getSurveyQuestion(Long id) {
		// TODO test this with Grails 2.0
		return sessionFactory.currentSession.get(SurveyQuestion.class, id)
	}
	
	List<SurveyQuestion> searchSurveyQuestions(String text, Survey survey, def params = [:]) {
		def criteria = getQuestionSearchCriteria(text, survey)
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		else criteria.setMaxResults(500)
		
		List<SurveyQuestion> questions = criteria.addOrder(Order.asc("id")).list()
		
		StringUtils.split(text).each { chunk ->
			questions.retainAll { question ->
				Utils.matches(chunk, question.names[languageService.getCurrentLanguage()]) ||
				Utils.matches(chunk, question.descriptions[languageService.getCurrentLanguage()])
			}
		}
		
		return questions;
	}
	
	Integer countSurveyQuestions(String text, Survey survey) {
		return getQuestionSearchCriteria(text, survey).setProjection(Projections.count("id")).uniqueResult()
	}

	private def getQuestionSearchCriteria(String text, Survey survey) {
		def criteria = sessionFactory.currentSession.createCriteria(SurveyQuestion.class)
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			
			disjunction.add(Restrictions.ilike("names.jsonText", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("descriptions.jsonText", chunk, MatchMode.ANYWHERE))

			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		
		if (survey != null) {
			criteria.createAlias("section", "ss")
			.createAlias("ss.objective", "so")
			.add(Restrictions.eq("so.survey", survey))
		}
		
		return criteria
	}
	
	Set<SurveyValidationRule> searchValidationRules(SurveyElement surveyElement, OrganisationUnitGroup group) {
		if (log.isDebugEnabled()) log.debug("searchValidationRules(surveyElement="+surveyElement+", group="+group+")");
		
		def c = SurveyValidationRule.createCriteria()
		c.add(Restrictions.like("expression", "\$${surveyElement.id}", MatchMode.ANYWHERE))
		c.add(Restrictions.like("groupUuidString", group.uuid, MatchMode.ANYWHERE))
		
		List<SurveyValidationRule> rules = c.setFlushMode(FlushMode.COMMIT).list()
		return filter(rules, surveyElement.id);
	}
	
	Set<SurveySkipRule> searchSkipRules(SurveyElement surveyElement) {
		if (log.isDebugEnabled()) log.debug("searchSkipRules(surveyElement="+surveyElement+")");
		
		def c = SurveySkipRule.createCriteria()
		c.add(Restrictions.like("expression", "\$${surveyElement.id}", MatchMode.ANYWHERE))
		
		List<SurveySkipRule> rules = c.setFlushMode(FlushMode.COMMIT).list()
		return filter(rules, surveyElement.id);
	}
	
	
	static def filter(def rules, Long id) {
		return rules.findAll { rule ->
			return rule.expression.matches(".*\\\$"+id+"(\\z|\\D|\$).*")
		}
	}
	
//	Set<SurveyElement> getSurveyElements(SurveyQuestion question, OrganisationUnitGroup group) {
//		def c = SurveyElement.createCriteria()
//		c.add(Restrictions.eq("surveyQuestion", question))
//		c.add(Restrictions.like("groupUuidString", group.uuid, MatchMode.ANYWHERE))
//
//		return c.setFlushMode(FlushMode.COMMIT).list()
//	}
	
	Set<SurveyElement> getSurveyElements(RawDataElement dataElement, Survey survey) {
		def c = SurveyElement.createCriteria()
		if (survey != null) {
			c.createAlias("surveyQuestion", "sq")
			.createAlias("sq.section", "ss")
			.createAlias("ss.objective", "so")
			.add(Restrictions.eq("so.survey", survey))
		}
		if (dataElement != null) {
			c.add(Restrictions.eq("dataElement", dataElement))
		}
		
		return c.setFlushMode(FlushMode.COMMIT).list()
	}

	List<SurveyElement> searchSurveyElements(String text, Survey survey, List<String> allowedTypes, Map<String, String> params) {
		def criteria = getSurveyElementSearchCriteria(text, survey, allowedTypes)
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		else criteria.setMaxResults(500)
		
		List<SurveyElement> data = criteria.addOrder(Order.asc("id")).list()
		
		StringUtils.split(text).each { chunk ->
			data.retainAll { element ->
				// we look in "info" if it is a data element
				Utils.matches(chunk, element.dataElement.id+"") ||
				Utils.matches(chunk, element.dataElement.info) ||
				Utils.matches(chunk, element.dataElement.names[languageService.getCurrentLanguage()]) ||
				Utils.matches(chunk, element.dataElement.code) ||
				Utils.matches(chunk, element.surveyQuestion.names[languageService.getCurrentLanguage()]) ||
				Utils.matches(chunk, element.id+"")
			}
		}
		
		if (!allowedTypes.isEmpty()) {
			data.retainAll { element ->
				element.dataElement.type.type.name().toLowerCase() in allowedTypes
			}
		}
		
		return data
	}
	
	private def getSurveyElementSearchCriteria(String text, Survey survey, List<String> allowedTypes) {
		def criteria = sessionFactory.currentSession.createCriteria(SurveyElement.class)
		criteria.createAlias("dataElement", "de")
		criteria.createAlias("surveyQuestion", "sq")
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			
			// data element
			disjunction.add(Restrictions.ilike("de.info", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("de.code", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("de.names.jsonText", chunk, MatchMode.ANYWHERE))
			if (NumberUtils.isNumber(chunk)) disjunction.add(Restrictions.eq("de.id", Long.parseLong(chunk)))
			// question
			disjunction.add(Restrictions.ilike("sq.names.jsonText", chunk, MatchMode.ANYWHERE))
			// survey element
			if (NumberUtils.isNumber(chunk)) disjunction.add(Restrictions.eq("id", Long.parseLong(chunk)))
			
			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		
		if (!allowedTypes.isEmpty()) {
			def typeRestrictions = Restrictions.disjunction()
			allowedTypes.each { type ->
				typeRestrictions.add(Restrictions.like("de.type.jsonValue", type, MatchMode.ANYWHERE))
			}
			criteria.add(typeRestrictions)
		}

		if (survey != null) {
			criteria.createAlias("sq.section", "ss")
			.createAlias("ss.objective", "so")
			.add(Restrictions.eq("so.survey", survey))
		}
		
		return criteria
	}
	
	Integer getNumberOfOrganisationUnitApplicable(SurveyElement surveyElement) {
		Set<String> groupUuids = surveyElement.getOrganisationUnitGroupApplicable();
		int number = 0;
		for (String groupUuid : groupUuids) {
			OrganisationUnitGroup group = organisationService.getOrganisationUnitGroup(groupUuid);
			if (group != null) number += organisationService.getNumberOfOrganisationOfGroup(group)
		}
		return number;
	}
	
	// TODO move this somewhere else and change signature to getHeaderPrefixes(Type type)
	List<String> getHeaderPrefixes(SurveyElement element) {
		
		List<String> prefixes = new ArrayList(element.getDataElement().getType().getPrefixes(
			element.getDataElement().getType().getPlaceHolderValue(),
			new PrefixPredicate() {
				@Override
				public boolean holds(Type type, Value value, String prefix) {
					if (getParent() != null && getParent().getType() == ValueType.MAP) return true;
				}
			}).keySet());
		
		return prefixes.collect { prefix ->
			prefix.replace("[0]", "[_]")
		}
		
	}
	
}