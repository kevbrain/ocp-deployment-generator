package com.its4u.buildfactory.model.placeholders;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Project implements Serializable{
	
	private static final long serialVersionUID = 1L;
	

	private String project_Id;
	
	private String gitUrl;
	
	private String owner;

	private List<Environments> environments;

	public Project() {
		super();
	}

	public Project(String projectId, String gitUrl, String owner) {
		
		super();
		this.project_Id = projectId;
		this.gitUrl = gitUrl;
		this.owner = owner;
		System.out.println("New project placeholder : "+projectId+" "+gitUrl+" "+owner);
	}

	
	public String toString() {
		return project_Id ;
	}
	
}
