package org.chai.kevin.dsr

import java.util.List;
class DsrTargetService {
	
	static transactional = true
	
	List<DsrTarget> getTargets() {
		return DsrTarget.list();
	}
		
}
