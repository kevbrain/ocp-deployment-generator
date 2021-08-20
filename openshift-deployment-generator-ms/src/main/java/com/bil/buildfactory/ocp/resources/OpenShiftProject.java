package com.bil.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class OpenShiftProject extends Resource{

	public static final String TYPE = "projects";
	
	public static final String API= "/oapi/v1/";
	
	public static final String PATHAPI = API;
	
	private String projectName;
	
//	private Map<String, DeploymentConfig> deploymentConfigs;
	
	
//	private Map<String, Routes> routes;
	
	private Map<String, ConfigMap> configMaps;
	
	protected static Logger logger = LoggerFactory.getLogger(OpenShiftProject.class.getName());

	public OpenShiftProject(String projectName) {
		super(projectName,TYPE,"true",PATHAPI);
		this.projectName = projectName;
	//	this.deploymentConfigs = new HashMap<>();
		//this.services = new HashMap<>();
		//this.routes = new HashMap<>();
	}
	
	

}
