package org.chai.kevin.dashboard;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Map;

import org.chai.kevin.CalculationInfo;
import org.chai.kevin.NormalizedDataElementInfo;
import org.chai.kevin.Info;
import org.chai.kevin.InfoService;
import org.chai.kevin.Organisation;
import org.chai.kevin.data.Expression;
import org.hisp.dhis.period.Period;

public class ExplanationCalculator extends PercentageCalculator {

	private InfoService infoService;
	
	public ExplanationCalculator() {
		super();
		// TODO Auto-generated constructor stub
	}

//	private static Log log = LogFactory.getLog(ExplanationCalculator.class);
	
	public DashboardExplanation explainNonLeafTarget(DashboardTarget target, Organisation organisation, Period period) {
		CalculationInfo info = infoService.getInfo(target.getCalculation(), organisation, period);
		if (info == null) return null;
		return new DashboardExplanation(info, target, organisation);
	}
	
	
	public DashboardExplanation explainLeafTarget(DashboardTarget target, Organisation organisation, Period period) {
		Expression expression = expressionService.getMatchingExpression(target.getCalculation().getExpressions(), organisation);
		NormalizedDataElementInfo info = null;
		if (expression != null) {
			info = infoService.getInfo(expression, organisation, period, null);
			if (info == null) return null;
		}
		return new DashboardExplanation(info, target, organisation);
	}

	public DashboardExplanation explainObjective(DashboardObjective objective, Organisation organisation, Period period) {
		DashboardPercentage percentage = getPercentageForObjective(objective, organisation, period);
		if (percentage == null) return null;
		Map<DashboardObjectiveEntry, DashboardPercentage> values = getValues(objective, organisation, period);
		Info info = new DashboardObjectiveInfo(percentage, organisation, values);
		
		return new DashboardExplanation(info, objective, organisation);
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	
}
