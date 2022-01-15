package com.its4u.buildfactory.ocp.resources;

import lombok.Data;

@Data
public class Mounting {

	private boolean selected;
	
	private String keyName;
	
	private String value;
	
	private ConfigResource configResource;
	
	private String[] containers;
		

	public Mounting(String keyName, String value) {
		super();
		this.keyName = keyName;
		this.value = value;
	}
		

	public Mounting(String keyName,String keyValue,ConfigResource configResource,String[] containers, boolean selected) {
		super();
		this.keyName = keyName;
		this.value= keyValue;
		this.configResource = configResource;
		this.selected= selected;
		this.containers = containers;
	}

	
}
