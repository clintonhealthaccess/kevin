package org.chai.kevin.dsr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.Translatable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity(name = "DsrObjective")
@Table(name = "dhsst_dsr_objective")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DsrObjective extends Translatable {
	private List<DsrTarget> targets = new ArrayList<DsrTarget>();

	private Integer id;
	
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
	@OneToMany(cascade=CascadeType.ALL,targetEntity=DsrTarget.class)
	public List<DsrTarget> getTargets() {
		return targets;
	}

	public void addTarget(DsrTarget target) {
		target.setParent(this);
		targets.add(target);
	}

}
