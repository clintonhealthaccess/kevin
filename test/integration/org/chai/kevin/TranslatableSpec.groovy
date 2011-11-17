package org.chai.kevin

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

import grails.validation.ValidationException;

import java.util.List;

import net.sf.json.JSONObject;

import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyObjective
import org.chai.kevin.survey.SurveySection
import org.chai.kevin.survey.SurveyTableColumn
import org.chai.kevin.survey.SurveyTableQuestion
import org.chai.kevin.survey.SurveyTableRow
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.dao.DataIntegrityViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

class TranslatableSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(TranslatableSpec.class)

	static final String ENGLISH = "ENGLISH"
	static final String FRANCAIS = "FRANCAIS"

	def "empty name transfers properly to json"() {
		when:
		def dataElement = newRawDataElement(new Translation(), CODE(1), Type.TYPE_NUMBER())

		then:
		dataElement.names["en"] == null
		dataElement.names["fr"] == null
	}


	def "translatable set map sets json"() {
		when:
		def dataElement = newRawDataElement(j(["en":ENGLISH, "fr":FRANCAIS]), CODE(1), Type.TYPE_NUMBER())

		then:
		(new HashMap(["en":ENGLISH, "fr":FRANCAIS])).equals(dataElement.names)
		dataElement.names["en"] == ENGLISH
		dataElement.names["fr"] == FRANCAIS
	}


	def "translatable set json sets map"() {
		when:
		def dataElement = newRawDataElement(j("en":"test"), CODE(1), Type.TYPE_NUMBER())
		
		dataElement.names.putAll([en: ENGLISH, fr: FRANCAIS]);
		dataElement.save(failOnError:true);
		dataElement = RawDataElement.findByCode(CODE(1))

		then:
		dataElement.names["en"] == ENGLISH
		dataElement.names["fr"] == FRANCAIS
	}


	def "translatable set map modifies json"() {
		when:
		def dataElement = newRawDataElement(new Translation(), CODE(1), Type.TYPE_NUMBER())

		dataElement.names = new Translation(jsonText: JSONUtils.getJSONFromMap([en: ENGLISH, fr: "Anglais"]));
		dataElement.save(failOnError:true)
		dataElement.names = new Translation(jsonText: JSONUtils.getJSONFromMap([en: ENGLISH]));
		dataElement.save(failOnError: true)

		then:
		dataElement.names.getJsonText() == new JSONObject().put("en", ENGLISH).toString()
		dataElement.names["en"] == ENGLISH
		dataElement.names["fr"] == null
	}

}
