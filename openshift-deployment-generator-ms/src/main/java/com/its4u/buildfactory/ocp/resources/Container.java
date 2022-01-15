package com.its4u.buildfactory.ocp.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Container {
	
	private int containerID;
	
	private String name;
	
	private String image;
	
	private List<String> envVariables;
	
	private String selectedKey;
		
	private List<String> selectedEnvKeys;
	
	
	public Container(int containerID,String Appname) {
		super();
		this.name="-container-ms-"+containerID;
		this.containerID = containerID;
		this.envVariables = new ArrayList<String>();
    	this.selectedEnvKeys = new ArrayList<String>();
	}

}
