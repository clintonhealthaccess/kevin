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
	
	private String jsonValue = "";
	
	public Value() {}
	
	public Value(String jsonValue) {
		this.jsonValue = jsonValue;
		refreshValue();
	}
	
	public Value(JSONObject object) {
		this.jsonValue = object.toString();
		this.value = object;
	}
	
	@Lob
	@Column(nullable=false)
	public String getJsonValue() {
		return jsonValue;
	}
	
	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
		refreshValue();
		clearCache();
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
		return value;
	}
	
	public void setJsonObject(JSONObject object) {
		this.jsonValue = object.toString();
		this.value = object;
		clearCache();
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
		return value.isNull(VALUE_STRING);
	}
	
	@Transient
	public Value getValueWithoutAttributes() {
		if (value.isNull(VALUE_STRING)) return Value.NULL;
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
		
		if (!value.has(attribute)) return null;
		try {
			return value.getString(attribute);
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public void setAttribute(String attribute, String attributeValue) {
		if (attribute.equals(VALUE_STRING)) throw new IllegalArgumentException("trying to set "+VALUE_STRING+" attribute using getAttribute");

		JSONObject object = getJsonObject();
		try {
			if (attributeValue == null) object.remove(attribute);
			else object.put(attribute, attributeValue);
			this.jsonValue = object.toString();
			refreshValue();
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
					result.add(new Value(object.toString()));
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
						result.put(object.getString(MAP_KEY), new Value(object.getString(MAP_VALUE)));
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
		return jsonValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jsonValue == null) ? 0 : jsonValue.hashCode());
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
		if (jsonValue == null) {
			if (other.jsonValue != null)
				return false;
		} else if (!jsonValue.equals(other.jsonValue.toString()))
			return false;
		return true;
	}
	
}
