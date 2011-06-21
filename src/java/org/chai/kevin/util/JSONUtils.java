package org.chai.kevin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
	
}