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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ValueService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class CostTableService {

	private final static Log log = LogFactory.getLog(CostTableService.class);
	
	private ReportService reportService;
	private CostService costService;
	private LocationService locationService;
	private ValueService valueService;
	private Set<String> skipLevels;
	
	public CostTable getCostTable(Period period, ReportProgram program, CalculationEntity entity) {
		List<CostTarget> targets = reportService.getReportTargets(CostTarget.class, program);
		return new CostTable(targets, costService.getYears(), getValues(period, targets, entity));
	}

	public Explanation getExplanation(Period period, CostTarget target, LocationEntity entity) {
		Map<CalculationEntity, Map<Integer, Cost>> explanationMap = new HashMap<CalculationEntity, Map<Integer,Cost>>();
		
		for (CalculationEntity child : entity.getChildren()) {
			explanationMap.put(child, getCost(target, child, period));
		}
		for (DataLocationEntity child : entity.getDataEntities()) {
			if (appliesToLocation(target, child)) {
				explanationMap.put(child, getCost(target, child, period));
			}
		}

		List<DataEntityType> types = new ArrayList<DataEntityType>();
		for (String typeCode : Utils.split(target.getTypeCodeString())) {
			types.add(locationService.findDataEntityTypeByCode(typeCode));
		}
		return new Explanation(target, types, target.getProgram(), period, new ArrayList<CalculationEntity>(explanationMap.keySet()), costService.getYears(), explanationMap);
	}
	
	private boolean appliesToLocation(CostTarget target, DataLocationEntity entity) {
		return Utils.split(target.getTypeCodeString()).contains(entity.getType().getCode());
	}
	
	private Map<Integer, Cost> getCost(CostTarget target, CalculationEntity entity, Period period) {
		Map<Integer, Cost> result = new HashMap<Integer, Cost>();

		for (Integer year : costService.getYears()) {
			result.put(year, new Cost());
		}
		for (DataLocationEntity dataLocationEntity : entity.getDataEntities()) {
			if (appliesToLocation(target, dataLocationEntity)) {
				Map<Integer, Cost> costs = getCostForLeafLocation(target, dataLocationEntity, period);
				
				for (Integer year : costService.getYears()) {
					if (costs.containsKey(year)) {
						result.get(year).addValue(costs.get(year).getValue());
						if (costs.get(year).isHasMissingValue()) result.get(year).hasMissingValue();
					}
				}
			}
		}
		for (LocationEntity locationEntity : entity.getChildren()) {
			Map<Integer, Cost> costs = getCost(target, locationEntity, period);
			
			for (Integer year : costService.getYears()) {
				if (costs.containsKey(year)) {
					result.get(year).addValue(costs.get(year).getValue());
					if (costs.get(year).isHasMissingValue()) result.get(year).hasMissingValue();
				}
			}
		}
		return result;
	}
	
	private Map<Integer, Cost> getCostForLeafLocation(CostTarget target, DataLocationEntity entity, Period period) {
		if (log.isDebugEnabled()) log.debug("getCostForLeafLocation(target="+target+", entity:"+entity+", period:"+period+")");
		
		Map<Integer, Cost> result = new HashMap<Integer, Cost>();
		if (appliesToLocation(target, entity)) {
			boolean hasMissingValues = false;

			if (log.isDebugEnabled()) log.debug("target "+target+" applies to entity "+entity);
			List<Integer> years = costService.getYears();

			
			DataValue expressionValue = valueService.getDataElementValue(target.getDataElement(), entity, period);
			DataValue expressionEndValue = null;
			if (target.isAverage()) expressionEndValue = valueService.getDataElementValue(target.getDataElementEnd(), entity, period);
			if (expressionValue != null && expressionValue.getValue().getNumberValue() != null && (!target.isAverage() || (expressionEndValue != null && expressionEndValue.getValue().getNumberValue() != null))) { 
				Double baseCost = expressionValue.getValue().getNumberValue().doubleValue();

				// FIXME
	//			if (ExpressionService.hasNullValues(values.values())) hasMissingValues = true;
				
				Double steps = 0d;
				if (target.isAverage()) {
					Double endCost = expressionEndValue.getValue().getNumberValue().doubleValue();
					// FIXME
	//				if (ExpressionService.hasNullValues(values.values())) hasMissingValues = true;
					steps = (endCost - baseCost)/(years.size()-1);
				}
	
				for (Integer year : years) {
					Double yearCost;
					if (target.isAverage()) {
						yearCost = ( baseCost + steps * (year-1) ) * target.getCostRampUp().getYears().get(year).getValue();
					}
					else {
						yearCost = baseCost * target.getCostRampUp().getYears().get(year).getValue();
					}
					// TODO add inflation
					Cost cost = new Cost(yearCost, hasMissingValues);
					result.put(year, cost);
				}
			}
		}
		else {
			if (log.isDebugEnabled()) log.debug("skipping location: "+entity);
		}
		
		if (log.isDebugEnabled()) log.debug("getCostForLeafTarget(): "+result);
		return result;
	}
	
	private Map<CostTarget, Map<Integer, Cost>> getValues(Period period, List<CostTarget> targets, CalculationEntity entity) {
		Map<CostTarget, Map<Integer, Cost>> result = new HashMap<CostTarget, Map<Integer,Cost>>();
		
		for (CostTarget target : targets) {
			result.put(target, getCost(target, entity, period));
		}
		return result;
	}
	
	public void setCostService(CostService costService) {
		this.costService = costService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public List<LocationLevel> getSkipLevelList() {
		List<LocationLevel> result = new ArrayList<LocationLevel>();
		for (String code : skipLevels) {
			result.add(locationService.findLocationLevelByCode(code));
		}
		return result;
	}
	
}
