package com.bil.buildfactory.ocp.resources;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;

@Data
public class TemplateGenerator {


    private final Template template_all_in_one;
    
    private final Template template_configMaps;
    
    private final Template template_secrets;
    
    private final Template template_deployment;
    
    private final Template template_service;
    
    private final Template template_route;
    
    private final Template template_serviceAccount;
    
    private final Template template_scc;
    
    private final Template template_pvc;
    
    private final Template template_implementation_allInOne;
    
    private final Template template_implementation_splited;
    
    public List<TemplateResource> generatedResources = new ArrayList<>();

    public TemplateGenerator(String pathTemplate ) throws IOException {    	
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        
        cfg.setDirectoryForTemplateLoading(new File(pathTemplate));
        template_all_in_one = cfg.getTemplate("deployment.yaml");
        template_configMaps = cfg.getTemplate("deployment-configMaps.yaml");
        template_secrets = cfg.getTemplate("deployment-secrets.yaml");
        template_deployment = cfg.getTemplate("deployment-deployment.yaml");
        template_service = cfg.getTemplate("deployment-service.yaml");
        template_route = cfg.getTemplate("deployment-route.yaml");
        template_serviceAccount = cfg.getTemplate("deployment-serviceAccount.yaml");
        template_scc = cfg.getTemplate("deployment-scc.yaml");
        template_pvc = cfg.getTemplate("deployment-pvc.yaml");
        template_implementation_allInOne = cfg.getTemplate("implementation-dar.yaml");
        template_implementation_splited = cfg.getTemplate("implementation-split-dar.yaml");
        this.generatedResources = new ArrayList<>();
    }

    public TemplateResource generateAllInOne(DeploymentModel model) throws IOException, TemplateException {
    	TemplateResource allInOne = new TemplateResource("Deployment-all.yml",generateDeployment(model,template_all_in_one),60,50,40);
    	return allInOne;
    }
    public TemplateResource generateImplemenatationAllInOne(DeploymentModel model) throws IOException, TemplateException {
    	TemplateResource allInOne = new TemplateResource("pom-dar.xml",generateDeployment(model,template_implementation_allInOne),0,0,0);
    	return allInOne;
    }
    
    public List<TemplateResource> generateAllDeployments(DeploymentModel model) throws IOException, TemplateException {

    	this.generatedResources = new ArrayList<>();

    	if (model.getServiceAccount()!=null) {
    		TemplateResource serviceAccount = new TemplateResource("ServiceAccount.yml",generateDeployment(model,template_serviceAccount),10,10,55);
    		generatedResources.add(serviceAccount);
    		TemplateResource scc = new TemplateResource("SecurityContextConstraint.yml",generateDeployment(model,template_scc),0,0,0);
    		generatedResources.add(scc);
    	}
    	
    	TemplateResource configMaps = new TemplateResource("ConfigMaps.yml",generateDeployment(model,template_configMaps),20,20,50);
    	TemplateResource secrets = new TemplateResource("Secrets.yml",generateDeployment(model,template_secrets),25,25,25);
    	TemplateResource deployment = new TemplateResource("Deployment.yml",generateDeployment(model,template_deployment),50,50,10);
    	TemplateResource service = new TemplateResource("Service.yml",generateDeployment(model,template_service),55,55,20);
    	
    	
    	
    	generatedResources.add(configMaps);
    	generatedResources.add(secrets);
    	generatedResources.add(deployment);
    	generatedResources.add(service);
    	
    	if (!model.getRoutes().isEmpty()) {
    		TemplateResource route = new TemplateResource("Routes.yml",generateDeployment(model,template_route),60,60,10);
    		generatedResources.add(route);
    	}
    	
    	if (!model.getPersitentVolumes().isEmpty()) {
    		TemplateResource pvc = new TemplateResource("PVClaims.yml",generateDeployment(model,template_pvc),30,30,60);
    		generatedResources.add(pvc);
    	}
    	
    	TemplateResource implementation = new TemplateResource("pom-dar.xml",generateDeployment(model,template_implementation_splited),0,0,0);
    	generatedResources.add(implementation);
    	
        return generatedResources;

    }

    public String generateDeployment(DeploymentModel model,Template template) throws IOException, TemplateException {

        Writer out = new StringWriter();
        template.process(model, out);
        return out.toString();


    }

  
}
