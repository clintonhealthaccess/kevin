package org.chai.kevin.planning;

public class PlanningUtils {

	public static String getPrefix(String prefix, Integer lineNumber) {
		return prefix.replaceFirst("^\\[_\\]", "["+lineNumber+"]");
	}

}
