package org.chai.kevin.form;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Translation;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.util.Utils;

@Entity(name = "FormSkipRule")
@Table(name = "dhsst_form_skip_rule")
@Inheritance(strategy = InheritanceType.JOINED)
public class FormSkipRule {

	private final static Log log = LogFactory.getLog(FormSkipRule.class);
	
	private Long id;
	private String code;
	private String expression;
	private Translation descriptions = new Translation();
	private Map<FormElement, String> skippedFormElements = new HashMap<FormElement, String>();

	public FormSkipRule() {
		super();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Lob
	@Column(nullable = false)
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonDescriptions", nullable = false)) })
	public Translation getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Translation descriptions) {
		this.descriptions = descriptions;
	}

	@Lob
	@ElementCollection
	@CollectionTable(name = "dhsst_form_skipped_form_elements")
	@MapKeyJoinColumn
	public Map<FormElement, String> getSkippedFormElements() {
		return skippedFormElements;
	}

	public void setSkippedFormElements(Map<FormElement, String> skippedFormElements) {
		this.skippedFormElements = skippedFormElements;
	}
	
	@Transient
	public Set<String> getSkippedPrefixes(FormElement element) {
		Set<String> result = new HashSet<String>();
		if (skippedFormElements.containsKey(element)) {
			String text = skippedFormElements.get(element);
			if (text.isEmpty()) result.add(text);
			result.addAll(Utils.split(text));
		}
		return result;
	}

	public void deepCopy(FormSkipRule copy, FormCloner formCloner) {
		copy.setExpression(formCloner.getExpression(getExpression(), copy));
		for (Entry<FormElement, String> entry : getSkippedFormElements().entrySet()) {
			copy.getSkippedFormElements().put(formCloner.getElement(entry.getKey()), entry.getValue());
		}
	}
	
	public void evaluate(DataLocation dataLocation, ElementCalculator calculator) {
		if (log.isDebugEnabled()) log.debug("evaluate(dataLocation="+dataLocation+") on "+this);
		
		for (FormElement formElement : getSkippedFormElements().keySet()) {
			Set<String> prefixes = calculator.getFormValidationService().getSkippedPrefix(formElement, this, dataLocation, calculator.getValidatableLocator());
	
			FormEnteredValue enteredValue = calculator.getFormElementService().getOrCreateFormEnteredValue(dataLocation, formElement);
			enteredValue.getValidatable().setSkipped(this, prefixes);
			
			calculator.addAffectedValue(enteredValue);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormSkipRule other = (FormSkipRule) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}