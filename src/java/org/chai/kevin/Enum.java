package org.chai.kevin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Enum")
@Table(name="enum")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Enum extends Translatable {

	private Long id;

	private List<EnumOption> enumOptions = new ArrayList<EnumOption>();
	
	@Id
	@GeneratedValue
	@Column
	public Long getId() {
		return id;
	}
	
	@OneToMany(mappedBy="enume", targetEntity=EnumOption.class)
	public List<EnumOption> getEnumOptions() {
		return enumOptions;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setEnumOptions(List<EnumOption> enumOptions) {
		this.enumOptions = enumOptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enum other = (Enum) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	
	
}
