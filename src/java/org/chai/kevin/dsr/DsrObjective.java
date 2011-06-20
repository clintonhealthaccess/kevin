package org.chai.kevin.dsr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translatable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity(name = "DsrObjective")
@Table(name = "dhsst_dsr_objective")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DsrObjective extends Translatable {
	
	private Integer id;
	private Integer order;
	private List<DsrTarget> targets = new ArrayList<DsrTarget>();
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setTargets(List<DsrTarget> targets) {
		this.targets = targets;
	}
	@OneToMany(cascade=CascadeType.ALL,targetEntity=DsrTarget.class, mappedBy="objective")
	public List<DsrTarget> getTargets() {
		return targets;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@Basic
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	@Transient
	public void addTarget(DsrTarget target) {
		target.setObjective(this);
		targets.add(target);
	}

}
