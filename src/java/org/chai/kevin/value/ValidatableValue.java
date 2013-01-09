package org.chai.kevin.value;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.PrefixPredicate;
import org.chai.kevin.data.Type.Sanitizer;
import org.chai.kevin.data.Type.ValuePredicate;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.util.DataUtils;

public class ValidatableValue {

	private static final Log log = LogFactory.getLog(ValidatableValue.class);
	private static Sanitizer SANITIZER = new Sanitizer(){
		
		@Override
		public Object sanitizeValue(Object value, Type type, String prefix, String genericPrefix) {
			Object result = null;
			String string = String.valueOf(value);
			switch (type.getType()) {
				case NUMBER:
					if (string.trim().isEmpty()) result = null;
					else {
						try {
							result = Double.parseDouble(string);
						} catch (NumberFormatException e) {
							result = JSONNull.getInstance();
						}
					}
					break;
				case BOOL:
					if (value != null && string.equals("0")) result = false;
					else if (value != null && !string.equals("") && !string.equals("0")) result = true;
					else result = null;
					break;
				case STRING:
				case TEXT:
					if (value == null || string.equals("")) result = null;
					else result = string;
					break;
				case DATE:
					if (value == null || string.equals("")) result = null;
					else {
						try {
							result = DataUtils.parseDate(string);
						} catch (ParseException e) {
							result = null;
						}
					}
					break;
				case ENUM:
					if (value == null || string.equals("")) result = null;
					else result = string; 
					break;
				default:
					if (value == null || string.equals("")) result = null;
					else result = string;
			}
			return result;
		}
		
	};
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
	
	public void setInvalid(FormValidationRule validationRule, Set<String> prefixes) {
		setAttribute("invalid", validationRule.getId().toString(), prefixes);
	}

	public void setSkipped(FormSkipRule skipRule, Set<String> prefixes) {
		setAttribute("skipped", skipRule.getId().toString(), prefixes);
	}
	
	/**
	 * Returns a set of rule ids that are stored in the invalid attribute.
	 * 
	 * @param prefix a set of rule ids that are stored in the invalid attribute.
	 * @return
	 */
	public Set<String> getErrorRules(String prefix) {
		try {
			return DataUtils.split(type.getAttribute(value, prefix, "invalid"), FormElement.FIELD_DELIMITER);
		}
		catch (IndexOutOfBoundsException e) {
			return new HashSet<String>();
		}
	}
	
	public boolean isTreeValid(String prefix) {
		for (String invalidPrefix : getInvalidPrefixes()) {
			if (invalidPrefix.startsWith(prefix)) return false;
		}
		return true;
	}
	
	public boolean isTreeComplete(String prefix) {
		for (String incompletePrefix : getReallyIncompletePrefixes()) {
			if (incompletePrefix.startsWith(prefix)) return false;
		}
		return true;
	}
	
	public boolean isValid(String prefix) {
		return !getInvalidPrefixes().contains(prefix);
	}
	
	public boolean isSkipped(String prefix) {
		return getSkippedPrefixes().contains(prefix);
	}
	
