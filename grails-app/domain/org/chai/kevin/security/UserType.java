package org.chai.kevin.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum UserType {
	SURVEY("editSurvey:view","editSurvey:*:<id>","menu:survey"), 
	PLANNING("editPlanning:view","editPlanning:*:<id>","menu:planning"), 
	OTHER("home:*");
	
	protected Set<String> defaultPermissions;
	
	private UserType(String... defaultPermissions) {
		this.defaultPermissions = new HashSet<String>(Arrays.asList(defaultPermissions));
	}
	
	String getKey() { return name(); }
	
	public static Set<String> getAllPermissions() {
		Set<String> result = new HashSet<String>();
		for (UserType userType : UserType.values()) {
			result.addAll(userType.defaultPermissions);
		}
		return result;
	}
}
