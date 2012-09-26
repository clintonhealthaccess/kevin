package org.chai.kevin.reports;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.data.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="AbstractReportTarget")
@Table(name="dhsst_report_target" /*, uniqueConstraints={@UniqueConstraint(columnNames="code")}*/)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class AbstractReportTarget extends ReportEntity {

	private Long id;
	private Data<?> data;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=Data.class, optional=false, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Data<?> getData() {
		return data;
	}

	public void setData(Data<?> data) {
		this.data = data;
	}
	
}