	public boolean isAcceptedWarning(FormValidationRule rule, String prefix) {
		try {
			return DataUtils.split(type.getAttribute(value, prefix, "warning"), FormElement.FIELD_DELIMITER).contains(rule.getId().toString());
		}
		catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	/**
	 * Returns all prefixes whose "skipped" attributes is not empty.
	 * 
	 * @return all prefixes whose "skipped" attributes is not empty.
	 */
	public Set<String> getSkippedPrefixes() {
		return getPrefixesWithAttribute("skipped").keySet();
	}

	/**
	 * Returns all prefixes whose "invalid" attributes is not empty.
	 * 
	 * @return all prefixes whose "invalid" attributes is not empty.
	 */
	public Set<String> getInvalidPrefixes() {
		// we get the list of all the invalid prefixes that
		// have not been accepted
		Map<String, Value> invalidPrefixes = getPrefixesWithAttribute("invalid");
		Map<String, Value> acceptedPrefixes = getPrefixesWithAttribute("warning");
		
		Set<String> invalidAndUnacceptedPrefixes = new HashSet<String>();
		for (String invalidPrefix : invalidPrefixes.keySet()) {
			Set<String> invalidRules = DataUtils.split(invalidPrefixes.get(invalidPrefix).getAttribute("invalid"), FormElement.FIELD_DELIMITER);
			Set<String> acceptedRules = new HashSet<String>();
			if (acceptedPrefixes.containsKey(invalidPrefix)) {
				acceptedRules.addAll(DataUtils.split(acceptedPrefixes.get(invalidPrefix).getAttribute("warning"), FormElement.FIELD_DELIMITER));
			}
			
			if (!CollectionUtils.subtract(invalidRules, acceptedRules).isEmpty()) invalidAndUnacceptedPrefixes.add(invalidPrefix);
		}
		
		// element is invalid if those prefixes are not all skipped
		Set<String> skippedPrefixes = getSkippedPrefixes();
		
		return new HashSet<String>(CollectionUtils.subtract(invalidAndUnacceptedPrefixes, skippedPrefixes));
	}
	
	/**
	 * Returns true if this value is invalid. A value is invalid if one of the contained values
	 * is marked as invalid, is not skipped and is not an accepted warning.
	 * 
	 * @return true if this value is invalid.
	 */
	public Boolean isInvalid() {
		return !getInvalidPrefixes().isEmpty();
	}
	
	/**
	 * Returns a set containing all the prefixes for which isNull() returns true.
	 * 
	 * @return a set containing all the prefixes for which isNull() returns true.
	 */
	public Set<String> getNullPrefixes() {
		return getNullPrefixesMap().keySet();
	}
	
	private Map<String, Value> getNullPrefixesMap() {
		return type.getPrefixes(value, new PrefixPredicate() {
			@Override
			public boolean holds(Type type, Value value, String prefix) {
				return value.isNull();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private Collection<String> getReallyIncompletePrefixes() {
		// element is complete if all the non-skipped values are not-null
		// regardless of whether they are valid or not
		Set<String> skippedPrefixes = getSkippedPrefixes();
		Map<String, Value> nullPrefixes = getNullPrefixesMap();			
		return CollectionUtils.subtract(nullPrefixes.keySet(), skippedPrefixes);
	}
	
	public void setAttribute(String prefix, String attribute, String value) {
		getType().setAttribute(getValue(), prefix, attribute, value);
	}
	
	/**
	 * Returns true if this value is complete. A value is complete if all the containing values are
	 * valid, or skipped, or whose invalid rules are all accepted warning. 
	 * 
	 * @return true if this value is valid.
	 */
	public Boolean isComplete() {
		return getReallyIncompletePrefixes().isEmpty();
	}
	
	private void setAttribute(final String attribute, final String id, Set<String> prefixes) {
		Map<String, Value> prefixesWithId = type.getPrefixes(value, new PrefixPredicate() {
			@Override
			public boolean holds(Type type, Value value, String prefix) {
				return DataUtils.split(value.getAttribute(attribute), FormElement.FIELD_DELIMITER).contains(id);
			}
		});
		
		final Map<String, String> newPrefixAttributes = new HashMap<String, String>();
		// remove the attribute from prefixes which are not in the set
		for (String prefixWithId : prefixesWithId.keySet()) {
			if (!prefixes.contains(prefixWithId)) {
				Set<String> attributeValue = DataUtils.split(type.getAttribute(value, prefixWithId, attribute), FormElement.FIELD_DELIMITER);
				attributeValue.remove(id);
				
				String newAttributeValue = DataUtils.unsplit(attributeValue, FormElement.FIELD_DELIMITER);
				if (newAttributeValue.isEmpty()) newAttributeValue = null;
				newPrefixAttributes.put(prefixWithId, newAttributeValue);
			}
		}
		
		// add it on prefixes which are in the set
		for (String prefix : prefixes) {
			Set<String> attributeValue = DataUtils.split(type.getAttribute(value, prefix, attribute), FormElement.FIELD_DELIMITER);
			attributeValue.add(id);
			newPrefixAttributes.put(prefix, DataUtils.unsplit(attributeValue, FormElement.FIELD_DELIMITER));
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
	
	public void mergeValue(Map<String, Object> params, String prefix, Set<String> attributes) {
		Set<String> newAttributes = new HashSet<String>(attributes);
		newAttributes.add("warning");
		
		if (log.isDebugEnabled()) log.debug("getting new value from parameters for prefix: "+prefix);
		Value value = getType().mergeValueFromMap(getValue(), params, prefix, newAttributes, SANITIZER);
		
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
