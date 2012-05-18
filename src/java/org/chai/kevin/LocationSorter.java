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

import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;

/**
 * @author Jean Kahigiso M.
 * 
 */
public class LocationSorter {

	public static final Comparator<DataLocation> BY_DATA_LOCATION_TYPE(final String language) {
		return new Comparator<DataLocation>() {
			public int compare(DataLocation dataLoc1, DataLocation dataLoc2) {			
				if(dataLoc1 == null || dataLoc2 == null) 
					return 0;				
				if (dataLoc1.getType().getNames().get(language).equals(dataLoc2.getType().getNames().get(language)))
					return compareTranslations(dataLoc1.getNames(), dataLoc2.getNames(), language);
				else
					return compareTranslations(dataLoc1.getType().getNames(), dataLoc2.getType().getNames(), language);
			}
		};
	}

	public static final Comparator<Location> BY_LEVEL(final String language) {
		return new Comparator<Location>() {
			public int compare(Location loc1, Location loc2) {			
				if(loc1 == null || loc2 == null) 
					return 0;				
				if (loc1.getLevel().equals(loc2.getLevel()))
					return compareTranslations(loc1.getNames(), loc2.getNames(), language);
				else
					return loc1.getLevel().compareTo(loc2.getLevel());
			}
		};
	}

	public static final Comparator<CalculationLocation> BY_NAME(final String language) {
		return new Comparator<CalculationLocation>() {
			public int compare(CalculationLocation org1, CalculationLocation org2) {
				if(org1 == null || org2 == null) return 0;
				
				return compareTranslations(org1.getNames(), org2.getNames(), language);
			}
		};
	}
	
	private static int compareTranslations(Translation t1, Translation t2, String language) {
		if (t1.get(language) != null && t2.get(language) != null) return t1.get(language).compareTo(t2.get(language));
		return 0;
	}
}
