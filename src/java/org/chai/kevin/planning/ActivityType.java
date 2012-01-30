package org.chai.kevin.planning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.Translation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity(name="ActivityType")
@Table(name="dhsst_planning_activity_type")
public class ActivityType {

	private Long id;
	
	private List<String> sections;
	private Map<String, Translation> sectionDescriptions = new HashMap<String, Translation>();
	private Map<String, Translation> headers = new HashMap<String, Translation>();
	
	// only accepts element of LIST(MAP) type
	private RawDataElement dataElement;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	@JoinTable(name="dhsst_planning_activity_type_sections")
	public List<String> getSections() {
		return sections;
	}
	
	public void setSections(List<String> sections) {
		this.sections = sections;
	}
	
	@ElementCollection(targetClass=Translation.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name="dhsst_planning_activity_type_descriptions")	
	public Map<String, Translation> getSectionDescriptions() {
		return sectionDescriptions;
	}
	
	public void setSectionDescriptions(Map<String, Translation> sectionDescriptions) {
		this.sectionDescriptions = sectionDescriptions;
	}
	
	@ElementCollection(targetClass=Translation.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name="dhsst_planning_activity_type_headers")
	public Map<String, Translation> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, Translation> headers) {
		this.headers = headers;
	}
	
	public Type getSectionType(String section) {
		return dataElement.getType().getType("[_]"+section);
	}
	

}
