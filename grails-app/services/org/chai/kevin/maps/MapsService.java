package org.chai.kevin.maps;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.period.Period;

public class MapsService {

	private static final Log log = LogFactory.getLog(MapsService.class);
	
	private AggregationService aggregationService;
	private OrganisationService organisationService;
	
	public Map getMap(Period period, Organisation organisation, MapsTarget target) {
		if (log.isDebugEnabled()) log.debug("getMap(period="+period+",organisation="+organisation+",target="+target+")");

		List<Polygon> polygons = new ArrayList<Polygon>();
		organisationService.loadChildren(organisation);
		for (Organisation child : organisation.getChildren()) {
			Double value = aggregationService.getAggregatedIndicatorValue(target.getIndicator(), period.getStartDate(), period.getEndDate(), child.getOrganisationUnit());
			polygons.add(new Polygon(child, value));
		}

		return new Map(polygons);
	}
	
	
	public void setAggregationService(AggregationService aggregationService) {
		this.aggregationService = aggregationService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
}
