package org.chai.kevin.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.apache.commons.lang.NotImplementedException;
import org.chai.kevin.value.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.lang.JaqlQuery;

@Embeddable
public class Type {
	
	public enum ValueType {NUMBER, BOOL, STRING, DATE, ENUM, LIST, MAP}

	private String jsonType = "";
	
	public Type() {}
	
	public Type(String jsonType) {
		this.jsonType = jsonType;
	}
	
	@Lob
	@Column(nullable=false)
	public String getJsonType() {
		return jsonType;
	}
	
	public void setJsonType(String jsonType) {
		this.jsonType = jsonType;
	}
	
	private JSONObject type = null;
	
	@Transient
	public JSONObject getJsonObject() {
		if (type == null) {
			try {
				type = new JSONObject(jsonType);
			} catch (JSONException e) {}
		}
		return type;
	}
	
	@Transient
	public ValueType getType() {
		try {
			return ValueType.valueOf(getJsonObject().getString("type").toUpperCase());
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public Long getEnumId() {
		// TODO think that through
		if (!getType().equals(ValueType.ENUM)) throw new IllegalStateException();
		try {
			return getJsonObject().getLong("enum_id");
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public Type getListType() {
		if (!getType().equals(ValueType.LIST)) throw new IllegalStateException();
		try {
			return new Type(getJsonObject().getString("list_type"));
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Transient
	public Map<String, Type> getElementMap() {
		if (!getType().equals(ValueType.MAP)) throw new IllegalStateException();
		Map<String, Type> result = new HashMap<String, Type>();
		try {
			JSONArray array = getJsonObject().getJSONArray("elements");
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				result.put(object.getString("name"), new Type(object.getString("element_type")));
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	@Transient
	public Value getValueFromMap(Map<String, Object> map, String suffix) {
		try {
			JSONObject object = new JSONObject();
			switch (getType()) {
				case NUMBER:
					object.put("value", sanitizeValue(map.get(suffix)));
					break;
				case BOOL:
					object.put("value", sanitizeValue(map.get(suffix)));
					break;
				case STRING:
					object.put("value", sanitizeValue(map.get(suffix)));
					break;
				case DATE:
					// TODO
					break;
				case ENUM:
					object.put("value", sanitizeValue(map.get(suffix)));
					break;
				case LIST:
					JSONArray array1 = new JSONArray();
					List<String> suffixMap = new ArrayList<String>();
					if (map.get(suffix) instanceof String[]) suffixMap.addAll(Arrays.asList((String[])map.get(suffix)));
					else if (map.get(suffix) instanceof Collection) suffixMap.addAll((Collection<String>)map.get(suffix));
					else if (map.get(suffix) instanceof String) suffixMap.add((String)map.get(suffix));
					
					for (String item : suffixMap) {
						if (!item.equals("[_]")) { 
							array1.put(getListType().getValueFromMap(map, suffix+item).getJsonObject());
						}
					}
					
					object.put("value", array1);
					break;
				case MAP:
					Map<String, Type> elementMap = getElementMap();
					JSONArray array = new JSONArray();
					for (Entry<String, Type> entry : elementMap.entrySet()) {
						JSONObject element = new JSONObject();
						element.put("key", entry.getKey());
						element.put("value", elementMap.get(entry.getKey()).getValueFromMap(map, suffix+"."+entry.getKey()).getJsonObject());
						array.put(element);
					}
					object.put("value", array);
					break;
				default:
					throw new NotImplementedException();
			}
			return new Value(object.toString());
		} catch (JSONException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transient
	public Value getValue(Object value) {
		if (value == null) return Value.NULL;
		try {
			JSONObject object = new JSONObject();
			switch (getType()) {
				case NUMBER:
					object.put("value", (Number)value);
					break;
				case BOOL:
					object.put("value", (Boolean)value);
					break;
				case STRING:
					object.put("value", (String)value);
					break;
				case DATE:
					object.put("value", ((Date)value).getTime());
					break;
				case ENUM:
					object.put("value", (String)value);
					break;
				case LIST:
					JSONArray array1 = new JSONArray();
					for (Object item : (List<?>)value) {
						array1.put(getListType().getValue(item).getJsonObject());
					}
					object.put("value", array1);
					break;
				case MAP:
					Map<String, Type> elementMap = getElementMap();
					JSONArray array = new JSONArray();
					for (Entry<String, Object> entry : ((Map<String, Object>)value).entrySet()) {
						JSONObject element = new JSONObject();
						element.put("key", entry.getKey());
						element.put("value", elementMap.get(entry.getKey()).getValue(entry.getValue()).getJsonObject());
						array.put(element);
					}
					object.put("value", array);
					break;
				default:
					throw new NotImplementedException();
			}
			return new Value(object.toString());
		} catch (JSONException e) {
			return null;
		}
	}
	
	public Value getValueFromJaql(String jaqlString) {
		if (jaqlString.equals("null")) return Value.NULL;
		try {
			JSONObject object = new JSONObject();
			switch (getType()) {
				case NUMBER:
					object.put("value", Double.parseDouble(jaqlString));
					break;
				case BOOL:
					object.put("value", Boolean.parseBoolean(jaqlString));
					break;
				case STRING:
					object.put("value", jaqlString);
					break;
				case DATE:
					// TODO
					break;
				case ENUM:
					object.put("value", jaqlString);
					break;
				case LIST:
					List<String> values = new ArrayList<String>();
					JSONArray array = new JSONArray(jaqlString);
					for (int i = 0; i < array.length(); i++) {
						String itemJaqlString = array.getString(i);
						values.add(getListType().getValueFromJaql(itemJaqlString).toString());
					}
					object.put("value", values);
					break;
				case MAP:
					JSONObject jaqlObject = new JSONObject(jaqlString);
					
					Map<String, Type> elementMap = getElementMap();
					JSONArray array1 = new JSONArray();
					for (Entry<String, Type> entry : elementMap.entrySet()) {
						JSONObject element = new JSONObject();
						element.put("key", entry.getKey());
						element.put("value", entry.getValue().getValueFromJaql(jaqlObject.getString(entry.getKey())).toString());
						array1.put(element);
					}
					object.put("value", array1);
					break;
				default:
					throw new NotImplementedException();
			}
			return new Value(object.toString());
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getJaqlValue(Value value) {
		StringBuilder result = new StringBuilder();
		if (value.isNull()) result.append("null");
		else {
			switch (getType()) {
				case NUMBER:
					result.append(value.getNumberValue().toString());
					break;
				case BOOL:
					result.append(value.getBooleanValue().toString());
					break;
				case STRING:
					result.append("\""+value.getStringValue()+"\"");
					break;
				case DATE:
					// TODO
					result = null;
					break;
				case ENUM:
					result.append("\""+value.getEnumValue()+"\"");
					break;
				case LIST:
					result.append("[");
					for (Value item : value.getListValue()) {
						result.append(getListType().getJaqlValue(item));
						result.append(',');
					}
					result.append("]");
					break;
				case MAP:
					result.append("{");
					for (Entry<String, Value> entry : value.getMapValue().entrySet()) {
						result.append("\""+entry.getKey()+"\"");
						result.append(":");
						result.append(getElementMap().get(entry.getKey()).getJaqlValue(entry.getValue()));
						result.append(",");
					}
					result.append("}");
					break;
				default:
					throw new NotImplementedException();
			}
		}
		return result.toString();
	}
	
	public void setAttribute(Value value, String prefix, String attribute, String text) {
		Value prefixedValue = getValue(value, prefix);
		prefixedValue.setAttribute(attribute, text);
		setValue(value, prefix, prefixedValue);
	}
	
	public String getAttribute(Value value, String prefix, String attribute) {
		return getValue(value, prefix).getAttribute(attribute);
	}
	
	public Value getValue(Value value, String prefix) {
		return getValue(prefix, value, "");
	}
	
	private Value getValue(String prefix, Value currentValue, String currentPrefix) {
		if (prefix.equals(currentPrefix)) return currentValue;
		else {
			switch (getType()) {
				case LIST:
					List<Value> values = currentValue.getListValue();
					Type listType = getListType();
					for (int i = 0; i < values.size(); i++) {
						Value value = listType.getValue(prefix, values.get(i), currentPrefix+"["+i+"]");
						if (value != null) return value;
					}
					break;
				case MAP:
					Map<String, Type> typeMap = getElementMap();
					for (Entry<String, Value> entry : currentValue.getMapValue().entrySet()) {
						Value value = typeMap.get(entry.getKey()).getValue(prefix, entry.getValue(), currentPrefix+"."+entry.getKey());
						if (value != null) return value;
					}
					break;
				default:
					break;
			}
		}
		return null;
	}
	
	public void setValue(Value value, String prefix, Value toSet) {
		value.setJsonValue(setValue(prefix, value, "", toSet).toString());
	}
	
	private Value setValue(String prefix, Value currentValue, String currentPrefix, Value toSet) {
		if (prefix.equals(currentPrefix)) return toSet;
		else {
			try {
				JSONObject object = new JSONObject();
				switch (getType()) {
					case LIST:
						List<Value> values = currentValue.getListValue();
						JSONArray array1 = new JSONArray();
						Type listType = getListType();
						for (int i = 0; i < values.size(); i++) {
							array1.put(listType.setValue(prefix, values.get(i), currentPrefix+"["+i+"]", toSet).getJsonObject());
						}
						object.put("value", array1);
						return new Value(object.toString());
					case MAP:
						JSONArray array2 = new JSONArray();
						Map<String, Type> typeMap = getElementMap();
						for (Entry<String, Value> entry : currentValue.getMapValue().entrySet()) {
							JSONObject element = new JSONObject();
							element.put("key", entry.getKey());
							element.put("value", typeMap.get(entry.getKey()).setValue(prefix, entry.getValue(), currentPrefix+"."+entry.getKey(), toSet).getJsonObject());
							array2.put(element);
						}
						object.put("value", array2);
						return new Value(object.toString());
					default:
						return currentValue;
				}
			}
			catch(JSONException e) {
				return null;
			}
		}
	}
	
	public void getCombinations(Value value, List<String> strings, Set<List<String>> combinations, String prefix) {
		switch (getType()) {
			case NUMBER:
			case BOOL:
			case STRING:
			case DATE:
			case ENUM:
				combinations.add(strings);
				break;
			case LIST:
				List<Value> values = value.getListValue();
				Type listType = getListType();
				for (int i = 0; i < values.size(); i++) {
					combinations.add(replace(strings, prefix+"[_]", prefix+"["+i+"]"));
					listType.getCombinations(values.get(i), strings, combinations, prefix+"["+i+"]");
					listType.getCombinations(values.get(i), strings, combinations, prefix+"[]");
				}
				break;
			case MAP:
				Map<String, Type> typeMap = getElementMap();
				for (Entry<String, Value> entry : value.getMapValue().entrySet()) {
					typeMap.get(entry.getKey()).getCombinations(entry.getValue(), strings, combinations, prefix+"."+entry.getKey());
				}
				break;
			default:
				throw new NotImplementedException();
		}
	}

	public void getPrefixes(Value value, String prefix, Map<String, Value> prefixes, PrefixPredicate predicate) {
		if (predicate.holds(this, value, prefix)) prefixes.put(prefix, value);
		switch (getType()) {
			case NUMBER:
			case BOOL:
			case STRING:
			case DATE:
			case ENUM:
				break;
			case LIST:
				Type listType = getListType();
				List<Value> values = value.getListValue();
				for (int i = 0; i < values.size(); i++) {
					listType.getPrefixes(values.get(i), prefix+"["+i+"]", prefixes, predicate);
				}
				break;
			case MAP:
				Map<String, Type> typeMap = getElementMap();
				for (Entry<String, Value> entry : value.getMapValue().entrySet()) {
					typeMap.get(entry.getKey()).getPrefixes(entry.getValue(), prefix+"."+entry.getKey(), prefixes, predicate);
				}
				break;
			default:
				throw new NotImplementedException();
		}
	}

	public static interface PrefixPredicate {
		public boolean holds(Type type, Value value, String prefix);
	}
	
	public String getDisplayValue(Value value) {
		// TODO implement this
		return getJaqlValue(value);
	}
	
	private Object sanitizeValue(Object value) {
		Object result = null;
		String string = String.valueOf(value);
		switch (getType()) {
			case NUMBER:
				try {
					result = Double.parseDouble(string);
				} catch (NumberFormatException e) {
					result = JSONObject.NULL;
				}
				break;
			case BOOL:
				if (value != null && !string.equals("0")) result = true;
				else result = false;
				break;
			case STRING:
				if (value == null || string.equals("")) result = JSONObject.NULL;
				else result = string;
				break;
			case DATE:
				// TODO
				result = string;
				break;
			case ENUM:
				if (value == null || string.equals("null")) result = JSONObject.NULL;
				else result = string; 
				break;
			default:
				if (value == null || string.equals("null")) result = JSONObject.NULL;
				else result = string;
		}
		return result;
	}
	
	private List<String> replace(List<String> strings, String toReplace, String replaceWith) {
		List<String> result = new ArrayList<String>();
		for (String string : strings) {
			result.add(string.replace(toReplace, replaceWith));
		}
		return result;
	}
		
	@Override
	public String toString() {
		return jsonType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getJsonObject() == null) ? 0 : getJsonObject().toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Type))
			return false;
		Type other = (Type) obj;
		if (getJsonObject() == null) {
			if (other.getJsonObject() != null)
				return false;
		} else if (!getJsonObject().toString().equals(other.getJsonObject().toString()))
			return false;
		return true;
	}

}
