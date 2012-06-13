package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.chai.kevin.Translation;
import org.chai.kevin.data.DataElement;

@Entity(name="PlanningOutput")
@Table(name="dhsst_planning_output")
public class PlanningOutput {

	private Long id;
	
	private Integer order;
	private Planning planning;
	private DataElement<?> dataElement;
	private String fixedHeader;
	
	private List<PlanningOutputColumn> columns = new ArrayList<PlanningOutputColumn>();
	private Translation names = new Translation();
	private Translation helps = new Translation();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@ManyToOne(targetEntity=Planning.class)
	public Planning getPlanning() {
		return planning;
	}

	public void setPlanning(Planning planning) {
		this.planning = planning;
	}

	@ManyToOne(targetEntity=DataElement.class)
	public DataElement<?> getDataElement() {
		return dataElement;
	}

	public void setDataElement(DataElement<?> dataElement) {
		this.dataElement = dataElement;
	}

	@OneToMany(targetEntity=PlanningOutputColumn.class, mappedBy="planningOutput")
	@OrderBy("order")
	public List<PlanningOutputColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<PlanningOutputColumn> columns) {
		this.columns = columns;
	}
	
	public void addColumn(PlanningOutputColumn column) {
		columns.add(column);
		Collections.sort(columns);
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonHelps", nullable = false)) })
	public Translation getHelps() {
		return helps;
	}
	
	public void setHelps(Translation helps) {
		this.helps = helps;
	}
	
	@Basic
	public String getFixedHeader() {
		return fixedHeader;
	}
	
	public void setFixedHeader(String fixedHeader) {
		this.fixedHeader = fixedHeader;
	}
	
}
