package com.its4u.buildfactory.model.placeholders;

import java.io.Serializable;


import lombok.Data;

@Data
public class PlaceHolderId implements Serializable {
	

	private String environment;
	

	private String key;
	
	
	public PlaceHolderId() {
		super();
	}


	public PlaceHolderId(String environment, String key) {
		super();
		this.environment = environment;
		this.key = key;
	}
	
	

}
