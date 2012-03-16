package org.chai.kevin.form

import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.hibernate.FlushMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

class FormElementService {

	static transactional = true
	
	void save(FormEnteredValue formEnteredValue) {
		if (log.isDebugEnabled()) log.debug("save(formEnteredValue=${formEnteredValue}})")
		formEnteredValue.setUserUuid(SecurityUtils.subject.principal)
		formEnteredValue.setTimestamp(new Date());
		formEnteredValue.save();
	}
	
	void delete(FormEnteredValue formEnteredValue) {
		formEnteredValue.delete()
	}
	
	FormEnteredValue getFormEnteredValue(FormElement formElement, DataLocationEntity entity) {
		def c = FormEnteredValue.createCriteria()
		c.add(Restrictions.naturalId()
			.set("entity", entity)
			.set("formElement", formElement)
		)
		c.setCacheable(true)
		c.setCacheRegion("formEnteredValueQueryCache")
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getFormEnteredValue(...)="+result);
		return result
	}
	
	FormEnteredValue getOrCreateFormEnteredValue(DataLocationEntity entity, FormElement element) {
		FormEnteredValue enteredValue = getFormEnteredValue(element, entity);
		if (enteredValue == null) {
			enteredValue = new FormEnteredValue(element, entity, Value.NULL_INSTANCE(), null);
			save(enteredValue);
		}
		return enteredValue;
	}
	
	Set<FormValidationRule> searchValidationRules(FormElement formElement, DataEntityType type) {
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
