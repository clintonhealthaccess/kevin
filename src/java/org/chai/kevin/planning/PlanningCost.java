package org.chai.kevin.planning;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.util.Utils;

@Entity(name="PlanningCost")
@Table(name="dhsst_planning_cost")
public class PlanningCost {

	public enum PlanningCostType {OUTGOING("planning.planningcost.type.outgoing"), INCOMING("planning.planningcost.type.incoming");
		private String code;
	
		PlanningCostType(String code) {
			this.code = code;
		}
	
		public String getCode() {
			return code;
		}
		String getKey() { return name(); }
	};

	private Long id;
	private PlanningCostType type;
	private String discriminatorValueString;
	private NormalizedDataElement dataElement;
	private Translation names = new Translation();
	
	// section in which the cost is grouped (can be null)
	private String groupSection;
	
	// corresponding section in PlanningType (cannot be null)
	// this is the section that will open when clicking on the line
	private String section;
	
	private PlanningType planningType;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=NormalizedDataElement.class)
	public NormalizedDataElement getDataElement() {
		return dataElement;
	}

	public void setDataElement(NormalizedDataElement dataElement) {
		this.dataElement = dataElement;
	}
	
	@Basic
	@Enumerated(EnumType.STRING)
	public PlanningCostType getType() {
		return type;
	}
	
	public void setType(PlanningCostType type) {
		this.type = type;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}
	
	@Basic
	public String getGroupSection() {
		return groupSection;
	}
	
	public void setGroupSection(String groupSection) {
		this.groupSection = groupSection;
	}
	
	@Basic
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	@ManyToOne(targetEntity=PlanningType.class)
	public PlanningType getPlanningType() {
		return planningType;
	}
	
	public void setPlanningType(PlanningType planningType) {
		this.planningType = planningType;
	}
	
	@Basic
	public String getDiscriminatorValueString() {
		return discriminatorValueString;
	}
	
	public void setDiscriminatorValueString(String discriminatorValueString) {
		this.discriminatorValueString = discriminatorValueString;
	}

	@Transient
	public Set<String> getDiscriminatorValues() {
		return Utils.split(discriminatorValueString);
	}
	
	public void setDiscriminatorValues(Set<String> discriminatorValues) {
		this.discriminatorValueString = Utils.unsplit(discriminatorValues);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PlanningCost))
			return false;
		PlanningCost other = (PlanningCost) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
