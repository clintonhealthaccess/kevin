package org.chai.kevin.planning;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.NotImplementedException;
import org.chai.kevin.form.FormCloner;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.location.DataLocation;

@Entity(name="PlanningSkipRule")
@Table(name="dhsst_planning_skip_rule")
public class PlanningSkipRule extends FormSkipRule {

	private Planning planning;
	
	@ManyToOne(targetEntity=Planning.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Planning getPlanning() {
		return planning;
	}
	
	public void setPlanning(Planning planning) {
		this.planning = planning;
	}
	
	@Override
	public void deepCopy(FormSkipRule copy, FormCloner surveyCloner) {
		throw new NotImplementedException();
	}
	
	@Override
	public void evaluate(DataLocation datLocation, ElementCalculator calculator) {
		super.evaluate(datLocation, calculator);
	}

}
