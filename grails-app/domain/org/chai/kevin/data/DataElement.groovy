package org.chai.kevin.data;

import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map

import javax.persistence.Transient

import org.chai.kevin.Exportable
import org.chai.kevin.data.Type.TypeVisitor
import org.chai.kevin.data.Type.ValueType
import org.chai.kevin.value.DataValue

public abstract class DataElement<T extends DataValue> extends Data<T> implements Exportable {

	Type type;

	static mapping = {
		table 'dhsst_data_element'
		tablePerHierarchy false
	}
	
	static constraints = {
		type (nullable: false,  validator: {val, obj -> 
			return val.isValid();
		})
	}
	
	static embedded = ['type']
	
	@Transient
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
	
	@Transient
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

	@Transient
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
