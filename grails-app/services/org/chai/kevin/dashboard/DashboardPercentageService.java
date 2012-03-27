package org.chai.kevin.dashboard;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.springframework.transaction.annotation.Transactional;

public class DashboardPercentageService {

	private static Type type = Type.TYPE_NUMBER();
	
//	private InfoService infoService;
	private DashboardService dashboardService;
	private ValueService valueService;

	private class PercentageVisitor implements DashboardVisitor<DashboardPercentage> {
		
		private Set<DataLocationType> types;
		
		public PercentageVisitor(Set<DataLocationType> types) {
			this.types = types;
		}

		private final Log log = LogFactory.getLog(PercentageVisitor.class);
		
		@Override
		public DashboardPercentage visitProgram(DashboardProgram program, CalculationLocation location, Period period) {
			if (log.isDebugEnabled()) log.debug("visitProgram(program="+program+",location="+location+",period="+period+")");
			
			Integer totalWeight = 0;
			Double sum = 0.0d;

			List<DashboardEntity> dashboardEntities = dashboardService.getDashboardEntitiesWithTargets(program.getProgram());
			for (DashboardEntity child : dashboardEntities) {
				DashboardPercentage childPercentage = child.visit(this, location, period);
				if (childPercentage == null) {
					if (log.isErrorEnabled()) log.error("found null percentage, program: "+child+", location: "+location+", period: "+period);
					return null;
				}
				Integer weight = child.getWeight();
				if (childPercentage.isValid()) {
					sum += childPercentage.getGradientValue() * weight;
					totalWeight += weight;
				}
				else {
					// MISSING_EXPRESSION - we skip it
					// MISSING_NUMBER - should we count it in as zero ?
				}

			}
			// TODO what if sum = 0 and totalWeight = 0 ?
			Double average = sum/totalWeight;
			Value value = null;
			if (average.isNaN() || average.isInfinite()) value = Value.NULL_INSTANCE();
			else value = type.getValue(average);
			DashboardPercentage percentage = new DashboardPercentage(value, location, period);
			
			if (log.isDebugEnabled()) log.debug("visitProgram()="+percentage);
			return percentage;
		}

		@Override
		public DashboardPercentage visitTarget(DashboardTarget target, CalculationLocation location, Period period) {
			if (log.isDebugEnabled()) log.debug("visitTarget(target="+target+",location="+location+",period="+period+")");
			
			CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getCalculation(), location, period, types);
			if (calculationValue == null) return null;
			DashboardPercentage percentage = new DashboardPercentage(calculationValue.getValue(), location, period);

			if (log.isDebugEnabled()) log.debug("visitTarget(...)="+percentage);
			return percentage;
		}
	}
	
//	private class ExplanationVisitor implements DashboardVisitor<Info> {
//
//		private Set<DataLocationType> types;
//		
//		public ExplanationVisitor(Set<DataLocationType> types) {
//			this.types = types;
//		}
//		
//		@Override
//		public Info visitProgram(DashboardProgram program, CalculationLocation location, Period period) {
//			DashboardPercentage percentage = program.visit(new PercentageVisitor(types), location, period);
//			if (percentage == null) return null;
//			List<DashboardEntity> dashboardEntities = getDashboardEntities(program.getProgram());
//			Map<DashboardEntity, DashboardPercentage> values = getValues(dashboardEntities, period, location, types);
//			return new DashboardProgramInfo(percentage, values);
//		}
//
//		@Override
//		public Info visitTarget(DashboardTarget target, CalculationLocation location, Period period) {
//			return infoService.getCalculationInfo(target.getCalculation(), location, period, types);
//		}
//		
//	}
	
	
	@Transactional(readOnly = true)
	@Cacheable(cache="dashboardCache")
	public DashboardPercentage getDashboardValue(Period period, CalculationLocation location, Set<DataLocationType> types, DashboardEntity dashboardEntity) {
		return dashboardEntity.visit(new PercentageVisitor(types), location, period);
	}
	
	public void setDashboardService(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
}
