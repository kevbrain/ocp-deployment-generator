package com.bil.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonarsource.scanner.api.internal.shaded.minimaljson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InstanceOpenShift {
	
	@Autowired
	RestTemplate restTemplate;

    private String name;
	
	private String url;
	
	private String token;	
	
	private Map<String, OpenShiftProject> projects;
	
	protected static Logger logger = LoggerFactory.getLogger(InstanceOpenShift.class.getName());

	public InstanceOpenShift(String name,String url, String token) {
		super();
		this.name = name;
		this.url = url;
		this.token = token;
	}



	public InstanceOpenShift(String url, String token) {
		super();
		this.url = url;
		this.token = token;
	}
	
	public Map<String, OpenShiftProject> getMapProject() {
		
		System.out.println("GET MAP PROJECT ....");
		String urlCall = this.getUrl()+"/api/v1/namespaces";
		System.out.println("URL = "+urlCall);
				
	    HashMap<String, OpenShiftProject> projects = new HashMap<>();
		
		try {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
	        headers.add("Pragma", "no-cache");
	        headers.add("Expires", "0");
	        headers.add("Accept", "application/json");
	        
	              
	        JsonObject myObj = restTemplate.getForObject("", JsonObject.class); 
	        System.out.println(myObj);
	        
			/*
			// load dcs , services , routes , secrets
			JSONArray results = myObj.getJSONArray("items");						
			for (int i=0;i<results.length();i++) {
				
				JSONObject itemsProject = results.getJSONObject(i).getJSONObject("metadata");
				String projectName = itemsProject.getString("name").toString();	
				
				//if ( !(projectName.equalsIgnoreCase("kafka") && openshift.getName().equalsIgnoreCase("PRODUCTION") )) {
					OpenShiftProject project =new OpenShiftProject(projectName);	
					
					//Map<String, Services> svcs= Services.getMapSVC(openshift, projectName);
					//Map<String, Routes> routes= Routes.getMapRoutes(openshift, projectName);//getMapRoutes(openshift, projectName);	
					//Map<String, Secret> secrets = Secret.getMapSecrets(openshift, projectName); //getMapSecrets(openshift, projectName);
					//Map<String, ConfigMap> configMaps = ConfigMap.getMapResources(openshift, projectName); //getMapConfigMap(openshift, projectName);
					//Map<String, DeploymentConfig> dcs = DeploymentConfig.getMapDC(openshift, projectName, svcs, routes, secrets);//getMapDC(openshift,projectName,svcs,routes,secrets);
															
					//project.setDeploymentConfigs(dcs);
					//project.setServices(svcs);
					//project.setRoutes(routes);
					//project.setConfigMaps(configMaps);
												
					projects.put(project.getProjectName(),project);
				//}
			}
			*/
		} catch (Exception e) {
			logger.info(e.getMessage());
		}		
		return projects;
		
	}
}
