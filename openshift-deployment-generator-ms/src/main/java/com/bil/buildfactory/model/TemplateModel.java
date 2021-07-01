package com.bil.buildfactory.model;


import com.bil.buildfactory.ocp.resources.TemplateGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateModel {
	
	private TemplateGenerator templateGenerator;

	
	
	public TemplateModel(TemplateGenerator templateGenerator) {
		super();
		this.templateGenerator = templateGenerator;
	}



	public TemplateModel() {
		super();
	}

	
	
}
