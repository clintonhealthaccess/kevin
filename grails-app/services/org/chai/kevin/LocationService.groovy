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

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Set

import org.apache.commons.lang.StringUtils
import org.chai.kevin.location.CalculationLocation
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.DataLocation
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.util.Utils
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

public class LocationService {
	
	static transactional = true
	
	def languageService;
	def sessionFactory;	
	
    public Location getRootLocation() {
    	return (Location)sessionFactory.getCurrentSession().createCriteria(Location.class).add(Restrictions.isNull("parent")).uniqueResult();
    }

    public DataLocationType findDataLocationTypeByCode(String code) {
    	return (DataLocationType)sessionFactory.getCurrentSession().createCriteria(DataLocationType.class).add(Restrictions.eq("code", code)).uniqueResult();
    }
	
    public LocationLevel findLocationLevelByCode(String code) {
    	return (LocationLevel)sessionFactory.getCurrentSession().createCriteria(LocationLevel.class).add(Restrictions.eq("code", code)).uniqueResult();
    }
	
	public List<LocationLevel> listLevels() {
		List<LocationLevel> levels = sessionFactory.getCurrentSession()
			.createCriteria(LocationLevel.class)
			.setCacheable(true)
			.setCacheRegion("locationLevelListQueryCache")
			.list();
		return levels;
	}	
	
	public List<LocationLevel> listLevels(Set<LocationLevel> skipLevels) {
		List<LocationLevel> levels = listLevels();
		if(skipLevels != null)
			levels.removeAll(skipLevels);		
		return levels;
	}
	
	public List<DataLocationType> listTypes() {
		return sessionFactory.getCurrentSession()
			.createCriteria(DataLocationType.class)
			.setCacheable(true)
			.setCacheRegion("dataLocationTypeListQueryCache")
			.list();
	}
	
	public Long getNumberOfDataLocationsForType(DataLocationType dataLocationType){
		return (Long)sessionFactory.getCurrentSession().createCriteria(DataLocation.class)
		.add(Restrictions.eq("type", dataLocationType))
		.setProjection(Projections.rowCount()).uniqueResult();
	}
		
	public <T extends CalculationLocation> T getCalculationLocation(Long id, Class<T> clazz) {
		return (T)sessionFactory.getCurrentSession().get(clazz, id);
	}
	
	public <T extends CalculationLocation> T findCalculationLocationByCode(String code, Class<T> clazz) {
		return (T) sessionFactory.getCurrentSession().createCriteria(clazz)
				.add(Restrictions.eq("code", code)).uniqueResult();
	}
	public Integer countLocation(Class<CalculationLocation> clazz, String text) {
		return getSearchCriteria(clazz, text).setProjection(Projections.count("id")).uniqueResult()
	}
	
	public <T extends CalculationLocation> List<T> searchLocation(Class<T> clazz, String text, Map<String, String> params) {
		def criteria = getSearchCriteria(clazz, text)
		
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		List<T> locations = criteria.addOrder(Order.asc("id")).list()
		
		StringUtils.split(text).each { chunk ->
			locations.retainAll { location ->
				// we look in "info" if it is a data element
				Utils.matches(chunk, location.names[languageService.getCurrentLanguage()]) ||
				Utils.matches(chunk, location.code)
			}
		}
		return locations
	}
	
	private <T extends CalculationLocation> Criteria getSearchCriteria(Class<T> clazz, String text) {
		def criteria = sessionFactory.getCurrentSession().createCriteria(clazz);
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			
			disjunction.add(Restrictions.ilike("code", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("names.jsonText", chunk, MatchMode.ANYWHERE))
			
			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		return criteria
	}
	// TODO property of level?
	public LocationLevel getLevelBefore(LocationLevel level) {
		List<LocationLevel> levels = listLevels();
		Integer intLevel = levels.indexOf(level);
		if (intLevel-1 >= 0) return levels.get(levels.indexOf(level)-1);
		else return null;
	}
	
	// TODO property of level?	
	public LocationLevel getLevelAfter(LocationLevel level) {
		List<LocationLevel> levels = listLevels();
		Integer intLevel = levels.indexOf(level);
		if (intLevel+1 < levels.size()) return levels.get(levels.indexOf(level)+1);
		else return null;
	}
	
	// TODO move to location
	public Location getParentOfLevel(CalculationLocation location, LocationLevel level) {
		Location tmp = location.getParent();
		while (tmp != null) {
			if (tmp.getLevel().equals(level)) return tmp;
			tmp = tmp.getParent();
		}
		return null;
	}
	
	// TODO move to Location
	public List<Location> getChildrenOfLevel(Location location, LocationLevel level) {
		List<Location> result = new ArrayList<Location>();
		collectChildrenOfLevel(location, level, result);
		return result;
	}
	
	private void collectChildrenOfLevel(Location location, LocationLevel level, List<Location> locations) {
		if (location.getLevel().equals(level)) locations.add(location);
		else {
			for (Location child : location.getChildren()) {
				collectChildrenOfLevel(child, level, locations);
			}
		}
	}	

}
