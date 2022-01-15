package com.its4u.buildfactory.ocp.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConfigMap extends ConfigResource {
	
	public ConfigMap(int configMapID,String appName) {
		super(configMapID,"-cm-"+configMapID,new HashMap<String,String>());
	}

}
