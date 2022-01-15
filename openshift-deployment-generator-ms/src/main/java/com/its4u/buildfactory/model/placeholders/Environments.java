package com.its4u.buildfactory.model.placeholders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Data

public class Environments implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private String environment;
	
	@Setter
	@Getter
	private String projectId;
	
	@JsonIgnore

    private Project project;
	
	public List<PlaceHolders> placeholders;
	
	@JsonIgnore
	@Getter
	public List<PlaceHolders> clearPlaceholders;
	
	@JsonIgnore
	@Getter
	public List<PlaceHolders> secretsPlaceholders;
	
	public Environments() {
		super();
	}
	
	public Environments(String environment) {
		super();	
		this.environment = environment;
	}


	public Environments(Project project, String environment) {
		super();
		this.project = project;
		this.environment = environment;
	}
	
	public void setProjectId(String projectId) {
		this.projectId=projectId;
		if (project!=null) this.project.setProject_Id(projectId);
	}
	
	public String getProjectId() {
		if (project!=null) return this.project.getProject_Id();
		return this.projectId;
	}
	
	public List<PlaceHolders> getSecretsPlaceholders() {
		List<PlaceHolders> secrets = new ArrayList<>();
		for (PlaceHolders pl: placeholders) {
			if (pl.getType()!=null && pl.getType().equalsIgnoreCase("secret")) {
				secrets.add(pl);
			}
		}
		return secrets;
	}
	
	public List<PlaceHolders> getClearPlaceholders() {
		List<PlaceHolders> clears = new ArrayList<>();
		for (PlaceHolders pl: placeholders) {
			if (pl.getType()==null || !pl.getType().equalsIgnoreCase("secret")) {
				clears.add(pl);
			}
		}
		return clears;
	}

	@Override
	public String toString() {
		return "Environments [environment=" + environment + "]";
	}

	
	
}
