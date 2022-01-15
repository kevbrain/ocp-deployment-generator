package com.its4u.buildfactory.model;

import java.util.HashMap;
import java.util.List;

import org.primefaces.model.file.UploadedFile;

import com.its4u.buildfactory.ocp.resources.Mounting;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FilesToAnalyse {
	
	public static final String SCRIPT_FILE= "Script file";
	
	public static final String PROPERTY_FILE= "Properties file";

	private String fileName;
	
	private String content;
	
	private String type;
	
}
