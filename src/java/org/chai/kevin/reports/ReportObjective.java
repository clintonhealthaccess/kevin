package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name="ReportObjective")
@Table(name="dhsst_report_objective")
public class ReportObjective extends ReportEntity {

	private Long id;
	private ReportObjective parent;
	private List<ReportObjective> children = new ArrayList<ReportObjective>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=ReportObjective.class)
	public ReportObjective getParent() {
		return parent;
	}
	
	public void setParent(ReportObjective parent) {
		this.parent = parent;
	}

	@OneToMany(targetEntity=ReportObjective.class, mappedBy="parent")
	public List<ReportObjective> getChildren() {
		return children;
	}
	
	public void setChildren(List<ReportObjective> children) {
		this.children = children;
	}
	
	public void addChild(ReportObjective child){
		children.add(child);
		child.setParent(this);
	}
	
}
