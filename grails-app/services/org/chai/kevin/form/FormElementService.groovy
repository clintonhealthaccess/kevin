package org.chai.kevin.form

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.hibernate.FlushMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

class FormElementService {

	static transactional = true
	
	def languageService
	def sessionFactory
	
	List<FormElement> searchFormElements(String text, List<String> allowedTypes, Map<String, String> params) {
		def criteria = getFormElementSearch(text, allowedTypes)
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		else criteria.setMaxResults(500)
		
		List<FormElement> data = criteria.addOrder(Order.asc("id")).list()
		
		StringUtils.split(text).each { chunk ->
			data.retainAll { element ->
				// we look in "info" if it is a data element
				Utils.matches(chunk, element.dataElement.id+"") ||
				Utils.matches(chunk, element.dataElement.info) ||
				Utils.matches(chunk, element.dataElement.names[languageService.getCurrentLanguage()]) ||
				Utils.matches(chunk, element.dataElement.code) ||
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
	
	private def getFormElementSearch(String text, List<String> allowedTypes) {
		def criteria = sessionFactory.currentSession.createCriteria(FormElement.class)
		criteria.createAlias("dataElement", "de")
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			
			// data element
			disjunction.add(Restrictions.ilike("de.info", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("de.code", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("de.names.jsonText", chunk, MatchMode.ANYWHERE))
			if (NumberUtils.isNumber(chunk)) disjunction.add(Restrictions.eq("de.id", Long.parseLong(chunk)))
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

		return criteria
	}
	
	FormElement getFormElement(Long id, Class<?> clazz = FormElement.class) {
		return sessionFactory.currentSession.get(clazz, id)
	}
	
	void save(FormEnteredValue formEnteredValue) {
		if (log.isDebugEnabled()) log.debug("save(formEnteredValue=${formEnteredValue}})")
		formEnteredValue.setUserUuid(SecurityUtils.subject.principal)
		formEnteredValue.setTimestamp(new Date());
		formEnteredValue.save();
	}
	
	void delete(FormEnteredValue formEnteredValue) {
		formEnteredValue.delete()
	}
	
	FormEnteredValue getFormEnteredValue(FormElement formElement, DataLocation dataLocation) {
		def c = FormEnteredValue.createCriteria()
		c.add(Restrictions.naturalId()
			.set("dataLocation", dataLocation)
			.set("formElement", formElement)
		)
		c.setCacheable(true)
		c.setCacheRegion("formEnteredValueQueryCache")
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getFormEnteredValue(...)="+result);
		return result
	}
	
	FormEnteredValue getOrCreateFormEnteredValue(DataLocation dataLocation, FormElement element) {
		FormEnteredValue enteredValue = getFormEnteredValue(element, dataLocation);
		if (enteredValue == null) {
			// TODO get raw data element
			enteredValue = new FormEnteredValue(element, dataLocation, Value.NULL_INSTANCE(), null);
		}
		return enteredValue;
	}
	
	Set<FormValidationRule> searchValidationRules(FormElement formElement, DataLocationType type) {
		if (log.isDebugEnabled()) log.debug("searchValidationRules(formElement="+formElement+", type="+type+")");
		
		def c = FormValidationRule.createCriteria()
		c.add(Restrictions.like("expression", "\$${formElement.id}", MatchMode.ANYWHERE))
		c.add(Restrictions.like("typeCodeString", type.code, MatchMode.ANYWHERE))
		
		List<FormValidationRule> rules = c.setFlushMode(FlushMode.COMMIT).list()
		return filter(rules, formElement.id);
	}
	
	Set<FormSkipRule> searchSkipRules(FormElement formElement) {
		if (log.isDebugEnabled()) log.debug("searchSkipRules(formElement="+formElement+")");
		
		def c = FormSkipRule.createCriteria()
		c.add(Restrictions.like("expression", "\$${formElement.id}", MatchMode.ANYWHERE))
		
		List<FormSkipRule> rules = c.setFlushMode(FlushMode.COMMIT).list()
		return filter(rules, formElement.id);
	}
	
	static def filter(def rules, Long id) {
		return rules.findAll { rule ->
			return Utils.containsId(rule.expression, id)
		}
	}
	
}
