package com.its4u.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Nas {

	private String id;
	
	private ServiceAccount serviceAccount;
	
	private String server;
	
	private String serverPath;
	
	public Nas(String id) {
		super();
		this.id = id;
	}
	
	
}
