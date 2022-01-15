package com.its4u.buildfactory.beans;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.its4u.buildfactory.ocp.infra.ClusterOcp;
import com.its4u.buildfactory.ocp.infra.NamespaceOcp;
import com.its4u.buildfactory.ocp.infra.NodeOcp;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.api.model.ResourceQuotaList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Data;

@Data
@Component
public class OcpInitializerBean {

	
	private String server="https://api.ocp-lab.its4u.eu:6443";
	
	private String namespace="";
	
	private String registry="image-registry.openshift-image-registry.svc.cluster.local:5000";
	
	private String token="";
	
	private KubernetesClient client;
	
	private ClusterOcp clusterOcp;
	
	private NodeList nodeslist;
	
	private HashMap<String,Boolean> namespaces ;
	
	private boolean dev_env=true;
	private boolean tst_env=false;
	private boolean int_env=false;
	
	
	@PostConstruct
	public void init() {
    	this.namespaces = new HashMap<String, Boolean>();
    	this.namespaces.put("dev", this.dev_env);
    	this.namespaces.put("tst", this.tst_env);
    	this.namespaces.put("int", this.int_env);
 
	}
	
	
	
	public void testConnection() {
		System.out.println("TEST CONNECTION TO OCP");
		System.out.println("token = "+token);
		checkCapacityCluster();
	}
	
	public void checkCapacityCluster() {
		Config config = new ConfigBuilder().withMasterUrl("https://api.ocp-lab.its4u.eu:6443")
                .withDisableHostnameVerification(true)
                .withOauthToken(token)
                .withTrustCerts(false).build();

		client = new DefaultKubernetesClient(config);
		clusterOcp = new ClusterOcp("ocp");
		
		loadWorkers();
		loadProjets();
		System.out.println("Workers loaded "+clusterOcp.getNodes().size());
		System.out.println("Projects loaded "+clusterOcp.getNamespaceOcp().size());
		
	}
	
	private void loadProjets() {
		NamespaceList namespaceList = client.namespaces().list();
		for (Namespace nsp: namespaceList.getItems()) {
			String name= nsp.getMetadata().getName();
			NamespaceOcp namespaceOcp = new NamespaceOcp(name);
						
			PodList podList = client.pods().inNamespace(name).list();
			namespaceOcp.setPodList(podList);
			namespaceOcp.setPodCount(podList.getItems().size());
			
			ResourceQuotaList resourceQuotaList = client.resourceQuotas().inNamespace(name).list();
			for (ResourceQuota quota: resourceQuotaList.getItems()) {
				System.out.println(quota.getSpec().getHard());
				System.out.println(quota.getStatus());
			}
			
			clusterOcp.getNamespaceOcp().add(namespaceOcp);
		}
	}
	
	

	private void loadWorkers() {
		NodeList nodeslist = client.nodes().withLabel("node-role.kubernetes.io/worker","").withoutLabel("node-role.kubernetes.io/infra","").withoutLabel("cluster.ocs.openshift.io/openshift-storage","").list();
		
	
		for (Node node: nodeslist.getItems()) { 
			
		    NodeStatus nodeStatus = node.getStatus();
			String hostname = node.getMetadata().getLabels().get("kubernetes.io/hostname");
			NodeOcp nodeOcp= new NodeOcp(hostname);
			clusterOcp.getNodes().add(new NodeOcp(hostname));
			
		    nodeOcp.setAllocatable_cpu(new BigDecimal(nodeStatus.getAllocatable().get("cpu").getAmount()));
		    nodeOcp.setAllocatable_memory(new BigDecimal(nodeStatus.getAllocatable().get("memory").getAmount()).divide(new BigDecimal(1024)));
							
			PodList podList = client.pods().inAnyNamespace().withField("spec.nodeName", hostname).withField("status.phase", "Running").list();
			nodeOcp.setPodList(podList);
			nodeOcp.setNbrPods(podList.getItems().size());
						
			for (Pod pod: podList.getItems()) {
				for (io.fabric8.kubernetes.api.model.Container container: pod.getSpec().getContainers()) {
					if(container.getResources().getLimits()!=null ) {
						for (String typeLimit : container.getResources().getLimits().keySet()) {
							if (typeLimit=="cpu") {
								
								
								if (container.getResources().getLimits().get("cpu").getFormat().equalsIgnoreCase("m")) {
									
									nodeOcp.setLimits_cpu(nodeOcp.getLimits_cpu().add(new BigDecimal(container.getResources().getLimits().get("cpu").getAmount())));
									
								} else {					
								
									nodeOcp.setLimits_cpu(nodeOcp.getLimits_cpu().add(new BigDecimal(container.getResources().getLimits().get("cpu").getAmount()).multiply(new BigDecimal(1000))));
								}
							}
							if (typeLimit=="memory") {
								
								
								if (container.getResources().getLimits().get("memory").getFormat().equalsIgnoreCase("Gi")){
					
									nodeOcp.setLimits_memory(nodeOcp.getLimits_memory().add(new BigDecimal(container.getResources().getLimits().get("memory").getAmount()).multiply(new BigDecimal(1024))));
								} else {
								
									nodeOcp.setLimits_memory(nodeOcp.getLimits_memory().add(new BigDecimal(container.getResources().getLimits().get("memory").getAmount())));
								}
							}
						}
					}
					if(container.getResources().getRequests()!=null ) {
						for (String typeRequest : container.getResources().getRequests().keySet()) {
							if (typeRequest=="cpu") {
								nodeOcp.setRequests_cpu(nodeOcp.getRequests_cpu().add(new BigDecimal(container.getResources().getRequests().get("cpu").getAmount())));
							}
							if (typeRequest=="memory") {
								nodeOcp.setRequests_memory(nodeOcp.getRequests_memory().add(new BigDecimal(container.getResources().getRequests().get("memory").getAmount())));
							}
						}
					}	
					
				}
			}

		}
		

	}
	
	
	
}
