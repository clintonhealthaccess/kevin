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
package org.chai.kevin.exports

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.location.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.util.ImportExportConstant;
import org.chai.kevin.Translation;
import org.chai.kevin.exports.DataExport;
import org.apache.commons.lang.StringUtils
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.chai.kevin.util.Utils
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

/**
 * @author Jean Kahigiso M.
 *
 */
public class DataExportService {

	def languageService;
	def sessionFactory;

	public Integer countDataExports(Class<DataExport> clazz, String text) {
		return getSearchCriteria(clazz,text).setProjection(Projections.count("id")).uniqueResult()
	}

	public <T extends DataExport> List<T> searchDataExports(Class<T> clazz, String text, Map<String, String> params) {
		def exporters=[]
		def criteria = getSearchCriteria(clazz,text)

		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])

		if(params['sort']!=null)
			exporters= criteria.addOrder(Order.asc(params['sort'])).list()
		else
			exporters= criteria.addOrder(Order.desc("date")).list()

		StringUtils.split(text).each { chunk ->
			exporters.retainAll { exporter ->
				Utils.matches(chunk, exporter.descriptions[languageService.getCurrentLanguage()]);
			}
		}

		return exporters;
	}
	
	private Criteria getSearchCriteria(Class<DataExport> clazz, String text) {
		def criteria = sessionFactory.getCurrentSession().createCriteria(clazz);
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.ilike("descriptions.jsonText", chunk, MatchMode.ANYWHERE))
			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		return criteria
	}
	
}