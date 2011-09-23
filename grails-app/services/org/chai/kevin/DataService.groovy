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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.util.Utils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;


class DataService {

	static transactional = true
	
	LocaleService localeService;
	SessionFactory sessionFactory;
	
	public Data getData(Long id) {
		return (Data)sessionFactory.getCurrentSession().get(Data.class, id);
	}
		
	public Enum getEnum(Long id) {
		return (Enum)sessionFactory.getCurrentSession().get(Enum.class, id);
	}
	
	public Enum findEnumByCode(String code) {
		return Enum.findByCode(code)
	}
	
//	def searchConstants(String text) {
//		def constants = Constant.list()
//		StringUtils.split(text).each { chunk ->
//			constants.retainAll { element ->
//				Utils.matches(chunk, element.id+"") ||
//				Utils.matches(chunk, element.names[localeService.getCurrentLanguage()]) ||
//				Utils.matches(chunk, element.code) 
//			}
//		}
//		return constants.sort {it.names[localeService.getCurrentLanguage()]}
//	}
	
    public List<DataElement> searchDataElements(String text, List<String> allowedTypes) {
		def criteria = DataElement.createCriteria()
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			
			disjunction.add(Restrictions.ilike("names.jsonText", text, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("code", text, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("info", text, MatchMode.ANYWHERE))
			if (NumberUtils.isNumber(chunk)) disjunction.add(Restrictions.eq("id", Long.parseLong(chunk)))
			
			textRestrictions.add(disjunction)
		}
		
		if (!allowedTypes.isEmpty()) {
			def typeRestrictions = Restrictions.disjunction()
			allowedTypes.each { type ->
				typeRestrictions.add(Restrictions.like("type.jsonType", type, MatchMode.ANYWHERE))
			}
			criteria.add(Restrictions.and(textRestrictions, typeRestrictions))
		}
		else {
			criteria.add(textRestrictions)
		}
		
		List<DataElement> dataElements = criteria.setMaxResults(200).list()
		
		StringUtils.split(text).each { chunk ->
			dataElements.retainAll { element ->
				Utils.matches(chunk, element.id+"") ||
				Utils.matches(chunk, element.names[localeService.getCurrentLanguage()]) ||
				Utils.matches(chunk, element.code) ||
				Utils.matches(chunk, element.info)
			}
		}
		
		if (!allowedTypes.isEmpty()) {
			dataElements.retainAll { element ->
				element.type.type.name().toLowerCase() in allowedTypes 
			}
		}
		
		return dataElements.sort {it.names[localeService.getCurrentLanguage()]}
    }
	
    
}
