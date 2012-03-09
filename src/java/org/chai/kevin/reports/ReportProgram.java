package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name="ReportProgram")
@Table(name="dhsst_report_program")
public class ReportProgram extends ReportEntity {

	private Long id;
	private ReportProgram parent;
	private List<ReportProgram> children = new ArrayList<ReportProgram>();
	private List<ReportTarget> targets = new ArrayList<ReportTarget>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=ReportProgram.class)
	public ReportProgram getParent() {
		return parent;
	}
	
	public void setParent(ReportProgram parent) {
		this.parent = parent;
	}

	@OneToMany(targetEntity=ReportProgram.class, mappedBy="parent")
	public List<ReportProgram> getChildren() {
		return children;
	}
	
	public void setChildren(List<ReportProgram> children) {
		this.children = children;
	}
	
	public void addChild(ReportProgram child){
		children.add(child);
		child.setParent(this);
	}
	
}
