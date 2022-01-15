package com.its4u.buildfactory.ocp.infra;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import io.fabric8.kubernetes.api.model.PodList;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NodeOcp {

	private String name;
	
	private PodList podList;
	
	private BigDecimal allocatable_cpu;
	
	private BigDecimal allocatable_memory;
	
	private BigDecimal limits_cpu = BigDecimal.ZERO;
	private BigDecimal limits_memory = BigDecimal.ZERO;
	
	private BigDecimal requests_cpu= BigDecimal.ZERO;
	private BigDecimal requests_memory= BigDecimal.ZERO;
	
	private int nbrPods;

	public NodeOcp(String name) {
		super();
		this.name = name;
	}
	
	
	
}
