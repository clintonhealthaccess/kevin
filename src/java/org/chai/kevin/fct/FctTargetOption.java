package org.chai.kevin.fct;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.data.Sum;
import org.chai.kevin.reports.ReportEntity;
import org.chai.kevin.util.Utils;

@Entity(name = "FctTargetOption")
@Table(name = "dhsst_fct_target_option")
public class FctTargetOption extends ReportEntity {

	private Long id;
	private Sum sum;
	private FctTarget target;
	private String format;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=Sum.class)
	public Sum getSum() {
		return sum;		
	}
	
	public void setSum(Sum sum){
		this.sum = sum;
	}
	
	@ManyToOne(targetEntity=FctTarget.class)
	public FctTarget getTarget() {
		return target;
	}
	
	public void setTarget(FctTarget target) {
		this.target = target;
	}
	
	@Basic
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
