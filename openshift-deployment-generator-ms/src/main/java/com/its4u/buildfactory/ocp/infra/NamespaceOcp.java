package com.its4u.buildfactory.ocp.infra;


import io.fabric8.kubernetes.api.model.PodList;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NamespaceOcp {

	
	private String name;
	
	private boolean managedByQuotas;
	
	private PodList podList;
	
	private int podCount;

	public NamespaceOcp(String name) {
		super();
		this.name = name;
	}
	
	
	
}
