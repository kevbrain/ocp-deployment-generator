package com.bil.buildfactory.ocp.resources;


import lombok.Data;

@Data
public class Route {
	
	private String host;

	public Route(String host) {
		super();
		this.host = host;
	}	

}
