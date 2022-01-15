package com.its4u.buildfactory.maven.resources;

import com.its4u.buildfactory.ocp.resources.TemplateResource;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectArborescenceItem {

	private String name;
	
	private String type;
	
	private TemplateResource templateResource;

	public ProjectArborescenceItem(String name, String type, TemplateResource templateResource) {
		super();
		this.name = name;
		this.type = type;
		this.templateResource = templateResource;
	}
	
	
}
