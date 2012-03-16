package org.chai.kevin.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.form.FormValidationService.ValidatableLocator;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.SurveyCloner;
import org.chai.kevin.survey.SurveyElement;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity(name = "FormElement")
@Table(name = "dhsst_form_element")
@Inheritance(strategy = InheritanceType.JOINED)
public class FormElement {

	protected Long id;
	private RawDataElement rawDataElement;
	private List<FormValidationRule> validationRules = new ArrayList<FormValidationRule>();
	private Map<String, Translation> headers = new HashMap<String, Translation>();

	public FormElement() {
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

	@ManyToOne(targetEntity = RawDataElement.class, optional = false)
	@JoinColumn(nullable = false)
	public RawDataElement getDataElement() {
		return rawDataElement;
	}

	public void setDataElement(RawDataElement rawDataElement) {
		this.rawDataElement = rawDataElement;
	}

	@OneToMany(mappedBy = "formElement", targetEntity = FormValidationRule.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	public List<FormValidationRule> getValidationRules() {
		return validationRules;
	}

	public void setValidationRules(List<FormValidationRule> validationRules) {
		this.validationRules = validationRules;
	}

	public void addValidationRule(FormValidationRule validationRule) {
		validationRule.setFormElement(this);
		validationRules.add(validationRule);
	}

	@ElementCollection(targetClass = Translation.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name = "dhsst_form_element_headers")
	public Map<String, Translation> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Translation> headers) {
		this.headers = headers;
	}
	
	@Transient
	public <T extends FormElement> void deepCopy(T copy, SurveyCloner cloner) {
		copy.setDataElement(getDataElement());
	}	
	
	@Transient
	public void copyRules(FormElement copy, SurveyCloner cloner) {
		for (FormValidationRule rule : getValidationRules()) {
			copy.getValidationRules().add(cloner.getValidationRule(rule));
		}
		for (Entry<String, Translation> entry : getHeaders().entrySet()) {
			copy.getHeaders().put(entry.getKey(), entry.getValue());
		}
	}

	@Transient
	public void validate(DataLocationEntity entity, ElementCalculator calculator) {
		Set<FormValidationRule> validationRules = calculator.getFormElementService().searchValidationRules(this, entity.getType());
		
		for (FormValidationRule validationRule : validationRules) {
			validationRule.evaluate(entity, calculator);
		}
	}
	
	@Transient
	public void executeSkip(DataLocationEntity entity, ElementCalculator calculator) {
		Set<FormSkipRule> skipRules = calculator.getFormElementService().searchSkipRules(this);
		
		for (FormSkipRule skipRule: skipRules) {
			skipRule.evaluate(entity, calculator);
		}
	}

	public static class ElementCalculator {
		private FormValidationService formValidationService;
		private FormElementService formElementService;
		private ValidatableLocator validatableLocator;
		private List<FormEnteredValue> affectedValues;
		
		public ElementCalculator(List<FormEnteredValue> affectedValues, FormValidationService formValidationService, FormElementService formElementService, ValidatableLocator validatableLocator) {
			this.formElementService = formElementService;
			this.formValidationService = formValidationService;
			this.validatableLocator = validatableLocator;
			this.affectedValues = affectedValues;
		}
		
		public List<FormEnteredValue> getAffectedValues() {
			return affectedValues;
		}
		
		public ValidatableLocator getValidatableLocator() {
			return validatableLocator;
		}
		
		public FormElementService getFormElementService() {
			return formElementService;
		}
		
		public FormValidationService getFormValidationService() {
			return formValidationService;
		}
		
		public void addAffectedValue(FormEnteredValue value) {
			this.affectedValues.add(value);
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
		if (!(obj instanceof FormElement))
			return false;
		FormElement other = (FormElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}