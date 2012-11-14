package org.chai.kevin.data;

import javax.persistence.Embeddable;

import org.chai.kevin.json.JSONMap;

@Embeddable
public class ModeMap extends JSONMap<Integer> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "ModeMap[getJsonMap()='" + getJsonText() + "']";
	}

}
