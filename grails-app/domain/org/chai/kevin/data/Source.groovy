package org.chai.kevin.data;

import i18nfields.I18nFields


@i18nfields.I18nFields
public class Source {

	String code;
	String names;
	String descriptions;
	
	// deprecated
	String jsonDescriptions;
	String jsonNames;
	
	static i18nFields = ['names', 'descriptions']
	
	static mapping = {
		table 'dhsst_data_source'
		cache true
	}
	
	static constraints =  {
		code (nullable: false, blank: false, unique: true)
		names (nullable: true)
		descriptions (nullable: true)
		
		// deprecated
		jsonDescriptions(nullable: true)
		jsonNames(nullable: true)
	}

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
		if (!(obj instanceof Source))
			return false;
		Source other = (Source) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
}
