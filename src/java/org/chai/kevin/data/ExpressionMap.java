package org.chai.kevin.data;

import java.util.Map;

import javax.persistence.Embeddable;

import org.chai.kevin.json.JSONMap;

@Embeddable
public class ExpressionMap extends JSONMap<Map<String, String>> {

	private static final long serialVersionUID = 6260432376582051031L;
	
	@Override
	public String toString() {
		return "ExpressionMap[getJsonMap()='" + getJsonText() + "']";
	}

}
