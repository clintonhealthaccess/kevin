package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.TypeVisitor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hisp.dhis.period.Period;

@Entity(name="PlanningType")
@Table(name="dhsst_planning_type")
public class PlanningType {

	private Long id;
	
	private Planning planning;
	private Translation names = new Translation();
	private Translation namesPlural = new Translation();
	private String discriminator;
	private List<String> sections;
	private Map<String, Translation> sectionDescriptions = new HashMap<String, Translation>();
	private Map<String, Translation> headers = new HashMap<String, Translation>();
	
	// only accepts element of LIST type
	private RawDataElement dataElement;
	
	private List<PlanningCost> costs = new ArrayList<PlanningCost>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNamesPlural", nullable = false)) })
	public Translation getNamesPlural() {
		return namesPlural;
	}
	
	public void setNamesPlural(Translation namesPlural) {
		this.namesPlural = namesPlural;
	}
	
	@JoinColumn(nullable=false)
	@ManyToOne(targetEntity=RawDataElement.class, optional=false)
	public RawDataElement getDataElement() {
		return dataElement;
	}
	
	public void setDataElement(RawDataElement dataElement) {
		this.dataElement = dataElement;
	}
	
	@ElementCollection(targetClass=String.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name="dhsst_planning_type_sections")
	public List<String> getSections() {
		return sections;
	}
	
	public void setSections(List<String> sections) {
		this.sections = sections;
	}
	
	@ElementCollection(targetClass=Translation.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name="dhsst_planning_type_descriptions")	
	public Map<String, Translation> getSectionDescriptions() {
		return sectionDescriptions;
	}
	
	public void setSectionDescriptions(Map<String, Translation> sectionDescriptions) {
		this.sectionDescriptions = sectionDescriptions;
	}
	
	@ElementCollection(targetClass=Translation.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name="dhsst_planning_type_headers")
	public Map<String, Translation> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, Translation> headers) {
		this.headers = headers;
	}
	
	@OneToMany(mappedBy="planningType", targetEntity=PlanningCost.class)
	public List<PlanningCost> getCosts() {
		return costs;
	}
	
	public void setCosts(List<PlanningCost> costs) {
		this.costs = costs;
	}

	@ManyToOne(targetEntity=Planning.class)
	public Planning getPlanning() {
		return planning;
	}
	
	public void setPlanning(Planning planning) {
		this.planning = planning;
	}
	
	@Basic
	public String getDiscriminator() {
		return discriminator;
	}
	
	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	@Transient
	public Period getPeriod() {
		return planning.getPeriod();
	}
	
	@Transient
	public Type getDiscriminatorType() {
		return dataElement.getType().getType(getDiscriminator());
	}
	
	@Transient
	public Type getSectionType(String section) {
		return dataElement.getType().getType(section);
	}
	
	@Transient
	public List<String> getValuePrefixes(String section) {
		final Type sectionType = dataElement.getType().getType(section);
		final List<String> result = new ArrayList<String>();
		dataElement.getType().visit(new TypeVisitor() {
			@Override
			public void handle(Type type, String prefix) {
				if (!type.isComplexType() && getParents().contains(sectionType)) {
					result.add(prefix);
				}
			}
		});
		return result;
	}

	@Transient
	public List<PlanningCost> getPlanningCosts(String discriminatorValue) {
		List<PlanningCost> result = new ArrayList<PlanningCost>();
		for (PlanningCost planningCost : getCosts()) {
			if (planningCost.getDiscriminatorValue().equals(discriminatorValue)) result.add(planningCost);
		}
		return result;
	}
}
