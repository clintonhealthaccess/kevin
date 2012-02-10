package org.chai.kevin.value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.PrefixPredicate;
import org.chai.kevin.data.Type.ValuePredicate;
import org.chai.kevin.survey.SurveySkipRule;
import org.chai.kevin.survey.SurveyValidationRule;
import org.chai.kevin.util.Utils;

public class ValidatableValue {

	private static final Log log = LogFactory.getLog(ValidatableValue.class);
	
	private final Type type;
	private final Value value;
	
	public ValidatableValue(Value value, Type type) {
		this.type = type;
		this.value = value;
	}

	public Type getType() {
		return type;
	}
	
	public Value getValue() {
		return value;
	}
	
	public void setInvalid(SurveyValidationRule validationRule, Set<String> prefixes) {
		setAttribute("invalid", validationRule.getId().toString(), prefixes);
	}

	public Set<String> getErrors(String prefix) {
		try {
			return Utils.split(type.getAttribute(value, prefix, "invalid"));
		}
		catch (IndexOutOfBoundsException e) {
			return new HashSet<String>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getUnacceptedErrors(String prefix) {
		try {
			Set<String> invalidRules = Utils.split(type.getAttribute(value, prefix, "invalid"));
			Set<String> acceptedRules = Utils.split(type.getAttribute(value, prefix, "warning"));
			
			return new HashSet<String>(CollectionUtils.subtract(invalidRules, acceptedRules));
		} 
		catch (IndexOutOfBoundsException e) {
			return new HashSet<String>();
		}
	}
	
	public boolean isValid(String prefix) {
		return getUnacceptedErrors(prefix).isEmpty();
	}
	
	public void setSkipped(SurveySkipRule skipRule, Set<String> prefixes) {
		setAttribute("skipped", skipRule.getId().toString(), prefixes);
	}
	
	public Set<String> getSkipped(String prefix) {
		try {
			return Utils.split(type.getAttribute(value, prefix, "skipped"));
		}
		catch (IndexOutOfBoundsException e) {
			return new HashSet<String>();
		}
	}
	
	public boolean isSkipped(String prefix) {
		return !getSkipped(prefix).isEmpty();
	}
	
	public boolean isAcceptedWarning(SurveyValidationRule rule, String prefix) {
		try {
			return Utils.split(type.getAttribute(value, prefix, "warning")).contains(rule.getId().toString());
		}
		catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	@Transient
	public Set<String> getSkippedPrefixes() {
		return getPrefixesWithAttribute("skipped").keySet();
	}
	
	@Transient
	public Set<String> getInvalidPrefixes() {
		return getPrefixesWithAttribute("invalid").keySet();
	}
	
	@Transient
	public Boolean isInvalid() {
		// we get the list of all the invalid prefixes that
		// have not been accepted
		Map<String, Value> invalidPrefixes = getPrefixesWithAttribute("invalid");
		Map<String, Value> acceptedPrefixes = getPrefixesWithAttribute("warning");
		
		Set<String> invalidAndUnacceptedPrefixes = new HashSet<String>();
		for (String invalidPrefix : invalidPrefixes.keySet()) {
			Set<String> invalidRules = Utils.split(invalidPrefixes.get(invalidPrefix).getAttribute("invalid"));
			Set<String> acceptedRules = new HashSet<String>();
			if (acceptedPrefixes.containsKey(invalidPrefix)) {
				acceptedRules.addAll(Utils.split(acceptedPrefixes.get(invalidPrefix).getAttribute("warning")));
			}
			
			if (!CollectionUtils.subtract(invalidRules, acceptedRules).isEmpty()) invalidAndUnacceptedPrefixes.add(invalidPrefix);
		}
		
		// element is invalid if those prefixes are not all skipped
		Set<String> skippedPrefixes = getSkippedPrefixes();
		
		return !CollectionUtils.subtract(
			invalidAndUnacceptedPrefixes, 
			skippedPrefixes
		).isEmpty();
	}
	
	@Transient
	public Set<String> getNullPrefixes() {
		return getNullPrefixesMap().keySet();
	}
	
	@Transient
	private Map<String, Value> getNullPrefixesMap() {
		return type.getPrefixes(value, new PrefixPredicate() {
			@Override
			public boolean holds(Type type, Value value, String prefix) {
				return value.isNull();
			}
		});
	}
	
	@Transient
	public Boolean isComplete() {
		// element is complete if all the non-skipped values are not-null
		// regardless of whether they are valid or not
		Set<String> skippedPrefixes = getSkippedPrefixes();
		Map<String, Value> nullPrefixes = getNullPrefixesMap();
		
		return CollectionUtils.subtract(nullPrefixes.keySet(), skippedPrefixes).isEmpty();
	}
	
	private void setAttribute(final String attribute, final String id, Set<String> prefixes) {
		Map<String, Value> prefixesWithId = type.getPrefixes(value, new PrefixPredicate() {
			@Override
			public boolean holds(Type type, Value value, String prefix) {
				return Utils.split(value.getAttribute(attribute)).contains(id);
			}
		});
		
		final Map<String, String> newPrefixAttributes = new HashMap<String, String>();
		// remove the attribute from prefixes which are not in the set
		for (String prefixWithId : prefixesWithId.keySet()) {
			if (!prefixes.contains(prefixWithId)) {
				Set<String> attributeValue = Utils.split(type.getAttribute(value, prefixWithId, attribute));
				attributeValue.remove(id);
				
				String newAttributeValue = Utils.unsplit(attributeValue);
				if (newAttributeValue.isEmpty()) newAttributeValue = null;
				newPrefixAttributes.put(prefixWithId, newAttributeValue);
			}
		}
		
		// add it on prefixes which are in the set
		for (String prefix : prefixes) {
			Set<String> attributeValue = Utils.split(type.getAttribute(value, prefix, attribute));
			attributeValue.add(id);
			newPrefixAttributes.put(prefix, Utils.unsplit(attributeValue));
		}
		
		// set the attribute to the new value
		type.transformValue(value, new ValuePredicate() {
			
			@Override
			public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
				if (newPrefixAttributes.containsKey(currentPrefix)) {
					String currentAttribute = currentValue.getAttribute(attribute);
					if (currentAttribute == null || !currentAttribute.equals(newPrefixAttributes.get(currentPrefix))) {
						currentValue.setAttribute(attribute, newPrefixAttributes.get(currentPrefix));
						return true;
					}
				}
				return false;
			}
			
		});
	}
	
	private Map<String, Value> getPrefixesWithAttribute(final String attribute) {
		return type.getPrefixes(value, new PrefixPredicate() {
			@Override
			public boolean holds(Type type, Value value, String prefix) {
				String attributeValue = value.getAttribute(attribute);
				if (attributeValue != null) return true;
				return false;
			}
		});
	}
	
	public void mergeValue(Map<String, Object> params, String prefix) {
		Set<String> attributes = new HashSet<String>();
		attributes.add("warning");
		
		if (log.isDebugEnabled()) log.debug("getting new value from parameters for prefix: "+prefix);
		Value value = getType().mergeValueFromMap(getValue(), params, prefix, attributes);
		
		// reset accepted warnings for changed values
		if (log.isDebugEnabled()) log.debug("resetting warning for modified prefix: "+prefix);
		getType().transformValue(value, new ValuePredicate() {
			@Override
			public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
				Value oldPrefix = getType().getValue(getValue(), currentPrefix);
				if (oldPrefix != null && oldPrefix.getAttribute("warning") != null) {
					if (!oldPrefix.getValueWithoutAttributes().equals(currentValue.getValueWithoutAttributes())) {
						currentValue.setAttribute("warning", null);
						return true;
					}
				}
				return false;
			}
		});
		
		// set the new value
		getType().setValue(getValue(), "", value);
	}

}
