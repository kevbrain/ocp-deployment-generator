package com.its4u.buildfactory.ocp.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Secrets extends ConfigResource{
	
	public Secrets(int secretID,String appName) {
		super(secretID,"-secret-"+secretID,new HashMap<String,String>());
	}
		
}
