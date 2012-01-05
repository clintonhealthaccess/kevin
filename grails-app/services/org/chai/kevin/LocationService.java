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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class LocationService {
	
	private static final Log log = LogFactory.getLog(LocationService.class);
	
	private SessionFactory sessionFactory;
	
    public LocationEntity getRootLocation() {
    	return (LocationEntity)sessionFactory.getCurrentSession().createCriteria(LocationEntity.class).add(Restrictions.isNull("parent")).uniqueResult();
    }
    
    public DataEntityType findDataEntityTypeByCode(String code) {
    	return (DataEntityType)sessionFactory.getCurrentSession().createCriteria(DataEntityType.class).add(Restrictions.eq("code", code)).uniqueResult();
    }
    
    public LocationLevel findLocationLevelByCode(String code) {
    	return (LocationLevel)sessionFactory.getCurrentSession().createCriteria(LocationLevel.class).add(Restrictions.eq("code", code)).uniqueResult();
    }
	
	public List<LocationLevel> listLevels(LocationLevel... skipLevels) {
		List<LocationLevel> levels = sessionFactory.getCurrentSession().createCriteria(LocationLevel.class).list();
		levels.removeAll(Arrays.asList(skipLevels));
		Collections.sort(levels);
		return levels;
	}	
	
	public List<DataEntityType> listTypes() {
		return sessionFactory.getCurrentSession().createCriteria(DataEntityType.class).list();
	}
	
	public Long getNumberOfDataEntitiesForType(DataEntityType dataEntityType){
		return (Long)sessionFactory.getCurrentSession().createCriteria(DataEntity.class)
		.add(Restrictions.eq("type", dataEntityType))
		.setProjection(Projections.rowCount()).uniqueResult();
	}
		
	public <T extends CalculationEntity> T getCalculationEntity(Long id, Class<T> clazz) {
		return (T)sessionFactory.getCurrentSession().get(clazz, id);
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
	
	public LocationEntity getParentOfLevel(CalculationEntity entity, LocationLevel level) {
		LocationEntity tmp = entity.getParent();
		while (tmp != null) {
			if (tmp.getLevel().equals(level)) return tmp;
			tmp = tmp.getParent();
		}
		return null;
	}
	
	public List<LocationEntity> getChildrenOfLevel(LocationEntity location, LocationLevel level) {
		List<LocationEntity> result = new ArrayList<LocationEntity>();
		collectChildrenOfLevel(location, level, result);
		return result;
	}
	
	private void collectChildrenOfLevel(LocationEntity location, LocationLevel level, List<LocationEntity> locations) {
		if (location.getLevel().equals(level)) locations.add(location);
		else {
			for (LocationEntity child : location.getChildren()) {
				collectChildrenOfLevel(child, level, locations);
			}
		}
	}

	public List<DataEntity> getDataEntities(CalculationEntity calculationEntity, DataEntityType... types) {
		List<DataEntity> result = new ArrayList<DataEntity>();
		collectDataEntitiesForLocation(calculationEntity, result, new HashSet<DataEntityType>(Arrays.asList(types)));
		return result;
	}
	
	private void collectDataEntitiesForLocation(CalculationEntity calculationEntity, List<DataEntity> dataEntities, Set<DataEntityType> types) {
		dataEntities.addAll(getDataEntitiesForLocation(calculationEntity, types));
		for (CalculationEntity child : calculationEntity.getChildren()) {
			collectDataEntitiesForLocation(child, dataEntities, types);
		}
	}
	
	private List<DataEntity> getDataEntitiesForLocation(CalculationEntity calculationEntity, Set<DataEntityType> types) {
		List<DataEntity> result = new ArrayList(calculationEntity.getDataEntities());
		if (!types.isEmpty()) {
			for (DataEntity dataEntity : calculationEntity.getDataEntities()) {
				if (!types.contains(dataEntity.getType())) result.remove(dataEntity);
			}
		}
		return result;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

}
