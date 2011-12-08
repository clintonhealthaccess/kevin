package org.chai.kevin.cost;

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

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.data.DataElement;
import org.chai.kevin.reports.ReportEntity;
import org.chai.kevin.util.Utils;

@Entity(name="CostTarget")
@Table(name="dhsst_cost_target")
public class CostTarget extends ReportEntity {

	public static enum CostType {
		INVESTMENT("INVESTMENT", "Investment"), OPERATION("OPERATION", "Operation");
		
		final String value;
		final String name;
		
	    CostType(String value, String name) { this.value = value; this.name = name;}

	    public String getName() { return name; }
	    public String toString() { return value; } 
	    String getKey() { return name(); }
	};
	
	private Long id;
	private DataElement<?> dataElement;
	private DataElement<?> dataElementEnd;
	
	private CostObjective objective;
	private CostRampUp costRampUp;
	private CostType costType;
	private String groupUuidString = "";
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=DataElement.class, optional=false)
	public DataElement<?> getDataElement() {
		return dataElement;
	}
	
	public void setDataElement(DataElement<?> dataElement) {
		this.dataElement = dataElement;
	}
	
	@ManyToOne(targetEntity=DataElement.class)
	public DataElement<?> getDataElementEnd() {
		return dataElementEnd;
	}

	public void setDataElementEnd(DataElement<?> dataElementEnd) {
		this.dataElementEnd = dataElementEnd;
	}
	
	@ManyToOne(targetEntity=CostObjective.class)
	// TODO change the name of the column
	@JoinColumn(name="parent")
	public CostObjective getObjective() {
		return objective;
	}
	public void setObjective(CostObjective objective) {
		this.objective = objective;
	}
	
	@ManyToOne(targetEntity=CostRampUp.class, optional=false)
	public CostRampUp getCostRampUp() {
		return costRampUp;
	}
	public void setCostRampUp(CostRampUp costRampUp) {
		this.costRampUp = costRampUp;
	}
	
	@Enumerated
	@Column(nullable=false)
	public CostType getCostType() {
		return costType;
	}
	public void setCostType(CostType costType) {
		this.costType = costType;
	}
	
	@Lob
	public String getGroupUuidString() {
		return groupUuidString;
	}
	public void setGroupUuidString(String groupUuidString) {
		this.groupUuidString = groupUuidString;
	}
	
	@Transient
	public Set<String> getGroupUuids() {
		return Utils.split(groupUuidString);
	}
	public void setGroupUuids(Set<String> groupUuids) {
		this.groupUuidString = Utils.unsplit(groupUuids);
	}
	
	@Transient
	public boolean isAverage() {
		return dataElementEnd != null;
	}
	
}
