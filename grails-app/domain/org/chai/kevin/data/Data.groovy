package org.chai.kevin.data;

import java.util.Date
import java.util.Set

import javax.persistence.Transient

import org.chai.kevin.Period
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.value.DataValue

@i18nfields.I18nFields
abstract class Data<T extends DataValue> {
	
	// deprecated
	Long id
	
	Date timestamp = new Date()
	Date lastValueChanged = new Date()
	
	String code
	String names
	String descriptions
	
	// deprecated
	String jsonDescriptions;
	String jsonNames;
	
	static i18nFields = ['names', 'descriptions']
	
	static mapping = {
		table 'dhsst_data'
		tablePerHierarchy false
		code index: 'Code_Index'
		timestamp sqlType: "datetime"
		lastValueChanged sqlType: "datetime"
		cache true
	}
	
	static constraints =  {
		code (nullable: false, blank: false, unique: true)
		names (nullable: true)
		descriptions (nullable: true)
		
		lastValueChanged(nullable: true)
		
		// deprecated
		jsonDescriptions(nullable: true)
		jsonNames(nullable: true)
	}
	
	@Transient
	public abstract Type getType();
	
	@Transient
	public abstract Class<T> getValueClass();	
	
	@Transient
	public abstract Set<String> getSources(Period period, DataLocationType type);
	
	@Transient
	public abstract Set<String> getSources();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Data))
			return false;
		Data<?> other = (Data<?>) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
