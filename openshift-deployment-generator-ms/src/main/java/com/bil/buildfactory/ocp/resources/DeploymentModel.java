package com.bil.buildfactory.ocp.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeploymentModel {
	
	
	public DeploymentModel() {
		super();
		configMaps = new ArrayList<>();
		configMapsAsvol = new ArrayList<>();
		emptyDirVolumes =new ArrayList<>();
		persitentVolumes= new ArrayList<>();
		secrets = new ArrayList<>();
		routes = new ArrayList<>();
		cmVolumes = new ArrayList<>();
		claims = new ArrayList<>();
		config = new HashMap<>();
	    secret = new HashMap<>();
	    nas =new ArrayList<>();
	    serviceAccount = null;
	}

	private TemplateGenerator templateGenerator;
	
	private HashMap<String,String> config;
	
	private HashMap<String,String> secret;
		
	private List<ConfigMap> configMaps;
	
	private List<Secrets> secrets;
	
	private List<ConfigMap> configMapsAsvol;
	
	private List<Volumes> emptyDirVolumes;
	
	private List<Volumes> persitentVolumes;
	
	private List<Volumes> cmVolumes;
	
	private List<Route> routes;
	
	private List<PersistentVolumeClaim> claims;
	
	private List<Volumes> nas;
	
	private String appName;
	
	private ServiceAccount serviceAccount;
	
	private String userUid;
	
	private String userGid;
	
	private String ibmUser;

}
