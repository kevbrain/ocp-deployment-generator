package com.its4u.buildfactory.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.its4u.buildfactory.model.placeholders.Environments;
import com.its4u.buildfactory.model.placeholders.PlaceHolderId;
import com.its4u.buildfactory.model.placeholders.PlaceHolders;
import com.its4u.buildfactory.model.placeholders.Project;
import com.its4u.buildfactory.ocp.resources.ConfigMap;
import com.its4u.buildfactory.ocp.resources.Secrets;



@Service
public class PlaceHolderManagerService {

	public Project createProject(String projectName,String gitURl,List<ConfigMap> cms,List<Secrets> secrets,HashMap<String,Boolean> environments) {
		Project project = new Project(projectName, gitURl, "Kevyn");
		List<Environments> envsProject =  new ArrayList<>();
		for (String keyenv:environments.keySet()) {
			if (environments.get(keyenv)) {
				System.out.println("Create Environment : "+keyenv);
				Environments env = new Environments(project,projectName+"-"+keyenv);
				env.setPlaceholders(createplaceHoldersForEnv(env,cms,secrets));
				envsProject.add(env);
			}
		}
		project.setEnvironments(envsProject);
		return project;
	}
	
	public List<PlaceHolders> createplaceHoldersForEnv(Environments env,List<ConfigMap> cms,List<Secrets> secrets) {
		List<PlaceHolders> placeHolders = new ArrayList<>();
		for (ConfigMap cm:cms) {
			for (String cmKey :cm.getKeyValue().keySet()) {
				placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),cmKey),env,cm.getKeyValue().get(cmKey),""));
			}
		}
		for (Secrets secret:secrets) {
			for (String secretKey :secret.getKeyValue().keySet()) {
				placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),secretKey),env,secret.getKeyValue().get(secretKey),"secret"));
			}
		}
		return placeHolders;
	}
	
	public Project createBetaProject() {
		String projectName = "suisse";
		Project project = new Project(projectName, null, "Kevyn");
		List<Environments> envs =  new ArrayList<>();
		
		Environments dev = new Environments(project,projectName+"-dev");
		Environments tst = new Environments(project,projectName+"-tst");
		Environments inte = new Environments(project,projectName+"-int");
				
		dev.setPlaceholders(createplaceHolders(dev));
		tst.setPlaceholders(createplaceHolders(tst));
		inte.setPlaceholders(createplaceHolders(inte));
		
		envs.add(dev);
		envs.add(tst);
		envs.add(inte);
				
		project.setEnvironments(envs);
		return project;
	}
	
	public List<PlaceHolders> createplaceHolders(Environments env) {
		List<PlaceHolders> placeHolders = new ArrayList<>();
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"welcome.message"),env,"Hello",""));
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"ocp.replicas"),env,"2",""));
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"application.title"),env,"My application",""));
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"database.password"),env,"systempassword","secret"));
		return placeHolders;
	}
}
