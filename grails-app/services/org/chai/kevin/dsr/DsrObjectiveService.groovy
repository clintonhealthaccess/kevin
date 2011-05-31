package org.chai.kevin.dsr

import java.util.List;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrTarget;

class DsrObjectiveService {

	static transactional = true

	DsrObjective getRootObjective() throws IllegalStateException {
		List<DsrObjective> objectives = DsrObjective.findAllByName(true);
		Collections.sort(objectives, new DsrObjectiveSorter());
		if (objectives.size() != 1) {
			throw new IllegalStateException(
			"there is no root objective in the system, please create one");
		}
		return objectives.get(0);
	}

	List<DsrTarget> getTargets() {
		return DsrTarget.list();
	}
}
