package com.bil.buildfactory.model;

import com.bil.buildfactory.ocp.resources.TemplateGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JoinFacesModel extends TemplateModel {
	
	private String appName;
	


	public String getAppName() {
		return appName;
	}



	public JoinFacesModel(String appName) {
		super();
		this.appName = appName;
	}


	
	

}
