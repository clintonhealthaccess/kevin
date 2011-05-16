package org.chai.kevin.maps;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.Expression;
import org.chai.kevin.Objective;
import org.hisp.dhis.indicator.Indicator;

@Entity(name="MapsTarget")
@Table(name="dhsst_maps_target")
public class MapsTarget extends Objective {

	private Expression expression;
	private Integer order;
	
	@ManyToOne(targetEntity=Expression.class, optional=false)
	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Basic(optional=true)
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	
}
