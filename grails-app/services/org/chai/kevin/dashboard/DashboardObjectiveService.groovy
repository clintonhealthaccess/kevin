package org.chai.kevin.dashboard

import java.util.List;

import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;

class DashboardObjectiveService {

    static transactional = true
	
	DashboardObjective getRootObjective() throws IllegalStateException {
		List<DashboardObjective> objectives = DashboardObjective.findAllByRoot(true);
		if (objectives.size() != 1) {
			throw new IllegalStateException("there is no root objective in the system, please create one")
		}
		return objectives.get(0);
	}
	
	
	List<DashboardTarget> getTargets() {
		return DashboardTarget.list();
	}
	
		
}
