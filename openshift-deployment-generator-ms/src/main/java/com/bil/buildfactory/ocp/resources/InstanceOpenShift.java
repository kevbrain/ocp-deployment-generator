package com.bil.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InstanceOpenShift {

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
			
	
		} catch (Exception e) {
			logger.info(e.getMessage());
		}		
		return projects;
		
	}
}
