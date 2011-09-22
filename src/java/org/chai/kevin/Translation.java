package org.chai.kevin;

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


import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.chai.kevin.util.JSONUtils;

@Embeddable
public class Translation implements Map<String, String>, Serializable {
	
	private static final long serialVersionUID = 659167226523919292L;
	private String jsonText = " ";
	
	@Lob
	@Column(nullable=false)
	public String getJsonText() {
		return jsonText;
	}
	
	public void setJsonText(String jsonText) {
		this.jsonText = jsonText;
		if (this.jsonText.isEmpty()) this.jsonText = " ";
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
		setJsonText(" ");
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

	@Override
	public String toString() {
		return "Translation [jsonText=" + jsonText + "]";
	}
	
	
}
