package com.its4u.buildfactory.ocp.resources;

import lombok.Data;

@Data
public class ServiceAccount {

	private String name;

	
	
	public ServiceAccount(String name) {
		super();
		this.name = name;
	}



	public ServiceAccount() {
		super();
	}
	
	
}
