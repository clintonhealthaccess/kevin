package org.chai.kevin.data;

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

import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map

import org.chai.kevin.Exportable
import org.chai.kevin.data.Type.TypeVisitor
import org.chai.kevin.data.Type.ValueType
import org.chai.kevin.value.DataValue

public abstract class DataElement<T extends DataValue> extends Data<T> implements Exportable {

	String typeString
	
	static mapping = {
		table 'dhsst_data_element'
		tablePerHierarchy false
	}
	
	static constraints = {
		type (nullable: false,  validator: {val, obj -> 
			return val.isValid();
		})
	}
	
	Type cachedType
	static transients = ['cachedType', 'type']
	
	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	Type getType() {
		if (typeString != null && cachedType == null) this.cachedType = new Type(typeString)
		return cachedType
	}
	
	void setType(Type type) {
		this.cachedType = type
		this.typeString = type.jsonValue
	}
	
	void setTypeString(String typeString) {
		this.typeString = typeString
		this.cachedType = null
	}
	
	public List<String> getHeaderPrefixes() {
		final List<String> prefixes = new ArrayList<String>();
		
		getType().visit(new TypeVisitor() {
			@Override
			public void handle(Type type, String prefix) {
				if (getParent() != null && getParent().getType() == ValueType.MAP) {
					prefixes.add(prefix);
				}
			}
		});
	
		return prefixes;
	}
	
	public List<String> getValuePrefixes(String section) {
		final Type sectionType = getType().getType(section);
		final List<String> result = new ArrayList<String>();
		getType().visit(new TypeVisitor() {
			@Override
			public void handle(Type type, String prefix) {
				if (!type.isComplexType() && getParents().contains(sectionType)) {
					result.add(prefix);
				}
			}
		});
		return result;
	}

	public Map<String, Type> getEnumPrefixes() {
		final Map<String, Type> result = new HashMap<String, Type>();
		getType().visit(new TypeVisitor() {
			@Override
			public void handle(Type type, String prefix) {
				if (type.getType() == ValueType.ENUM) result.put(prefix, type);
			}
		});
		return result;
	}

}
