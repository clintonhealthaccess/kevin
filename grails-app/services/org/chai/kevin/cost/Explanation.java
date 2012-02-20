package org.chai.kevin.cost;

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

import java.util.List;
import java.util.Map;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.reports.ReportObjective;
import org.hisp.dhis.period.Period;

public class Explanation {

	// if this is null, it means no expression is defined for the specified objective and location
	private Map<CalculationEntity, Map<Integer, Cost>> costs;
	private List<CalculationEntity> locations;
	private List<Integer> years;
	private CostTarget currentTarget;
	private ReportObjective currentObjective;
	private Period currentPeriod;
	private List<DataEntityType> types;
	
	public Explanation(CostTarget currentTarget, List<DataEntityType> types, ReportObjective currentObjective, Period currentPeriod, List<CalculationEntity> locations, List<Integer> years, Map<CalculationEntity, Map<Integer, Cost>> costs) {
		this.costs = costs;
		this.locations = locations;
		this.years = years;
		this.currentTarget = currentTarget;
		this.currentObjective = currentObjective;
		this.currentPeriod = currentPeriod;
		this.types = types;
	}

	public CostTarget getCurrentTarget() {
		return currentTarget;
	}
	
	public ReportObjective getCurrentObjective() {
		return currentObjective;
	}
	
	public Period getCurrentPeriod() {
		return currentPeriod;
	}
	
	public List<CalculationEntity> getLocations() {
		return locations;
	}
	
	public Cost getCost(CalculationEntity location, Integer year) {
		return costs.get(location).get(year);
	}
	
	public List<DataEntityType> getGroups() {
		return types;
	}
	
	public List<Integer> getYears() {
		return years;
	}
	
}
