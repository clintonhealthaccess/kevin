package org.chai.kevin.data;

import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;

import org.chai.kevin.Exportable;
import org.chai.kevin.Importable;
import org.chai.kevin.json.JSONMap;

@Embeddable
public class SourceMap extends JSONMap<Map<String, List<String>>> implements Exportable, Importable {

	private static final long serialVersionUID = 6260432376582051031L;
	
	@Override
	public String toString() {
		return "SourceMap[getJsonMap()='" + getJsonText() + "']";
	}

	@Override
	public String toExportString() {
		return getJsonText();
	}

	@Override
	public SourceMap fromExportString(Object value) {
		SourceMap sourceMap = new SourceMap();		
		sourceMap.setJsonText(value.toString());		
		return sourceMap;
	}
}
