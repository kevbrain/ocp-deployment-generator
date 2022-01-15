package com.its4u.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateResource {

	private String name;
	
	private String resourceAsString;
	
	private int createOrder;
	
	private int modifyOrder;
	
	private int destroyOrder;
	
	
	public TemplateResource(String name, String resourceAsString,int createOrder,int modifyOrder,int destroyOrder) {
		super();
		this.name = name;
		this.resourceAsString = resourceAsString;
		this.createOrder = createOrder;
		this.modifyOrder = modifyOrder;
		this.destroyOrder = destroyOrder;
	}
	
	public String getTypeName() {
		return StringUtils.replace(name, ".yml", "");
	}
	
	
}
