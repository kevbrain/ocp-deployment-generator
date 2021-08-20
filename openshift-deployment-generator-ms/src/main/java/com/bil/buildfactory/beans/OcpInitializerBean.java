package com.bil.buildfactory.beans;


import java.util.Map;

import org.springframework.stereotype.Component;

import com.bil.buildfactory.ocp.resources.InstanceOpenShift;
import com.bil.buildfactory.ocp.resources.OpenShiftProject;

import lombok.Data;

@Data
@Component
public class OcpInitializerBean {

	
	private String server="https://api.ocp-lab.its4u.eu:6443";
	
	private String namespace="lab-workspace";
	
	private String registry="image-registry.openshift-image-registry.svc.cluster.local:5000";
	
	private String token="";
	
	public void testConnection() {
		System.out.println("TEST CONNECTION TO OCP");
		System.out.println("token = "+token);
		InstanceOpenShift instanceOpenShift = new InstanceOpenShift(server,token);
		Map<String, OpenShiftProject> projects = instanceOpenShift.getMapProject();
		System.out.println(projects);
	}
	
}
