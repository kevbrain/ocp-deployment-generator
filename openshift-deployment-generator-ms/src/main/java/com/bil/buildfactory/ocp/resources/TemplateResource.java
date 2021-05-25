package com.bil.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateResource {

	private String type;
	
	private String resourceYaml;
	
	private int createOrder;
	
	private int modifyOrder;
	
	private int destroyOrder;
	
	
	public TemplateResource(String type, String resourceYaml,int createOrder,int modifyOrder,int destroyOrder) {
		super();
		this.type = type;
		this.resourceYaml = resourceYaml;
		this.createOrder = createOrder;
		this.modifyOrder = modifyOrder;
		this.destroyOrder = destroyOrder;
	}
	
	public String getTypeName() {
		return StringUtils.replace(type, ".yml", "");
	}
	
	
}
