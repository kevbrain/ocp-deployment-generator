package com.its4u.buildfactory.ocp.resources;

import java.util.HashMap;
import java.util.List;

import org.primefaces.model.file.UploadedFile;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Volumes {
	
	private String id;
	
	private String name;
	
	private String type;
	
	private PersistentVolumeClaim claim;
	
	private String path;
	
	private String subPath;
	
	private String container;
	
	private String configMapName;
	
	private ConfigMap configMap;
	
	private HashMap<String,UploadedFile> cmVol;
	
	private Nas nas;
	
	

	public Volumes(String id) {
		super();		
		this.id = id;
	}

	public Volumes(String id,String name, String type, String path) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.path = path;
	}

	public boolean isEmptyDir() {
		return type!=null && type.equalsIgnoreCase("EmptyDir");
	}

		
	public boolean isPersistent() {
		return type!=null && type.equalsIgnoreCase("Persistent");
	}

	public boolean isANas() {
		return type!=null && type.equalsIgnoreCase("NAS");
	}



	
	
	
		

}
