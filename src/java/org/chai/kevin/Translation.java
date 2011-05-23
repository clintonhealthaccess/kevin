package org.chai.kevin;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name="Translation")
@Table(name="translation")
public class Translation {

	private Long id;
	private String locale;
	private String text;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	@Basic
	public String getLocale() {
		return locale;
	}
	
	@Basic
	public String getText() {
		return text;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
