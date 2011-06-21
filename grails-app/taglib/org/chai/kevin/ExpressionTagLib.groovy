package org.chai.kevin;

import java.util.HashMap;
import java.util.Map;

public class ExpressionTagLib {

	def expressionService
	
	def expression = {attrs, body ->
		def expression = attrs['expression'];
		def dataElements = expressionService.getDataElementsInExpression(expression.expression);
		Map<Long, String> replacement = new HashMap<Long, String>();
		for (DataElement dataElement : dataElements) {
			replacement.put(dataElement.getId(), 
				"<span data-id=\""+dataElement.getId()+"\" class=\"element\"><a href=\"#\" class=\"no-link cluetip\" onclick=\"return false;\" rel=\""+g.createLink(controller:'expression', action:'getDataElementDescription', params:[dataElement: dataElement.id])+"\">["+dataElement.getId()+"]</a></span>"
			);
		}
		out << expressionService.convertStringExpression(expression.expression, replacement);
	}
	
	
}
