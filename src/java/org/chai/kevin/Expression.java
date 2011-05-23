package org.chai.kevin;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Expression")
@Table(name="dhsst_expression", uniqueConstraints={@UniqueConstraint(columnNames="name")})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Expression {

	public static enum ExpressionType {VALUE("VALUE"), STRING("STRING"), BOOL("BOOL"), ENUM("ENUM");
		final String value;

		ExpressionType(String value) { this.value = value; }

	    public String toString() { return value; } 
	    String getKey() { return name(); }
	};
	
	private Long id;
	private String name;
	private String description;
	
	private ExpressionType type;
	private String expression;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Basic
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Basic
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public ExpressionType getType() {
		return type;
	}
	public void setType(ExpressionType type) {
		this.type = type;
	}
	
	@Basic
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Override
	public String toString() {
		return "Expression [name=" + name + ", expression=" + expression + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Expression other = (Expression) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
