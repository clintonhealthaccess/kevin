package org.chai.kevin.dsr

import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

import org.chai.kevin.cost.CostTarget;

class DsrTargetService {
	
	static transactional = true
	
	List<DsrTarget> getTargets() {
		return DsrTarget.list();
	}
		
}
