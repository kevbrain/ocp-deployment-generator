package com.bil.buildfactory.ocp.resources;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.bil.buildfactory.maven.resources.MavenModel;
import com.bil.buildfactory.model.TemplateModel;

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
    
    private final Template template_pipeline;
    
    
    private final Template template_maven_application;
    
    private final Template template_maven_classpath;
    
    private final Template template_maven_lombok;
    
    private final Template template_maven_pom;
    
    private final Template template_maven_application_properties;
    
    private final Template template_maven_readMe;
    
    public List<TemplateResource> generatedResources = new ArrayList<>();

    public TemplateGenerator(String pathTemplate ) throws IOException {    	
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        
        // ocp resources templates
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
        template_pipeline = cfg.getTemplate("pipeline.yaml");
        
        // maven resources templates
        cfg.setDirectoryForTemplateLoading(new File(pathTemplate+"//maven"));
        template_maven_application = cfg.getTemplate("application");
        template_maven_classpath = cfg.getTemplate("classpath");
        template_maven_lombok = cfg.getTemplate("lombok.config");
        template_maven_pom = cfg.getTemplate("pom.xml");
        template_maven_application_properties = cfg.getTemplate("application-properties");
        template_maven_readMe = cfg.getTemplate("README-md");
        this.generatedResources = new ArrayList<>();
    }

    
    // generate ocp resources
    public TemplateResource generateAllInOne(DeploymentModel model) throws IOException, TemplateException {
    	TemplateResource allInOne = new TemplateResource("Deployment-all.yml",generateResourceWithTemplate(model,template_all_in_one),60,50,40);
    	return allInOne;
    }
    public TemplateResource generateImplemenatationAllInOne(DeploymentModel model) throws IOException, TemplateException {
    	TemplateResource allInOne = new TemplateResource("pom-dar.xml",generateResourceWithTemplate(model,template_implementation_allInOne),0,0,0);
    	return allInOne;
    }
    
    public List<TemplateResource> generateAllDeployments(DeploymentModel model) throws IOException, TemplateException {

    	this.generatedResources = new ArrayList<>();

    	if (model.getServiceAccount()!=null) {
    		TemplateResource serviceAccount = new TemplateResource("ServiceAccount.yml",generateResourceWithTemplate(model,template_serviceAccount),10,10,55);
    		generatedResources.add(serviceAccount);
    		TemplateResource scc = new TemplateResource("SecurityContextConstraint.yml",generateResourceWithTemplate(model,template_scc),0,0,0);
    		generatedResources.add(scc);
    	}
    	
    	TemplateResource configMaps = new TemplateResource("ConfigMaps.yml",generateResourceWithTemplate(model,template_configMaps),20,20,50);
    	TemplateResource secrets = new TemplateResource("Secrets.yml",generateResourceWithTemplate(model,template_secrets),25,25,25);
    	TemplateResource deployment = new TemplateResource("Deployment.yml",generateResourceWithTemplate(model,template_deployment),50,50,10);
    	TemplateResource service = new TemplateResource("Service.yml",generateResourceWithTemplate(model,template_service),55,55,20);
    	TemplateResource pipeline = new TemplateResource("pipeline.yml",generateResourceWithTemplate(model,template_pipeline),0,0,0);
    	
    	
    	generatedResources.add(configMaps);
    	generatedResources.add(secrets);
    	generatedResources.add(deployment);
    	generatedResources.add(service);
    	generatedResources.add(pipeline);
    	
    	if (!model.getRoutes().isEmpty()) {
    		TemplateResource route = new TemplateResource("Routes.yml",generateResourceWithTemplate(model,template_route),60,60,10);
    		generatedResources.add(route);
    	}
    	
    	if (!model.getPersitentVolumes().isEmpty()) {
    		TemplateResource pvc = new TemplateResource("PVClaims.yml",generateResourceWithTemplate(model,template_pvc),30,30,60);
    		generatedResources.add(pvc);
    	}
    	
    	//TemplateResource implementation = new TemplateResource("pom-dar.xml",generateResourceWithTemplate(model,template_implementation_splited),0,0,0);
    	//generatedResources.add(implementation);
    	
        return generatedResources;

    }
    
    
    // generate maven resources
    
    

    public String generateResourceWithTemplate(TemplateModel model,Template template) throws IOException, TemplateException {

        Writer out = new StringWriter();
        template.process(model, out);
        return out.toString();

    }
    
    

  
}
