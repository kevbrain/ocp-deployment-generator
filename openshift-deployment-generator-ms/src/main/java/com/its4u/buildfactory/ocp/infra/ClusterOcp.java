package com.its4u.buildfactory.ocp.infra;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClusterOcp {

	private String name;
	
	private List<NodeOcp> nodes;
	
	private List<NamespaceOcp> namespaceOcp;

	public ClusterOcp(String name) {
		super();
		this.name = name;
		this.nodes = new ArrayList<>();
		this.namespaceOcp = new ArrayList<>();
	}
	
	
}
