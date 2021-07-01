package com.bil.buildfactory.beans;


import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class OcpInitializerBean {

	private String namespace="lab-workspace";
	
	private String registry="image-registry.openshift-image-registry.svc.cluster.local:5000";
	
}
