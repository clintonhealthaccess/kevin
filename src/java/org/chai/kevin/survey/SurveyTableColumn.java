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
package org.chai.kevin.survey;

/**
 * @author JeanKahigiso
 *
 */

import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Exportable;
import org.chai.kevin.Orderable;
import org.chai.kevin.Translation;
import org.chai.location.DataLocationType;
import org.chai.kevin.util.Utils;

@Entity(name = "SurveyTableColumn")
@Table(name = "dhsst_survey_table_column", uniqueConstraints={@UniqueConstraint(columnNames="code")})
public class SurveyTableColumn extends Orderable implements Exportable {

	private Long id;
	private String code;
	private Integer order;
	private String typeCodeString;
	private SurveyTableQuestion question;
	private Translation names = new Translation();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}

	@Lob
	public String getTypeCodeString() {
		return typeCodeString;
	}

	public void setTypeCodeString(String typeCodeString) {
		this.typeCodeString = typeCodeString;
	}
	
	@Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, Utils.DEFAULT_CODE_DELIMITER);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, Utils.DEFAULT_CODE_DELIMITER);
	}

	@ManyToOne(targetEntity=SurveyTableQuestion.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public SurveyTableQuestion getQuestion() {
		return question;
	}

	public void setQuestion(SurveyTableQuestion question) {
		this.question = question;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}

	public void setNames(Translation names) {
		this.names = names;
	}
	
	@Transient
	public Set<String> getTypeApplicable() {
		return Utils.split(this.typeCodeString, Utils.DEFAULT_CODE_DELIMITER);
	}

	@Transient
	protected SurveyTableColumn deepCopy(SurveyCloner cloner) {
		SurveyTableColumn copy = new SurveyTableColumn();
		copy.setCode(getCode() + " clone");
		copy.setNames(new Translation(getNames()));
		copy.setTypeCodeString(getTypeCodeString());
		copy.setOrder(getOrder());
		copy.setQuestion((SurveyTableQuestion)cloner.getQuestion(getQuestion()));
		return copy;
	}
	
	@Override
	public String toString() {
		return "SurveyTableColumn[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
