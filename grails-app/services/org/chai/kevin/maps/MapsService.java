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

public class MapsService {

	private static final Log log = LogFactory.getLog(MapsService.class);
	
	private ExpressionService expressionService;
	private OrganisationService organisationService;
	private String organisationLevel;
	
	public Maps getMap(Period period, Organisation organisation, OrganisationUnitLevel level, MapsTarget target) {
		if (log.isDebugEnabled()) log.debug("getMap(period="+period+",organisation="+organisation+",target="+target+")");

		List<Polygon> polygons = new ArrayList<Polygon>();
		organisationService.loadParent(organisation);
		organisationService.getLevel(organisation);
		
		if (level != null && level.getLevel() <= organisation.getLevel().getLevel()) {
			// TODO let's not to this but throw an exception
			level = null;
		}
		
		if (level == null) {
			// TODO check this
			List<OrganisationUnitLevel> levels = organisationService.getChildren(organisation.getLevel());
			level = levels.iterator().next();
		}

		if (target == null) return new Maps(period, target, organisation, level, polygons, organisationService.getChildren(organisation.getLevel()));
		
		for (Organisation child : organisationService.getChildrenOfLevel(organisation, level)) {
			organisationService.getLevel(child);
			Map values = new HashMap();
			
			Double value = expressionService.getAggregatedValue(target.getExpression(), period, child, values);
//			if (ExpressionService.hasNullValues(values.values())) value = null;
			polygons.add(new Polygon(child, value));
		}

		return new Maps(period, target, organisation, level, polygons, organisationService.getChildren(organisation.getLevel()));
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setOrganisationLevel(String organisationLevel) {
		this.organisationLevel = organisationLevel;
	}
	
	public String getOrganisationLevel() {
		return organisationLevel;
	}
	
}
