package org.chai.kevin.data;

import java.util.Map;

import javax.persistence.Embeddable;

import org.chai.kevin.Exportable;
import org.chai.kevin.Importable;
import org.chai.kevin.json.JSONMap;

@Embeddable
public class ExpressionMap extends JSONMap<Map<String, String>> implements Exportable, Importable {

	private static final long serialVersionUID = 6260432376582051031L;
	
	@Override
	public String toString() {
		return "ExpressionMap[getJsonMap()='" + getJsonText() + "']";
	}

	@Override
	public String toExportString() {
		return getJsonText();
	}

	@Override
	public ExpressionMap fromExportString(Object value) {
		ExpressionMap expressionMap = new ExpressionMap();		
		expressionMap.setJsonText(value.toString());		
		return expressionMap;
	}
}
