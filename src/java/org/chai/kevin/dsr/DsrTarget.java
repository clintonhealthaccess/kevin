package org.chai.kevin.dsr;

import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Expression;
import org.chai.kevin.Translatable;
import org.chai.kevin.Translation;
import org.chai.kevin.util.JSONUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity(name = "DsrTarget")
@Table(name = "dhsst_dsr_target")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DsrTarget extends Translatable {
	private Integer id;
	private Integer order;
	private DsrObjective objective;
	private Expression expression;
	private DsrTargetCategory category;

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

	@ManyToOne(targetEntity = Expression.class, optional = false)
	public Expression getExpression() {
		return expression;
	}

	public void setObjective(DsrObjective objective) {
		this.objective = objective;
	}
	@ManyToOne(targetEntity = DsrObjective.class)
	public DsrObjective getObjective() {
		return objective;
	}
	public void setCategory(DsrTargetCategory category) {
		this.category = category;
	}

	@ManyToOne(targetEntity = DsrTargetCategory.class, optional = true)
	public DsrTargetCategory getCategory() {
		return category;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Basic
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}

}
