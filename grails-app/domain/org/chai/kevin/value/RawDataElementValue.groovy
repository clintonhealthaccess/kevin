package org.chai.kevin.value;

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

import org.chai.kevin.Period;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.RawDataElement;
import org.chai.location.DataLocation;

//@Table(name="dhsst_value_raw_data_element",
//		uniqueConstraints=@UniqueConstraint(columnNames={"data", "period", "location"})
//)
class RawDataElementValue extends StoredValue {
	
	// TODO this should be of type RawDataElement
	Data data;

	static mapping = {
		table 'dhsst_value_raw_data_element'
	}

	static constraints = {
		// TODO this create an UNIQUE index in the database that should not be theres 
//		data (nullable: false, unique: ['data', 'period', 'location'])
		data (nullable: false)
	}
	
	public RawDataElementValue(RawDataElement data, DataLocation dataLocation, Period period, Value value) {
		super(dataLocation, period, value);
		
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "RawDataElementValue [value=" + value + "]";
	}

}