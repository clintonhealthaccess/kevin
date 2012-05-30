package org.chai.kevin.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Translation;
import org.chai.kevin.entity.export.Exportable;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.util.Utils;

@Entity(name="FormValidationRule")
@Table(name="dhsst_form_validation_rule")
public class FormValidationRule {

	private final static Log log = LogFactory.getLog(FormValidationRule.class);
	
	private Long id;
	private String code;
	
	private FormElement formElement;
	private String prefix = "";
	
	private String expression;
	private Boolean allowOutlier;

	private Translation messages = new Translation();
	private List<FormElement> dependencies = new ArrayList<FormElement>();
	private String typeCodeString;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=FormElement.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public FormElement getFormElement() {
		return formElement;
	}
	
	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}
	
	@Lob
	@Column(nullable=false)
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Embedded
		@AttributeOverrides({
	    @AttributeOverride(name="jsonText", column=@Column(name="jsonMessages", nullable=false))
	})
	public Translation getMessages() {
		return messages;
	}
	
	public void setMessages(Translation messages) {
		this.messages = messages;
	}
	
	@ManyToMany(targetEntity=FormElement.class)
	@JoinTable(name="dhsst_form_validation_dependencies")
	public List<FormElement> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<FormElement> dependencies) {
		this.dependencies = dependencies;
	}
	
	@Lob
	public String getTypeCodeString() {
		return typeCodeString;
	}

	public void setTypeCodeString(String typeCodeString) {
		this.typeCodeString = typeCodeString;
	}
	
	@Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes);
	}
	
	@Basic
	@Column(nullable=false)
	public Boolean getAllowOutlier() {
		return allowOutlier;
	}
	
	public void setAllowOutlier(Boolean allowOutlier) {
		this.allowOutlier = allowOutlier;
	}
	
	@Basic
	@Column(nullable=false)
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}	
	
	@Transient
	public void deepCopy(FormValidationRule copy, FormCloner cloner) {
		copy.setAllowOutlier(getAllowOutlier());
		copy.setPrefix(getPrefix());
		copy.setExpression(cloner.getExpression(getExpression(), copy));
		copy.setFormElement(cloner.getElement(getFormElement()));
		copy.setMessages(getMessages());
		copy.setTypeCodeString(getTypeCodeString());
		for (FormElement element : getDependencies()) {
			FormElement newElement = cloner.getElement(element);
			if (newElement.equals(element)) {
				cloner.addUnchangedValidationRule(this, element.getId());
			}
			copy.getDependencies().add(newElement);
		}
	}
	
	@Transient
	public void evaluate(DataLocation dataLocation, ElementCalculator calculator) {
		if (log.isDebugEnabled()) log.debug("evaluate(location="+dataLocation+") on "+this);
		Set<String> prefixes = calculator.getFormValidationService().getInvalidPrefix(this, dataLocation, calculator.getValidatableLocator());

		FormEnteredValue enteredValue = calculator.getFormElementService().getOrCreateFormEnteredValue(dataLocation, this.getFormElement());
		enteredValue.getValidatable().setInvalid(this, prefixes);
		
		calculator.addAffectedValue(enteredValue);
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
		if (!(obj instanceof FormValidationRule))
			return false;
		FormValidationRule other = (FormValidationRule) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public String toString() {
		return "FormValidationRule[getId()=" + getId() + ", getExpression()='" + getExpression() + "']";
		}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	@Override
//	public String toExportString() {
//		return "[" + Utils.formatExportCode(getCode()) + "]";
//	}
}
