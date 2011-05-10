package org.chai.kevin.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

public class TargetExplanation extends Explanation {

	private List<DashboardExpressionExplanation> expressionExplanations;

	private Map<Organisation, DashboardPercentage> values;
	
	public TargetExplanation(DashboardPercentage average, OrganisationUnitLevel level) {
		super(average, level);
	}
	
	public TargetExplanation(DashboardExpressionExplanation expressionExplanation, DashboardPercentage average, OrganisationUnitLevel level) {
		super(average, level);
		this.expressionExplanations = new ArrayList<DashboardExpressionExplanation>();
		this.expressionExplanations.add(expressionExplanation);
	}

	public TargetExplanation(List<DashboardExpressionExplanation> expressionExplanations,
			DashboardPercentage average, OrganisationUnitLevel level, Map<Organisation, DashboardPercentage> values) {
		super(average, level);
		this.expressionExplanations = expressionExplanations;
		this.values = values;
	}

	public Map<Organisation, DashboardPercentage> getValues() {
		return values;
	}
	
	public List<DashboardExpressionExplanation> getExpressionExplanations() {
		return expressionExplanations;
	}
	
	public boolean isTarget() {
		return true;
	}

	public static class RelevantData {

		AbstractNameableObject element;
		Object value;
		
		public RelevantData(AbstractNameableObject element, Object value) {
			super();
			this.element = element;
			this.value = value;
		}
		
		public AbstractNameableObject getElement() {
			return element;
		}

		public Object getValue() {
			return value;
		}
	}


}
