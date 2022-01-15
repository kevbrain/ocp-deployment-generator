package com.its4u.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class ConfigResource {
	
	private int configResourceID;
	
	private String name;
	
	private HashMap<String,String> keyValue;
	
	private List<Mounting> selectedKeyValue;

	public ConfigResource(int configResourceID, String name, HashMap<String,String> keyValue) {
		super();
		this.configResourceID = configResourceID;
		this.name = name;
		this.keyValue = keyValue;
		
	}	
}
