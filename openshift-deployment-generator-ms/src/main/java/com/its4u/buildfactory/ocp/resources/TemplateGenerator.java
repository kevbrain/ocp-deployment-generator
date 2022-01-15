package com.its4u.buildfactory.ocp.resources;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.its4u.buildfactory.maven.resources.MavenModel;
import com.its4u.buildfactory.model.TemplateModel;

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
    
    private final Template template_argo_application;
    
    private final Template template_argo_kustomization;
    
    private final Template template_namespace;
    
    private final Template template_rolebinding;
    
    private final Template template_cm_maven;
    
    private final Template template_pvc_pipeline;
    
    private final Template template_pipelineTrigger;
    	
    private final Template template_pipelineTriggerBinding;
           
    private final Template template_pipelineTriggerTemplate;
    
    private final Template template_pipelineEventListener;
    
    private final Template template_pipelineEventListenerRoute;
    
    private final Template template_mainPageHtml;
    
    private final Template template_welcomePageRedirection;
    
    private TemplateResource appArgo;
    
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
        template_namespace= cfg.getTemplate("namespace.yaml");
        template_rolebinding= cfg.getTemplate("rolebinding.yaml");
        template_cm_maven=cfg.getTemplate("maven-cm.yaml");
        template_pvc_pipeline=cfg.getTemplate("pvc-claim-pipeline.yaml");
                
        // pipeline
        template_pipeline = cfg.getTemplate("pipeline.yaml");
        template_pipelineTrigger = cfg.getTemplate("pipeline-trigger.yaml");
        template_pipelineTriggerBinding = cfg.getTemplate("pipeline-triggerBinding.yaml");
        template_pipelineTriggerTemplate = cfg.getTemplate("pipeline-triggerTemplate.yaml");
        template_pipelineEventListener = cfg.getTemplate("pipeline-eventListener.yaml");
        template_pipelineEventListenerRoute  = cfg.getTemplate("pipeline-eventListenerRoute.yaml");
        
        // gitops argo
        template_argo_application = cfg.getTemplate("argo-application.yaml");
        template_argo_kustomization = cfg.getTemplate("argo-kustomization.yaml");
        
        // maven resources templates
        cfg.setDirectoryForTemplateLoading(new File(pathTemplate+"//maven"));
        template_maven_application = cfg.getTemplate("application");
        template_maven_classpath = cfg.getTemplate("classpath");
        template_maven_lombok = cfg.getTemplate("lombok.config");
        template_maven_pom = cfg.getTemplate("pom-template.xml");
        template_maven_application_properties = cfg.getTemplate("application-properties");
        template_maven_readMe = cfg.getTemplate("README-md");
        this.generatedResources = new ArrayList<>();
        
        // joinfaces
        cfg.setDirectoryForTemplateLoading(new File(pathTemplate+"//joinfaces"));
        template_mainPageHtml = cfg.getTemplate("main-page.xhtml");
        template_welcomePageRedirection = cfg.getTemplate("WelcomePageRedirect.template");
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
    	
    	String argoNameApp = "argoApp-"+model.getAppName()+".yaml";

    	if (model.getServiceAccount()!=null) {
    		TemplateResource serviceAccount = new TemplateResource("ServiceAccount-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_serviceAccount),10,10,55);
    		generatedResources.add(serviceAccount);
    		TemplateResource scc = new TemplateResource("SecurityContextConstraint-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_scc),0,0,0);
    		generatedResources.add(scc);
    	}
    	
    	TemplateResource configMaps = new TemplateResource("ConfigMaps-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_configMaps),20,20,50);
    	TemplateResource secrets = new TemplateResource("Secrets-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_secrets),25,25,25);
    	TemplateResource deployment = new TemplateResource("Deployment-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_deployment),50,50,10);
    	TemplateResource service = new TemplateResource("Service-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_service),55,55,20);
    	
    	TemplateResource pipeline = new TemplateResource("pipeline.yml",generateResourceWithTemplate(model,template_pipeline),0,0,0);
    	TemplateResource pipelineTrigger = new TemplateResource("pipeline-trigger.yml",generateResourceWithTemplate(model,template_pipelineTrigger),0,0,0);
    	TemplateResource pipelineTriggerTemplate = new TemplateResource("pipeline-triggerTemplate.yml",generateResourceWithTemplate(model,template_pipelineTriggerTemplate),0,0,0);
    	TemplateResource pipelineTriggerBinding = new TemplateResource("pipeline-triggerBinding.yml",generateResourceWithTemplate(model,template_pipelineTriggerBinding),0,0,0);
    	TemplateResource pipelineEventListener = new TemplateResource("pipeline-eventListener.yml",generateResourceWithTemplate(model,template_pipelineEventListener),0,0,0);
    	TemplateResource pipelineEventListenerRoute = new TemplateResource("pipeline-eventListenerRoute.yml",generateResourceWithTemplate(model,template_pipelineEventListenerRoute),0,0,0);
    	    	
    	TemplateResource namespace = new TemplateResource("namespace-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_namespace),0,0,0);
    	TemplateResource rolebinding= new TemplateResource("rolebinding-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_rolebinding),0,0,0);
    	TemplateResource mavensetting= new TemplateResource("mavensetting-cm-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_cm_maven),0,0,0);
    	TemplateResource pvcPipeline= new TemplateResource("pvc-claim-pipeline"+".yml",generateResourceWithTemplate(model,template_pvc_pipeline),0,0,0);
    	appArgo = new TemplateResource(argoNameApp,generateResourceWithTemplate(model,template_argo_application),0,0,0);
    	
    	TemplateResource argoKustomization = new TemplateResource("kustomization.yaml",generateResourceWithTemplate(model,template_argo_kustomization),0,0,0);
    	
    	generatedResources.add(argoKustomization);
    	generatedResources.add(appArgo);
    	    	
    	generatedResources.add(namespace);
    	//generatedResources.add(rolebinding);
    	       
    	generatedResources.add(configMaps);
    	generatedResources.add(secrets);
    	generatedResources.add(deployment);
    	generatedResources.add(service);
    	
    	// pipeline build , only on dev
    	if (model.getEnv().equalsIgnoreCase("dev")) {
    		generatedResources.add(pipeline);
    		generatedResources.add(pvcPipeline);
    		generatedResources.add(pipelineTrigger);
    		generatedResources.add(pipelineTriggerTemplate);
    		generatedResources.add(pipelineTriggerBinding);
    		generatedResources.add(pipelineEventListener); 
    		generatedResources.add(pipelineEventListenerRoute);
    		generatedResources.add(mavensetting);
    	}
    	
    	if (!model.getRoutes().isEmpty()) {
    		TemplateResource route = new TemplateResource("Routes-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_route),60,60,10);
    		generatedResources.add(route);
    	}
    	
    	if (!model.getPersitentVolumes().isEmpty()) {
    		TemplateResource pvc = new TemplateResource("PVClaims-"+model.getEnv()+".yml",generateResourceWithTemplate(model,template_pvc),30,30,60);
    		generatedResources.add(pvc);
    	}
 
    	
    	    	
    	//TemplateResource implementation = new TemplateResource("pom-dar.xml",generateResourceWithTemplate(model,template_implementation_splited),0,0,0);
    	//generatedResources.add(implementation);
    	
        return generatedResources;

    }
    
       

    public String generateResourceWithTemplate(TemplateModel model,Template template) throws IOException, TemplateException {

        Writer out = new StringWriter();
        template.process(model, out);
        return out.toString();

    }
    
    

  
}
