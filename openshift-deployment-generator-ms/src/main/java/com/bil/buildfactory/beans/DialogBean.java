package com.bil.buildfactory.beans;

import org.primefaces.model.TreeNode;
import org.springframework.stereotype.Component;

import com.bil.buildfactory.maven.resources.MavenModel;
import com.bil.buildfactory.ocp.resources.TemplateGenerator;
import com.bil.buildfactory.ocp.resources.TemplateResource;

import lombok.Data;

@Data
@Component
public class DialogBean {
	
	private String header;
	
	private String content;

}
