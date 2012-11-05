package org.chai.kevin.exports;

/**
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
import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

import java.util.Date
import java.util.Set

import org.chai.kevin.Period
import org.chai.kevin.util.Utils
import org.chai.location.CalculationLocation

/**
 * @author Jean Kahigiso M.
 *
 */
@I18nFields
@EqualsAndHashCode(includes='code')
abstract class DataExport {
	
	String code
	
	Date date;
	String typeCodeString;
	
	String descriptions
	
	// deprecated
	String jsonDescriptions
		
	static i18nFields = ['descriptions']
	
	static hasMany = [
		periods: Period,
		locations: CalculationLocation
	]
	
	static mapping = {
		table 'dhsst_export'
		tablePerHierarchy false
		
		periods joinTable: [
			name: 'dhsst_export_periods',
			key: 'exporter',
			column: 'periods'
		]
		locations joinTable: [
			name: 'dhsst_export_locations',
			key: 'exporter',
			column: 'locations'
		]
	}
	
	static constraints = {
		code (nullable: false, unique: true, blank: false)
		periods (nullable:false, minSize: 1)
		locations (nullable:false, minSize: 1)
		typeCodeString(nullable:false,blank:false)
		
		descriptions (nullable: true)
		
		jsonDescriptions (nullable: true)
	}
	
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, Utils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, Utils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	List<Period> getAllPeriods() {
		return new ArrayList(periods?:[])
	}
	
	List<CalculationLocation> getAllLocations() {
		return new ArrayList(locations?:[])
	}
	
}
