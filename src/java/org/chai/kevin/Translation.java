package org.chai.kevin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Embeddable
public class Translation implements Map<String, String> {
	
	private String jsonText = "";
	
	@Lob
	@Column(nullable=false)
	public String getJsonText() {
		return jsonText;
	}
	
	public void setJsonText(String jsonText) {
		this.jsonText = jsonText;
		reloadMap();
	}
	
	
	private Map<String, String> embeddedMap = new HashMap<String, String>();
	
	private void reloadMap() {
		embeddedMap.clear();
		embeddedMap.putAll(JSONUtils.getMapFromJSON(getJsonText()));
	}
	
	/*
	 * The methods below MODIFY the map
	 */
	@Override
	@Transient
	public void clear() {
		setJsonText("");
	}
	
	@Override
	@Transient
	public String put(String key, String value) {
		String result = embeddedMap.put(key, value);
		setJsonText(JSONUtils.getJSONFromMap(embeddedMap));
		return result;
	}

	@Override
	@Transient
	public void putAll(Map<? extends String, ? extends String> m) {
		embeddedMap.putAll(m);
		setJsonText(JSONUtils.getJSONFromMap(embeddedMap));
	}

	@Override
	@Transient
	public String remove(Object key) {
		String value = embeddedMap.remove(key);
		setJsonText(JSONUtils.getJSONFromMap(embeddedMap));
		return value;
	}

	/*
	 * The methods below are READ-ONLY
	 */
	@Override
	@Transient
	public boolean containsKey(Object key) {
		return embeddedMap.containsKey(key);
	}

	@Override
	@Transient
	public boolean containsValue(Object value) {
		return embeddedMap.containsValue(value);
	}

	@Override
	@Transient
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return embeddedMap.entrySet();
	}

	@Override
	@Transient
	public String get(Object key) {
		return embeddedMap.get(key);
	}

	@Override
	@Transient
	public boolean isEmpty() {
		return embeddedMap.isEmpty();
	}

	@Override
	@Transient
	public Set<String> keySet() {
		return embeddedMap.keySet();
	}

	@Override
	@Transient
	public int size() {
		return embeddedMap.size();
	}

	@Override
	@Transient
	public Collection<String> values() {
		return embeddedMap.values();
	}
	
	
}
