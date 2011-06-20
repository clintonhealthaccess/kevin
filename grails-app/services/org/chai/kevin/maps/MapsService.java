package org.chai.kevin.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class MapsService {

	private static final Log log = LogFactory.getLog(MapsService.class);
	
	private ExpressionService expressionService;
	private OrganisationService organisationService;
	
	public Maps getMap(Period period, Organisation organisation, Integer level, MapsTarget target) {
		if (log.isDebugEnabled()) log.debug("getMap(period="+period+",organisation="+organisation+",target="+target+")");

		List<Polygon> polygons = new ArrayList<Polygon>();
		organisationService.loadParent(organisation);
		organisationService.getLevel(organisation);
		
		List<OrganisationUnitLevel> levels = organisationService.getAllLevels();
		levels.remove(0);
		
		if (levels.isEmpty()) {
			// TODO throw exception
		}
		
		if (level == null) {
			List<OrganisationUnitLevel> childLevels = organisationService.getChildren(organisationService.getLevel(organisation));
			if (levels.size() > 0) level = childLevels.get(0).getLevel();
			else level = levels.get(0).getLevel();
		}
		
		// if we ask for an organisation level bigger than the organisation's, we go back to the right level
		while (level <= organisation.getLevel()) {
			organisation = organisation.getParent();
			organisationService.loadParent(organisation);
		}
		
		if (target == null) return new Maps(period, target, organisation, level, polygons, levels);
		
		for (Organisation child : organisationService.getChildrenOfLevel(organisation, level)) {
			organisationService.getLevel(child);
			
			Double value = Double.parseDouble(expressionService.calculateValue(target.getExpression(), period, child).getValue());
//			if (ExpressionService.hasNullValues(values.values())) value = null;
			polygons.add(new Polygon(child, value));
		}

		return new Maps(period, target, organisation, level, polygons, levels);
	}

	public MapsExplanation getExplanation(Period period, Organisation organisation, MapsTarget target) {
		Double value = Double.parseDouble(expressionService.calculateValue(target.getExpression(), period, organisation).getValue());
//		if (ExpressionService.hasNullValues(values.values())) value = null;
		
		return new MapsExplanation(organisation, target, period, value);
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
}
