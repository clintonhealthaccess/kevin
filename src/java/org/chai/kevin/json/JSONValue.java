package org.chai.kevin.json;

import java.util.Arrays;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public abstract class JSONValue {

	protected String jsonValue = null;
	protected JSONObject value = null;

	public JSONValue() {jsonValue = "";}
	
	public JSONValue(String jsonValue) {
		this.jsonValue = jsonValue;
		refreshValue();
	}
	
	// use this method with caution, never set directly a JSONObject coming
	// from another Value, as it could cause side effects
	// should be "protected"
	public JSONValue(JSONObject object) {
		this.value = object;
	}
	
	public String getJsonValue() {
		if (jsonValue == null) {
			jsonValue = value.toString();
		}
		return jsonValue;
	}

	public void setJsonValue(String jsonValue) {
		if (this.jsonValue == null || (this.jsonValue != jsonValue && !this.jsonValue.equals(jsonValue))) { 
			this.jsonValue = jsonValue;
			refreshValue();
			clearCache();
		}
	}
	
	protected abstract String[] getReservedKeywords();
	
	protected abstract void clearCache();
	
	void refreshValue() {
		this.value = null;
		
		try {
			value = JSONObject.fromObject(jsonValue);
		} catch (JSONException e) {
			value = new JSONObject();
		}
		this.jsonValue = value.toString();
	}

	public JSONObject getJsonObject() {
		if (value == null) {
			try {
				value = JSONObject.fromObject(jsonValue);
			} catch (JSONException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return value;
	}

	public void setJsonObject(JSONObject object) {
		if (this.value != object) {
			this.jsonValue = null;
			this.value = object;
			clearCache();
		}
	}

	public String getAttribute(String attribute) {
		if (Arrays.binarySearch(getReservedKeywords(), attribute) >= 0) throw new IllegalArgumentException("trying to get a reserved attribute using getAttribute");
		
		if (!getJsonObject().has(attribute)) return null;
		try {
			return value.getString(attribute);
		} catch (JSONException e) {
			return null;
		}
	}

	public void setAttribute(String attribute, String attributeValue) {
		if (Arrays.binarySearch(getReservedKeywords(), attribute) >= 0) throw new IllegalArgumentException("trying to set reserved attribute using getAttribute");
	
		// we get a reference to a JSON object
		JSONObject object = getJsonObject();
		try {
			if (attributeValue == null) object.remove(attribute);
			else object.put(attribute, attributeValue);
			this.jsonValue = null;
		} catch (JSONException e) {
			throw new IllegalArgumentException("could not set attribute", e);
		}
	}

	@Override
	public String toString() {
		return getJsonValue().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getJsonValue() == null) ? 0 : getJsonValue().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof JSONValue))
			return false;
		JSONValue other = (JSONValue) obj;
		if (getJsonValue() == null) {
			if (other.getJsonValue() != null)
				return false;
		} else if (!getJsonValue().equals(other.getJsonValue()))
			return false;
		return true;
	}	
}