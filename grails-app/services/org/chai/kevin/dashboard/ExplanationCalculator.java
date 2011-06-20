package org.chai.kevin.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.CalculationInfo;
import org.chai.kevin.DataElement;
import org.chai.kevin.Expression;
import org.chai.kevin.ExpressionInfo;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Info;
import org.chai.kevin.InfoService;
import org.chai.kevin.Organisation;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.period.Period;

public class ExplanationCalculator extends PercentageCalculator {

	private InfoService infoService;
	
	public ExplanationCalculator() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static Log log = LogFactory.getLog(ExplanationCalculator.class);
	
	public DashboardExplanation explainNonLeafTarget(DashboardTarget target, Organisation organisation, Period period) {
		CalculationInfo info = infoService.getInfo(target.getCalculation(), organisation, period);
		if (info == null) return null;
		return new DashboardExplanation(info, target, organisation);
	}
	
	
	public DashboardExplanation explainLeafTarget(DashboardTarget target, Organisation organisation, Period period) {
		Expression expression = expressionService.getMatchingExpression(target.getCalculation(), organisation);
		ExpressionInfo info = null;
		if (expression != null) {
			info = infoService.getInfo(expression, organisation, period);
			if (info == null) return null;
		}
		return new DashboardExplanation(info, target, organisation);
	}

	public DashboardExplanation explainObjective(DashboardObjective objective, Organisation organisation, Period period) {
		DashboardPercentage percentage = getPercentageForObjective(objective, organisation, period);
		if (percentage == null) return null;
		Map<DashboardObjectiveEntry, DashboardPercentage> values = getValues(objective, organisation, period);
		Info info = new DashboardObjectiveInfo(percentage, values);
		
		return new DashboardExplanation(info, objective, organisation);
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	
}
