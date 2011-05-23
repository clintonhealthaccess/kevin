package org.chai.kevin.dsr;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.Expression;
import org.chai.kevin.Translatable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity(name = "DsrTarget")
@Table(name = "dhsst_drs_target")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DsrTarget extends Translatable {
	private DsrObjective parent;
	private Expression expression;
	private String category;
	
	private Integer id;
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@ManyToOne(targetEntity = Expression.class, optional=false)
	public Expression getExpression() {
		return expression;
	}

	public void setParent(DsrObjective parent) {
		this.parent = parent;
	}
	
	@ManyToOne(targetEntity = DsrObjective.class)
	public DsrObjective getParent() {
		return parent;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

}
