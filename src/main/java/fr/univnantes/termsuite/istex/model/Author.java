package fr.univnantes.termsuite.istex.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Author {
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private List<String> affiliations;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getAffiliations() {
		return affiliations;
	}
	
	public void setAffiliations(List<String> affiliations) {
		this.affiliations = affiliations;
	}
}
