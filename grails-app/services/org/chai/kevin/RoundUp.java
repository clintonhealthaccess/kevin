package org.chai.kevin;

import com.ibm.jaql.json.type.JsonDouble;
import com.ibm.jaql.json.type.JsonNumber;

public class RoundUp {

	public JsonDouble eval(JsonNumber decimal) {
		return new JsonDouble(
			Math.ceil(decimal.doubleValue())
		);
	}
	
}
