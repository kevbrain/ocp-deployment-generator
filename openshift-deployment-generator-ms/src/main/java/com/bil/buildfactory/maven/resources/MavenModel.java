package com.bil.buildfactory.maven.resources;

import java.util.HashMap;
import java.util.List;

import com.bil.buildfactory.model.TemplateModel;
import com.bil.buildfactory.ocp.resources.ConfigMap;
import com.bil.buildfactory.ocp.resources.PersistentVolumeClaim;
import com.bil.buildfactory.ocp.resources.Route;
import com.bil.buildfactory.ocp.resources.Secrets;
import com.bil.buildfactory.ocp.resources.ServiceAccount;
import com.bil.buildfactory.ocp.resources.TemplateGenerator;
import com.bil.buildfactory.ocp.resources.Volumes;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MavenModel extends TemplateModel{

	
	private String group;
	
	private String artifact;
	
	private String description;
	
	private String packageName;
	
	private String java;
	
	private String ocpNamespace;
	
	private String ocpRegistry;
	
	private boolean newMavenProject;
	
	
	private boolean lombok;
	
	private boolean actuator;
	
	private boolean freemarker;
	
	private boolean sonar;
	
	private boolean web;
	
	private boolean joinfaces;

	public MavenModel(String group, String artifact, String description, String packageName, String java, String ocpNamespace, String ocpRegistry,
			boolean newMavenProject, boolean lombok, boolean actuator, boolean freemarker, boolean sonar, boolean web,
			boolean joinfaces) {
		super();
		this.group = group;
		this.artifact = artifact;
		this.description = description;
		this.packageName = packageName;
		this.java = java;
		this.ocpNamespace = ocpNamespace;
		this.ocpRegistry = ocpRegistry;
		this.newMavenProject = newMavenProject;
		this.lombok = lombok;
		this.actuator = actuator;
		this.freemarker = freemarker;
		this.sonar = sonar;
		this.web = web;
		this.joinfaces = joinfaces;
	}
	
	
	
}
