package org.chai.kevin.survey.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections.CollectionUtils;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.PrefixPredicate;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveySkipRule;
import org.chai.kevin.survey.SurveyValidationRule;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;

@Entity(name="SurveyEnteredValue")
@Table(name="dhsst_survey_entered_value", 
		uniqueConstraints=@UniqueConstraint(columnNames={"surveyElement", "organisationUnit"}
))
public class SurveyEnteredValue {

	private Long id;
	private SurveyElement surveyElement;
	private Value value;
	private Value lastValue;
	private OrganisationUnit organisationUnit;
	
	private Map<SurveySkipRule, String> skipped = new HashMap<SurveySkipRule, String>();
	private Map<SurveyValidationRule, String> invalid = new HashMap<SurveyValidationRule, String>();
	private Map<SurveyValidationRule, String> acceptedWarnings = new HashMap<SurveyValidationRule, String>();
	
	public SurveyEnteredValue() {}
	
	public SurveyEnteredValue(SurveyElement surveyElement, OrganisationUnit organisationUnit, Value value, Value lastValue) {
		this.surveyElement = surveyElement;
		this.organisationUnit = organisationUnit;
		this.value = value;
		this.lastValue = lastValue;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@NaturalId
	@OneToOne(targetEntity=SurveyElement.class)
	public SurveyElement getSurveyElement() {
		return surveyElement;
	}
	
	public void setSurveyElement(SurveyElement surveyElement) {
		this.surveyElement = surveyElement;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonValue", column=@Column(name="value", nullable=false))
	})
	public Value getValue() {
		return value;
	}
	
	public void setValue(Value value) {
		this.value = value;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonValue", column=@Column(name="last_value", nullable=true))
	})
	public Value getLastValue() {
		return lastValue;
	}
	
	public void setLastValue(Value lastValue) {
		this.lastValue = lastValue;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=OrganisationUnit.class, optional=false)
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	
	@ElementCollection
	@CollectionTable(name="dhsst_survey_value_invalid")
	@MapKeyJoinColumn
	public Map<SurveyValidationRule, String> getInvalid() {
		return invalid;
	}
	
	public void setInvalid(Map<SurveyValidationRule, String> invalid) {
		this.invalid = invalid;
	}
	
	public void addInvalid(SurveyValidationRule validationRule, Set<String> prefixes) {
		if (prefixes.isEmpty()) invalid.remove(validationRule);
		else invalid.put(validationRule, Utils.unsplit(prefixes));
	}

	public List<SurveyValidationRule> getErrors(String prefix) {
		List<SurveyValidationRule> result = new ArrayList<SurveyValidationRule>();
		for (Entry<SurveyValidationRule, String> entry : invalid.entrySet()) {
			if (split(entry.getValue()).contains(prefix) && !isAcceptedWarning(entry.getKey(), prefix)) result.add(entry.getKey());
		}
		return result;
	}
	
	public boolean isValid(String prefix) {
		for (Entry<SurveyValidationRule, String> entry : invalid.entrySet()) {
			if (split(entry.getValue()).contains(prefix) && !isAcceptedWarning(entry.getKey(), prefix)) return false;
		}
		return true;
	}
	
	@ElementCollection
	@CollectionTable(name="dhsst_survey_value_skipped")
	@MapKeyJoinColumn
	public Map<SurveySkipRule, String> getSkipped() {
		return skipped;
	}
	
	public void setSkipped(Map<SurveySkipRule, String> skipped) {
		this.skipped = skipped;
	}
	
	public void addSkipped(SurveySkipRule skipRule, Set<String> prefixes) {
		if (prefixes.isEmpty()) skipped.remove(skipRule);
		else skipped.put(skipRule, Utils.unsplit(prefixes));
	}
	
	public boolean isSkipped(String prefix) {
		for (Entry<SurveySkipRule, String> entry : skipped.entrySet()) {
			if (split(entry.getValue()).contains(prefix)) return true;
		}
		return false;
	}
	
	@ElementCollection
	@CollectionTable(name="dhsst_survey_value_accepted_warning")
	@MapKeyJoinColumn
	public Map<SurveyValidationRule, String> getAcceptedWarnings() {
		return acceptedWarnings;
	}
	
	public void setAcceptedWarnings(Map<SurveyValidationRule, String> acceptedWarnings) {
		this.acceptedWarnings = acceptedWarnings;
	}
	
	public void addAcceptedWarning(SurveyValidationRule rule, String prefix) {
		// TODO
	}
	
	public void removeAcceptedWarning(SurveyValidationRule rule, String prefix) {
		// TODO
	}
	
//	public List<SurveyValidationRule> getAcceptedWarnings(String prefix) {
//		
//	}
	
	public boolean isAcceptedWarning(SurveyValidationRule rule, String prefix) {
		if (acceptedWarnings.containsKey(rule)) {
			return split(acceptedWarnings.get(rule)).contains(prefix);
		}
		return false;
	}
	
	@Transient
	public Set<String> getSkippedPrefixes() {
		return split(skipped.values());
	}
	
	@Transient
	public Set<String> getInvalidPrefixes() {
		return split(invalid.values());
	}

	private static Set<String> split(Collection<String> strings) {
		Set<String> result = new HashSet<String>();
		for (String string : strings) {
			result.addAll(split(string));
		}
		return result;
	}
	
	private static Set<String> split(String string) {
		Set<String> result = new HashSet<String>();
		if (string.isEmpty()) result.add(string);
		result.addAll(Utils.split(string));
		return result;
	}
	
	@Transient
	public boolean isInvalid() {
		// element is invalid if some non-skipped values are invalid
		// and not in the accepted warning list
		Set<String> invalidPrefixes = split(invalid.values());
		Set<String> skippedPrefixes = split(skipped.values());
		Set<String> acceptedPrefixes = split(acceptedWarnings.values());
		
		return !CollectionUtils.subtract(
			invalidPrefixes, 
			CollectionUtils.union(skippedPrefixes, acceptedPrefixes)
		).isEmpty();
	}
	
	@Transient
	public boolean isComplete() {
		// element is complete if all the non-skipped values are not-null
		// regardless of whether they are valid or not
		Set<String> skippedPrefixes = split(skipped.values());
		Map<String, Value> nullPrefixes = new HashMap<String, Value>();
		surveyElement.getDataElement().getType().getPrefixes(value, "", nullPrefixes, new PrefixPredicate() {
			@Override
			public boolean holds(Type type, Value value, String prefix) {
				return value.isNull();
			}
		});
		
		return CollectionUtils.subtract(nullPrefixes.entrySet(), skippedPrefixes).isEmpty();
	}
	
	@Override
	public String toString() {
		return "SurveyEnteredValue [surveyElement=" + surveyElement
				+ ", value=" + value + ", organisationUnit=" + organisationUnit
				+ ", acceptedWarnings=" + acceptedWarnings.size() + "]";
	}
	
	@Transient
	public Survey getSurvey() {
		return surveyElement.getSurvey();
	}


}
