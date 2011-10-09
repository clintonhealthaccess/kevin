package org.chai.kevin.value;

import grails.converters.deep.JSON;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.chai.kevin.util.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Embeddable
public class Value {
	
	public static final String MAP_KEY = "map_key";
	public static final String MAP_VALUE = "map_value";
	public static final String VALUE_STRING = "value";

	public static final Value NULL = new Value("{value: null}");
	
	private String jsonValue = null;
	
	public Value() {}
	
	public Value(String jsonValue) {
		this.jsonValue = jsonValue;
		refreshValue();
	}
	
	// use this method with caution, never set directly a JSONObject coming
	// from another Value, as it could cause side effects
	// should be "protected"
	public Value(JSONObject object) {
		this.value = object;
	}
	
	@Lob
	@Column(nullable=false)
	public String getJsonValue() {
		if (jsonValue == null) {
			jsonValue = value.toString();
		}
		return jsonValue;
	}
	
	// this method is perfectly safe to use since
	// it constructs a new JSONObject from the given value
	public void setJsonValue(String jsonValue) {
		if (this.jsonValue == null || (this.jsonValue != jsonValue && !this.jsonValue.equals(jsonValue))) { 
			this.jsonValue = jsonValue;
			refreshValue();
			clearCache();
		}
	}
	
	private JSONObject value = null;
	
	private List<Value> listValue = null;
	private Map<String, Value> mapValue = null;
	private String stringValue = null;
	private String enumValue = null;
	private Number numberValue = null;
	private Boolean booleanValue = null;
	private Date dateValue = null;
	
	@Transient
	public JSONObject getJsonObject() {
		if (value == null) {
			try {
				value = new JSONObject(jsonValue);
			} catch (JSONException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return value;
	}

	// use this method with caution, never set directly a JSONObject coming
	// from another Value, as it could cause side effects
	// should be "protected"
	public void setJsonObject(JSONObject object) {
		if (this.value != object && !this.value.equals(object)) {
			this.jsonValue = null;
			this.value = object;
			clearCache();
		}
	}
	
	private void clearCache() {
		this.listValue = null;
		this.mapValue = null;
		this.numberValue = null;
		this.stringValue = null;
		this.enumValue = null;
		this.booleanValue = null;
		this.dateValue = null;
	}
	
	private void refreshValue() {
		this.value = null;
		
		try {
			value = new JSONObject(jsonValue);
		} catch (JSONException e) {
			value = new JSONObject();
		}
		this.jsonValue = value.toString();
	}
	
	@Transient
	public boolean isNull() {
		return getJsonObject().isNull(VALUE_STRING);
	}
	
	@Transient
	public Value getValueWithoutAttributes() {
		if (getJsonObject().isNull(VALUE_STRING)) return Value.NULL;
		else {
			JSONObject object = new JSONObject();
			try {
				object.put(VALUE_STRING, value.get(VALUE_STRING));
			} catch (JSONException e) {
				return null;
			}
			return new Value(object);
		}
	}
	
	@Transient
	public String getAttribute(String attribute) {
		if (attribute.equals(VALUE_STRING)) throw new IllegalArgumentException("trying to get "+VALUE_STRING+" attribute using getAttribute");
		
		if (!getJsonObject().has(attribute)) return null;
		try {
			return value.getString(attribute);
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public void setAttribute(String attribute, String attributeValue) {
		if (attribute.equals(VALUE_STRING)) throw new IllegalArgumentException("trying to set "+VALUE_STRING+" attribute using getAttribute");

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
	
	@Transient
	public Number getNumberValue() {
		if (numberValue == null) {
			try {
				numberValue = getJsonObject().getDouble(VALUE_STRING);
			} catch (JSONException e) {
				numberValue = null;
			}
		}
		return numberValue;
	}
	
	@Transient
	public String getStringValue() {
		if (stringValue == null) {
			try {
				if (getJsonObject().isNull(VALUE_STRING)) stringValue = null;
				else stringValue = getJsonObject().getString(VALUE_STRING);
			} catch (JSONException e) {
				stringValue = null;
			}
		}
		return stringValue;
	}
	
	@Transient
	public Boolean getBooleanValue() {
		if (booleanValue == null) {
			try {
				booleanValue = getJsonObject().getBoolean(VALUE_STRING);
			} catch (JSONException e) {
				booleanValue = null;
			}
		}
		return booleanValue;
	}
	
	@Transient
	public String getEnumValue() {
		// TODO think that through
		if (enumValue == null) {
			try {
				enumValue = getJsonObject().getString(VALUE_STRING);
			} catch (JSONException e) {
				enumValue = null;
			}
		}
		return enumValue;
	}
	
	@Transient
	public Date getDateValue() {
		if (dateValue == null) {
			try {
				dateValue = Utils.parseDate(getJsonObject().getString(VALUE_STRING));
			} catch (JSONException e) {
				dateValue = null;
			} catch (ParseException e) {
				dateValue = null;
			}
		}
		return dateValue;
	}
	
	@Transient
	public List<Value> getListValue() {
		if (listValue == null) {
			try {
				List<Value> result = new ArrayList<Value>();
				JSONArray array = getJsonObject().getJSONArray(VALUE_STRING);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					result.add(new Value(object));
				}
				listValue = result;
			} catch (JSONException e) {
				listValue = null;
			}
		}
		return listValue;
	}
	
	@Transient
	public Map<String, Value> getMapValue() {
		if (mapValue == null) {
			try {
				Map<String, Value> result = new LinkedHashMap<String, Value>();
				JSONArray array = getJsonObject().getJSONArray(VALUE_STRING);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.optJSONObject(i);
					try {
						result.put(object.getString(MAP_KEY), new Value(object.getJSONObject(MAP_VALUE)));
					} catch (JSONException e) {}
				}
				mapValue = result;
			} catch (JSONException e) {
				mapValue = null;
			}
		}
		return mapValue;
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
		if (!(obj instanceof Value))
			return false;
		Value other = (Value) obj;
		if (getJsonValue() == null) {
			if (other.getJsonValue() != null)
				return false;
		} else if (!getJsonValue().equals(other.getJsonValue()))
			return false;
		return true;
	}
	
}
