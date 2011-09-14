package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Embeddable
public class Value {

	public static final Value NULL = new Value("{value: null}");
	
	private String jsonValue = "";
	
	public Value() {}
	
	public Value(String jsonValue) {
		this.jsonValue = jsonValue;
	}
	
	@Lob
	@Column(nullable=false)
	public String getJsonValue() {
		return jsonValue;
	}
	
	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}
	
	private JSONObject value = null;
	
	@Transient
	public JSONObject getJsonObject() {
		if (value == null) {
			try {
				value = new JSONObject(jsonValue);
			} catch (JSONException e) {
				value = new JSONObject();
			}
		}
		return value;
	}
	
	@Transient
	public boolean isNull() {
		return getJsonObject().toString().equals(NULL.getJsonObject().toString());
	}
	
	@Transient
	public String getAttribute(String attribute) {
		if (attribute.equals("value")) throw new IllegalArgumentException("trying to get value attribute using getAttribute");
		
		try {
			return getJsonObject().getString(attribute);
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public void setAttribute(String attribute, String value) {
		if (attribute.equals("value")) throw new IllegalArgumentException("trying to set value attribute using getAttribute");

		JSONObject object = getJsonObject();
		try {
			object.put(attribute, value);
			this.jsonValue = object.toString();
			this.value = null;
		} catch (JSONException e) {
			throw new IllegalArgumentException("could not set attribute", e);
		}
	}
	
	@Transient
	public Number getNumberValue() {
		try {
			return getJsonObject().getDouble("value");
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public String getStringValue() {
		try {
			if (getJsonObject().isNull("value")) return null;
			return getJsonObject().getString("value");
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public Boolean getBooleanValue() {
		try {
			return getJsonObject().getBoolean("value");
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public String getEnumValue() {
		// TODO think that through
		try {
			return getJsonObject().getString("value");
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public Date getDateValue() {
		try {
			return new Date(getJsonObject().getLong("value"));
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public List<Value> getListValue() {
		try {
			List<Value> result = new ArrayList<Value>();
			JSONArray array = getJsonObject().getJSONArray("value");
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				result.add(new Value(object.toString()));
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public Map<String, Value> getMapValue() {
		try {
			Map<String, Value> result = new HashMap<String, Value>();
			JSONArray array = getJsonObject().getJSONArray("value");
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.optJSONObject(i);
				try {
					result.put(object.getString("key"), new Value(object.getString("value")));
				} catch (JSONException e) {}
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return jsonValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getJsonObject() == null) ? 0 : getJsonObject().toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Value))
			return false;
		Value other = (Value) obj;
		if (getJsonObject() == null) {
			if (other.getJsonObject() != null)
				return false;
		} else if (!getJsonObject().toString().equals(other.getJsonObject().toString()))
			return false;
		return true;
	}
	
}
