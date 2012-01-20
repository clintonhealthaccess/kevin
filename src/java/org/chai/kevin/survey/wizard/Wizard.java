package org.chai.kevin.survey.wizard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.data.Type;
import org.chai.kevin.survey.SurveySimpleQuestion;
import org.chai.kevin.value.Value;

@Entity
@Table(name="dhsst_survey_workflow")
public class Wizard extends SurveySimpleQuestion {

	private String fixedHeaderPrefix;
	private List<WizardStep> steps = new ArrayList<WizardStep>();
	
	@Basic
	public String getFixedHeaderPrefix() {
		return fixedHeaderPrefix;
	}
	
	public void setFixedHeaderPrefix(String fixedHeaderPrefix) {
		this.fixedHeaderPrefix = fixedHeaderPrefix;
	}
	
	@OneToMany(mappedBy="wizard", targetEntity=WizardStep.class)
	public List<WizardStep> getSteps() {
		return steps;
	}
	
	public void setSteps(List<WizardStep> steps) {
		this.steps = steps;
	}

	@Transient
	@Override
	public QuestionType getType() {
		return QuestionType.WIZARD;
	}
	
	@Override
	protected Wizard newInstance() {
		return new Wizard();
	}

	@Transient
	public Type getType(WizardStep step) {
		return getSurveyElement().getDataElement().getType().getType(step.getPrefix());
	}
	
	@Transient
	public Value getValue(Value value, WizardStep step) {
		return getType(step).getValue(value, step.getPrefix());
	}
	
	@Transient
	public SortedMap<String, Value> getValueList(Value value) {
		List<String> strings = new ArrayList<String>();
		strings.add(fixedHeaderPrefix);
		Set<List<String>> combinations = new HashSet<List<String>>();
		getSurveyElement().getDataElement().getType().getCombinations(value, strings, combinations, "");
		
		SortedMap<String, Value> result = new TreeMap<String, Value>();
		for (List<String> combination : combinations) {
			result.put(combination.get(0), getSurveyElement().getDataElement().getType().getValue(value, combination.get(0)));
		}
		return result;
	}
}
