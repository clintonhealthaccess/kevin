package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.dashboard.DashboardEntity;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardTarget;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class ReportService {
	
	private static final Log log = LogFactory.getLog(ReportService.class);
	
	private SessionFactory sessionFactory;
	private OrganisationService organisationService;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public Map<Organisation, List<Organisation>> getParents(List<Organisation> organisations, Integer level) {									
		
		Map<Organisation, List<Organisation>> organisationMap = new HashMap<Organisation, List<Organisation>>();
		
		for (Organisation organisation : organisations){			
			Organisation parentOrganisation = organisationService.getParentOfLevel(organisation, level);			
			if(!organisationMap.containsKey(parentOrganisation))
				organisationMap.put(parentOrganisation, new ArrayList<Organisation>());
			organisationMap.get(parentOrganisation).add(organisation);
		}
				
		//sort organisation map keys
		List<Organisation> sortedOrganisations = new ArrayList<Organisation>(organisationMap.keySet());
		Collections.sort(sortedOrganisations, OrganisationSorter.BY_LEVEL);
		
		//sort organisation map values
		LinkedHashMap<Organisation, List<Organisation>> sortedOrganisationMap = new LinkedHashMap<Organisation, List<Organisation>>();		
		for (Organisation org : sortedOrganisations){
			List<Organisation> sortedList = organisationMap.get(org);
			Collections.sort(sortedList, OrganisationSorter.BY_LEVEL);
			sortedOrganisationMap.put(org, sortedList);
		}
		
		return sortedOrganisationMap;
	}

	public List<DashboardEntity> getDashboardEntities(ReportObjective objective) {
		List<DashboardEntity> entities = new ArrayList<DashboardEntity>();
		if(objective.getChildren() != null){
			for (ReportObjective child : objective.getChildren()) {
				DashboardEntity dashboardEntity = getDashboardEntity(child);
				if(dashboardEntity != null)	entities.add(dashboardEntity);
			}
		}
		entities.addAll(getReportTargets(DashboardTarget.class, objective));
		return entities;
	}
	
	public DashboardEntity getDashboardEntity(ReportObjective objective) {
		DashboardEntity dashboardEntity = null;
		
		DashboardObjective dashboardObjective = (DashboardObjective) sessionFactory.getCurrentSession().createCriteria(DashboardObjective.class)
				.add(Restrictions.eq("objective", objective)).uniqueResult();
		if(dashboardObjective != null) 
			dashboardEntity = dashboardObjective;
		
//		DashboardTarget dashboardTarget = (DashboardTarget) sessionFactory.getCurrentSession().createCriteria(DashboardTarget.class)
//				.add(Restrictions.eq("objective", objective)).uniqueResult();
//		if(dashboardTarget != null)
//			dashboardEntity = dashboardTarget;
	
		return dashboardEntity;
	}
	
	public List getReportTargets(Class clazz, ReportObjective objective) {
		return sessionFactory.getCurrentSession().createCriteria(clazz)
				.add(Restrictions.eq("objective", objective))
				.list();
	}
}
