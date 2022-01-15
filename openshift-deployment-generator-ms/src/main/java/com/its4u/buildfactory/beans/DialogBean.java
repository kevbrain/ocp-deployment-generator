package com.its4u.buildfactory.beans;

import org.primefaces.model.TreeNode;
import org.springframework.stereotype.Component;

import com.its4u.buildfactory.maven.resources.MavenModel;
import com.its4u.buildfactory.ocp.resources.TemplateGenerator;
import com.its4u.buildfactory.ocp.resources.TemplateResource;

import lombok.Data;

@Data
@Component
public class DialogBean {
	
	private String header;
	
	private String content;

}
