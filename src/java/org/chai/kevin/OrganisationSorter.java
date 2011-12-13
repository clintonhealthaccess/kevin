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
package org.chai.kevin;

import java.util.Comparator;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.LocationEntity;

/**
 * @author Jean Kahigiso M.
 * 
 */
public class OrganisationSorter {

	public static final Comparator<DataEntity> BY_FACILITY_TYPE(final String language) {
		return new Comparator<DataEntity>() {
			public int compare(DataEntity org1, DataEntity org2) {			
				if(org1 == null || org2 == null) return 0;
				
				if (org1.getType().getNames().get(language).equals(org2.getType().getNames().get(language)))
					return org1.getNames().get(language).compareTo(org2.getNames().get(language));
				else
					return org1.getType().getNames().get(language).compareTo(org2.getType().getNames().get(language));
			}
		};
	}

	public static final Comparator<LocationEntity> BY_LEVEL(final String language) {
		return new Comparator<LocationEntity>() {
			public int compare(LocationEntity org1, LocationEntity org2) {			
				if(org1 == null || org2 == null) return 0;
				
				if (org1.getLevel().equals(org2.getLevel()))
					return org1.getNames().get(language).compareTo(org2.getNames().get(language));
				else
					return org1.getLevel().compareTo(org2.getLevel());
			}
		};
	}

	public static final Comparator<CalculationEntity> BY_NAME(final String language) {
		return new Comparator<CalculationEntity>() {
			public int compare(CalculationEntity org1, CalculationEntity org2) {
				if(org1 == null || org2 == null) return 0;
				
				return org1.getNames().get(language).compareTo(org2.getNames().get(language));
			}
		};
	}
}
