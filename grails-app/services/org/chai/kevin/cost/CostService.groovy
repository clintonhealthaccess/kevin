package org.chai.kevin.cost

import org.apache.commons.lang.StringUtils;

class CostService {

	static transactional = true
	
	List<CostTarget> getTargets() {
		return CostTarget.list();
	}
	
	static Set<String> getGroupUuids(String groupUuidString) {
		return groupUuidString!=null?new HashSet<String>(Arrays.asList(StringUtils.split(groupUuidString, ','))):[]
	}
	
	static String getGroupUuidString(def groupUuids) {
		if (groupUuids == null) return "";
		if (groupUuids instanceof String) return groupUuids;
		else return StringUtils.join(groupUuids, ',');
	}

	List<Integer> getYears() {
		return [1,2,3,4,5];
	}
		
}

