package org.chai.kevin;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

public abstract class Info {

	public abstract String getTemplate();
	
	public boolean isNumber() {
		return NumberUtils.isNumber(getValue());
	}
	
	public abstract String getValue();
	
}
