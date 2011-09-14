package org.chai.kevin.util;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;
import org.codehaus.groovy.grails.web.json.JSONException;
import org.codehaus.groovy.grails.web.json.JSONObject;

public class JSONUtils {

	public static String getJSONFromMap(Map<String, String> map) {
		String result = null;
		if (map != null) {
			try {
				JSONObject jsonObject = new JSONObject();
				for (Entry<String, String> entry : map.entrySet()) {
					jsonObject.put(entry.getKey(), entry.getValue());
				}
				result = jsonObject.toString();
			} catch (JSONException e) {
				// log
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getMapFromJSON(String jsonString) {
		Map<String, String> descriptions = new HashMap<String, String>();
		if (jsonString != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				Iterator<String> keyIterator = jsonObject.keys();
				while (keyIterator.hasNext()) {
					String type = (String) keyIterator.next();
					descriptions.put(type, jsonObject.getString(type));
				}
			} catch (JSONException e) {
				// log
			}
		}
		return descriptions;
	}
	
	
	public static Type TYPE_BOOL = new Type("{\"type\":\"bool\"}");
	public static Type TYPE_NUMBER = new Type("{\"type\":\"number\"}");
	public static Type TYPE_STRING = new Type("{\"type\":\"string\"}");
	public static Type TYPE_DATE = new Type("{\"type\":\"date\"}");
	public static Type TYPE_ENUM (Long enumId) {
		return new Type("{\"type\":\"enum\", \"enum_id\":"+enumId+"}");
	}
	public static Type TYPE_LIST (Type listType) {
		return new Type("{\"type\":\"list\", \"list_type\":"+listType.toString()+"}");
	}
	public static Type TYPE_MAP (Map<String, Type> map) {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, Type> entry : map.entrySet()) {
			builder.append("{\"name\":\""+entry.getKey()+"\", \"element_type\":"+entry.getValue().toString()+"}");
			builder.append(',');
		}
		return new Type("{\"type\":\"map\", \"elements\":["+builder.toString()+"]}");
	}
	
}