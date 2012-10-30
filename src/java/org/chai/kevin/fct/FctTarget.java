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
package org.chai.kevin.fct;
/**
 * @author Jean Kahigiso M.
 *
 */
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Exportable;
import org.chai.kevin.reports.ReportEntity;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportTarget;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "FctTarget")
@Table(name = "dhsst_fct_target", uniqueConstraints={@UniqueConstraint(columnNames="code")})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class FctTarget extends ReportEntity implements ReportTarget, Exportable {
	
	private Long id;
	private List<FctTargetOption> targetOptions = new ArrayList<FctTargetOption>();
	private ReportProgram program;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}	
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(targetEntity=FctTargetOption.class, mappedBy="target")
	public List<FctTargetOption> getTargetOptions() {
		return targetOptions;
	}
	
	public void setTargetOptions(List<FctTargetOption> targetOptions) {
		this.targetOptions = targetOptions;
	}

	@Transient
	public void addTargetOption(FctTargetOption targetOption) {
		targetOption.setTarget(this);
		targetOptions.add(targetOption);
	}

	@Override
	public String toString() {
		return "FctTarget[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}
	
	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
	
	
	@ManyToOne(targetEntity=ReportProgram.class)
	public ReportProgram getProgram() {
		return program;
	}

	public void setProgram(ReportProgram program) {
		this.program = program;
	}

}
