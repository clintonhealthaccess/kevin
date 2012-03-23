package org.chai.kevin.dashboard;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DashboardPercentageService {

	private static Type type = Type.TYPE_NUMBER();
	
//	private InfoService infoService;
	private DashboardService dashboardService;
	private ValueService valueService;

	private class PercentageVisitor implements DashboardVisitor<DashboardPercentage> {
		
		private Set<DataEntityType> types;
		
		public PercentageVisitor(Set<DataEntityType> types) {
			this.types = types;
		}

		private final Log log = LogFactory.getLog(PercentageVisitor.class);
		
		@Override
		public DashboardPercentage visitProgram(DashboardProgram program, CalculationEntity entity, Period period) {
			if (log.isDebugEnabled()) log.debug("visitProgram(program="+program+",entity="+entity+",period="+period+")");
			
			Integer totalWeight = 0;
			Double sum = 0.0d;

			List<DashboardEntity> dashboardEntities = dashboardService.getDashboardEntitiesWithTargets(program.getProgram());
			for (DashboardEntity child : dashboardEntities) {
				DashboardPercentage childPercentage = child.visit(this, entity, period);
				if (childPercentage == null) {
					if (log.isErrorEnabled()) log.error("found null percentage, program: "+child+", entity: "+entity+", period: "+period);
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
			DashboardPercentage percentage = new DashboardPercentage(value, entity, period);
			
			if (log.isDebugEnabled()) log.debug("visitProgram()="+percentage);
			return percentage;
		}

		@Override
		public DashboardPercentage visitTarget(DashboardTarget target, CalculationEntity entity, Period period) {
			if (log.isDebugEnabled()) log.debug("visitTarget(target="+target+",entity="+entity+",period="+period+")");
			
			CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getCalculation(), entity, period, types);
			if (calculationValue == null) return null;
			DashboardPercentage percentage = new DashboardPercentage(calculationValue.getValue(), entity, period);

			if (log.isDebugEnabled()) log.debug("visitTarget(...)="+percentage);
			return percentage;
		}
	}
	
//	private class ExplanationVisitor implements DashboardVisitor<Info> {
//
//		private Set<DataEntityType> types;
//		
//		public ExplanationVisitor(Set<DataEntityType> types) {
//			this.types = types;
//		}
//		
//		@Override
//		public Info visitProgram(DashboardProgram program, CalculationEntity entity, Period period) {
//			DashboardPercentage percentage = program.visit(new PercentageVisitor(types), entity, period);
//			if (percentage == null) return null;
//			List<DashboardEntity> dashboardEntities = getDashboardEntities(program.getProgram());
//			Map<DashboardEntity, DashboardPercentage> values = getValues(dashboardEntities, period, entity, types);
//			return new DashboardProgramInfo(percentage, values);
//		}
//
//		@Override
//		public Info visitTarget(DashboardTarget target, CalculationEntity entity, Period period) {
//			return infoService.getCalculationInfo(target.getCalculation(), entity, period, types);
//		}
//		
//	}
	
	
	@Transactional(readOnly = true)
	@Cacheable(cache="dashboardCache")
	public DashboardPercentage getDashboardValue(Period period, CalculationEntity location, Set<DataEntityType> types, DashboardEntity dashboardEntity) {
		return dashboardEntity.visit(new PercentageVisitor(types), location, period);
	}
	
	public void setDashboardService(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
}
