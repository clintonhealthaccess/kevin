package org.chai.kevin.maps;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.Calculation;
import org.chai.kevin.Expression;
import org.chai.kevin.Translatable;

@SuppressWarnings("serial")
@Entity(name="MapsTarget")
@Table(name="dhsst_maps_target")
public class MapsTarget extends Translatable {

	private Integer id;
	private Expression expression;
//	private Calculation calculation;
	private Integer order;
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
//	@ManyToOne(targetEntity=Calculation.class, optional=false)
//	public Calculation getCalculation() {
//		return calculation;
//	}
//	
//	public void setCalculation(Calculation calculation) {
//		this.calculation = calculation;
//	}
	
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
