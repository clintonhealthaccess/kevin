package org.chai.kevin.cost;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.Expression;
import org.chai.kevin.Objective;

@Entity(name="CostTarget")
@Table(name="dhsst_cost_target")
public class CostTarget extends Objective implements Comparable<CostTarget> {

	public static enum CostType {
		INVESTMENT("INVESTMENT", "Investment"), OPERATION("OPERATION", "Operation");
		
		final String value;
		final String name;
		
	    CostType(String value, String name) { this.value = value; this.name = name;}

	    public String getName() { return name; }
	    public String toString() { return value; } 
	    String getKey() { return name(); }
	};
	
	private Expression expression;
	private Expression expressionEnd;
	
	private CostObjective parent;
	private CostRampUp costRampUp;
	private CostType costType;
	private String groupUuidString;
	
	private Integer order;
	
	@ManyToOne(targetEntity=Expression.class, optional=false)
	public Expression getExpression() {
		return expression;
	}
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@ManyToOne(targetEntity=Expression.class)
	public Expression getExpressionEnd() {
		return expressionEnd;
	}
	public void setExpressionEnd(Expression expressionEnd) {
		this.expressionEnd = expressionEnd;
	}
	
	@ManyToOne(targetEntity=CostObjective.class)
	public CostObjective getParent() {
		return parent;
	}
	public void setParent(CostObjective parent) {
		this.parent = parent;
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
	
//	@Transient
//	public Set<String> getGroupUuids() {
//		if (groupUuidString == null) return new HashSet<String>();
//		return new HashSet<String>(Arrays.asList(StringUtils.split(groupUuidString, ',')));
//	}
//	public void setGroupUuids(Set<String> groupUuids) {
//		this.groupUuidString = StringUtils.join(groupUuids, ',');
//	}
	
	@Basic(optional=true)
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@Transient
	public boolean isAverage() {
		return expressionEnd != null;
	}
	
	@Override
	public int compareTo(CostTarget o) {
		if (this.getOrder() == null) {
			if (o.getOrder() == null) return 0;
			else return 1;
		}
		if (o.getOrder() == null) return -1;
		return this.getOrder().compareTo(o.getOrder());
	}
	
}
