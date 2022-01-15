package com.its4u.buildfactory.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.its4u.buildfactory.maven.resources.ProjectArborescenceItem;
import com.its4u.buildfactory.model.FilesToAnalyse;
import com.its4u.buildfactory.ocp.resources.ConfigMap;
import com.its4u.buildfactory.ocp.resources.ConfigResource;
import com.its4u.buildfactory.ocp.resources.Container;
import com.its4u.buildfactory.ocp.resources.DeploymentModel;
import com.its4u.buildfactory.ocp.resources.Mounting;
import com.its4u.buildfactory.ocp.resources.Nas;
import com.its4u.buildfactory.ocp.resources.PersistentVolumeClaim;
import com.its4u.buildfactory.ocp.resources.Route;
import com.its4u.buildfactory.ocp.resources.Secrets;
import com.its4u.buildfactory.ocp.resources.ServiceAccount;
import com.its4u.buildfactory.ocp.resources.TemplateGenerator;
import com.its4u.buildfactory.ocp.resources.TemplateResource;
import com.its4u.buildfactory.ocp.resources.Volumes;

import freemarker.template.TemplateException;
import lombok.Data;



@Data
@Component

public class ViewInitializerBean {
	
	@Autowired
    private GitInitializerBean gitInitializerBean;
	
	@Autowired
	private MavenInitializerBean mavenInitializerBean;
	
	@Autowired
    private OcpInitializerBean ocpInitializerBean;
	
	private static Logger logger = LoggerFactory.getLogger(ViewInitializerBean.class);
	
	private boolean showUploadFile;
	
	private String appName;
	
	private String namespace;
	
	private Set<String> allKeys;
	
	private Set<String> availableKeys;
	
	private HashMap<String,String> availableKeysAndValues;
	
	private Set<String> commonKeys;
	
	private String selectedKey;
	
	private List<String> selectedKeys;				
 	
	private List<String> envDroppedKey;
	
	private List<String> cmDroppedKey;
	
	private List<String> secretsDroppedKey;
	
	private List<Container> containers;
	
	private List<ConfigMap> configMaps;
	
	private List<ConfigMap> configMapsAsvol;
	
	private List<Secrets> secrets;
	
	private int indexContainer;
	
	private int indexVolume;
	
	private int indexNas;
	
	private int activeIndex;
	
	public List<Volumes> volumes;
	
	private DeploymentModel model = new DeploymentModel();

	UploadedFiles file;
	
	UploadedFiles fileForCm;
	
	
	private int nbrContainer;
	private int nbrConfigMap;
	private int nbrSecrets;
		
	private String cmItemKeyToDelete;
	private int cmIdOfKeyToDelete;
	private String newConfigMapVol;
	
	private Volumes selectedVol;
	
	public List<String> pathVolMount;
	
	private TemplateResource templateAllinOne;
	
	private TemplateResource implementation_allInOne;
	
	private List<TemplateResource> generatedTemplatesResources;
	
	private boolean routeExposed;
	
	private boolean runAsUser;
	
	private boolean splitDeployment;
	
	private List<Route> routes;
	
	private List<ServiceAccount> servicesAccounts;
	
	private String hostRouteName;
	
	private StreamedContent fileDeployment;
	
	private StreamedContent zipDeployment;
	
	private List<FilesToAnalyse> filesForAnalyse;
	
	private String user;
	
	private String userUid;
	
	private String userGid;
	
	private TemplateGenerator generator;
	
	@Value("${path.template}")
	private String pathTemplate;
	

	
	private boolean disablePublish;
	
	
	
    @PostConstruct
    public void init()  {
    	reset();    	

    }
    
    public void reset() {
    	this.appName="";
    	this.disablePublish=true;
    	this.showUploadFile=true;    	
    	this.cmDroppedKey = new ArrayList<>();
    	this.secretsDroppedKey= new ArrayList<>();
    	this.envDroppedKey= new ArrayList<>();
    	this.availableKeys= new HashSet<>();
    	this.allKeys= new HashSet<>();
    	this.commonKeys= new HashSet<>();
    	this.pathVolMount= new ArrayList<>();
    	this.configMapsAsvol= new ArrayList<>();
    	this.servicesAccounts= new ArrayList<>();    	
    	this.routes=new ArrayList<>();
    	this.nbrContainer=0; 
    	this.nbrConfigMap=0;
    	this.nbrSecrets=0;
    	this.activeIndex=0;
    	this.containers=new ArrayList<>();
    	this.configMaps = new ArrayList<>();
    	this.secrets = new ArrayList<>();
    	this.indexVolume=0;
    	this.volumes= new ArrayList<>();
    	Volumes logs = new Volumes(String.valueOf(this.indexVolume).trim(),"logs","EmptyDir","/Logs");
    	pathVolMount.add("/Logs");
    	this.volumes.add(logs);
    	this.newConfigMapVol=null;
    	this.filesForAnalyse= new ArrayList<>();
    	this.availableKeysAndValues = new HashMap();
    	this.splitDeployment=true;
    	this.userUid=null;
    	this.userGid=null;
    	this.hostRouteName=null;
    	this.runAsUser=false;
    	this.routeExposed=false;

    	initCommonOcp();
    	refreshConfigMapMounting();
    	    	
    }
    
 
    public void handleEnv(String env ) throws IOException, TemplateException {    	
        if (ocpInitializerBean.getNamespaces().get(env)!=null)  {
        	ocpInitializerBean.getNamespaces().put(env, !ocpInitializerBean.getNamespaces().get(env));
    	}     
        
        refreshAllconf();
    }
    
    public void newApp(String projet) throws IOException, TemplateException {
    	
    	appName = appName.toLowerCase();
    	ocpInitializerBean.setNamespace(appName);
    	gitInitializerBean.setGitUrl(appName);
        gitInitializerBean.setGitSubDirectory(appName);
       	mavenInitializerBean.setArtifact(appName);       	
       	refreshAllconf();
    }
    
    public void refreshAllconf() throws IOException, TemplateException {
    	mavenInitializerBean.handleNewMavenProject();       	
       	publishJkube();
       	generateDeployment("dev");
       	if (!(appName.isEmpty()||gitInitializerBean.getGitPassword().isBlank()||gitInitializerBean.getGitPassword().isEmpty())) {
       		disablePublish=false;
       	}
    }
    
    public void initCommonOcp()  {
    	Container commonContainer = new Container(this.nbrContainer,this.appName);
    	commonContainer.setImage("{{ocp-cluster.registry}}/"+this.appName+"-dev/"+this.appName+":@project.version@");
    	this.containers.add(commonContainer);
    	ConfigMap commonOcp = new ConfigMap(this.nbrConfigMap,this.appName);
    	Secrets commonSecret = new Secrets(this.nbrSecrets,this.appName);
    	try {
			List<String> mandatoryKeyListCm = readFileConfig(pathTemplate+"/mandatoryKeyConfigMap.txt");
			List<String> mandatoryKeyListSecrets = readFileConfig(pathTemplate+"/mandatoryKeySecret.txt");
			List<String> placeHoldersToIgnore = readFileConfig(pathTemplate+"/placeHoldersToIgnore.txt");
			for (String key:mandatoryKeyListCm) {
				System.out.println("key : "+key);
				commonOcp.getKeyValue().put(key,"{{"+key+"}}");
				commonKeys.add(key);
				allKeys.add(key);
			}
			for (String key:mandatoryKeyListSecrets) {
				commonSecret.getKeyValue().put(key,"{{"+key+"}}");
				commonKeys.add(key);
				allKeys.add(key);
			}
			for (String key:placeHoldersToIgnore) {
				commonKeys.add(key);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    	
    	this.secrets.add(commonSecret);
    	this.configMaps.add(commonOcp);
    	
    }
    
    public void onKeyDropToEnVar(DragDropEvent ddEvent) {    
    	Pattern containerPattern = Pattern.compile("container:(.*):envVar");
    	Matcher matcher = containerPattern.matcher(ddEvent.getDropId());
    	int containerId = 0;
    	while(matcher.find()){
            containerId=Integer.parseInt(matcher.group(1));
        }    	
    	String keyCm = (String) ddEvent.getData();
    	containers.get(containerId).getEnvVariables().add(keyCm);
    	availableKeys.remove(keyCm);    	
    }
    
    public void moveSelectedKeysToEnVar(int containerID) {
    	for (String keyCm:selectedKeys) {
    		containers.get(containerID).getEnvVariables().add(keyCm);
        	availableKeys.remove(keyCm);
    	}
    }
    
    public void moveSelectedEnVarKeysToDataKey(int containerID) {
     
    	for (String keyCm:containers.get(containerID).getSelectedEnvKeys()) {
    		availableKeys.add(keyCm);
    		containers.get(containerID).getEnvVariables().remove(keyCm);
    	}
    	containers.get(containerID).setSelectedEnvKeys(new ArrayList<String>());
    }
        
    public void selectEnvVar(int containerId,String key) {

    	if (containers.get(containerId).getSelectedEnvKeys()==null) {
    		containers.get(containerId).setSelectedEnvKeys(new ArrayList<String>());    		
    	}
    	containers.get(containerId).getSelectedEnvKeys().add(key);
    	
    }
    
    public void onKeyDropToCm(DragDropEvent ddEvent) {
    	Pattern containerPattern = Pattern.compile("configmaps:(.*):configmap");
    	Matcher matcher = containerPattern.matcher(ddEvent.getDropId());
    	int configMapId = 0;
    	while(matcher.find()){
    		configMapId=Integer.parseInt(matcher.group(1));
        }   
    	String keyCm = (String) ddEvent.getData();
    	String valueKey = availableKeysAndValues.get(keyCm);
    	configMaps.get(configMapId).getKeyValue().put(keyCm,valueKey);
    	availableKeys.remove(keyCm);
    	refreshConfigMapMounting();
    }
    
    
    public void onKeyDropToSecret(DragDropEvent ddEvent) { 
    	Pattern containerPattern = Pattern.compile("secrets:(.*):secret");
    	Matcher matcher = containerPattern.matcher(ddEvent.getDropId());
    	int secretId = 0;
    	while(matcher.find()){
    		secretId=Integer.parseInt(matcher.group(1));
        }  
    	String keyCm = (String) ddEvent.getData();
    	String valueKey = availableKeysAndValues.get(keyCm);
    	secrets.get(secretId).getKeyValue().put(keyCm,valueKey.replace("}}",".b64}}"));
    	availableKeys.remove(keyCm); 
    	refreshConfigMapMounting();
    }
    
    public void moveSelectedKeysToCM(int configMapID) {
    	for (String keyCm:selectedKeys) {
    		String valueKey = availableKeysAndValues.get(keyCm);
    		configMaps.get(configMapID).getKeyValue().put(keyCm,valueKey);    		
        	availableKeys.remove(keyCm);
    	}
    	refreshConfigMapMounting();
    }
             
    public void moveSelectedKeysToSecrets(int secretID) {
    	for (String keyCm:selectedKeys) {
    		String valueKey = availableKeysAndValues.get(keyCm);
    		secrets.get(secretID).getKeyValue().put(keyCm,valueKey.replace("}}",".b64}}"));
        	availableKeys.remove(keyCm);
    	}
    	refreshConfigMapMounting();
    }
    
    public void addContainer() {
    	this.nbrContainer++;   	    	    	
    	this.containers.add(new Container(this.nbrContainer,this.appName));    
    }
    
    public void addVolume() {
    	this.indexVolume++;
    	Volumes newVol = new Volumes(String.valueOf(this.indexVolume).trim());
    	newVol.setName("-vol-"+this.indexVolume);
    	newVol.setCmVol(new HashMap<>());
    	newVol.setType("EmptyDir");
    	this.volumes.add(newVol);
    }
    
    public void handleEvent(Volumes vol) {
    	if (vol.getType().equalsIgnoreCase("Persistent")) {
    		this.newConfigMapVol=null;
    		PersistentVolumeClaim pvc = new PersistentVolumeClaim(vol.getName()+"_claim");
    		pvc.setGbStorage(2);
    		pvc.setAccessModes("ReadWriteMany");
    		pvc.setStorageClassName("managed-nfs-storage");
    		vol.setClaim(pvc);
    	}
    	
    	if (vol.getType().equalsIgnoreCase("NAS")) {
    		Nas nas = new Nas(vol.getId());
    		nas.setServiceAccount(new ServiceAccount());
    		vol.setNas(nas);
    		this.runAsUser=true;
    	}
    	if (vol.getType().equalsIgnoreCase("ConfigMap")&& this.newConfigMapVol!=null && vol.getCmVol()==null) {
    			
    			vol.setCmVol(new HashMap<>());		
    	}
    }
    
    
    
    public void handleVolPath(Volumes vol) {
    }
    
    public void addPathLog(Volumes vol) {
    	this.pathVolMount.add(vol.getPath());
    }
    public void addConfigMap() {
    	this.nbrConfigMap++;
    	configMaps.add(new ConfigMap(this.nbrConfigMap,this.appName));    
    }
    
    public void addSecret() {
    	this.nbrSecrets++;
    	secrets.add(new Secrets(this.nbrSecrets,this.appName));    
    }
    
    public void removeConfigMap(int configMapID) {
    	if(configMapID>0) {
	    	for (String key: configMaps.get(this.nbrConfigMap).getKeyValue().keySet() ) {
	    		availableKeys.add(key);
	    	}
	    	configMaps.remove(configMapID);
	    	this.nbrConfigMap--;
    	}
    }
    
    public void removeCmItem() {

    	configMaps.get(cmIdOfKeyToDelete).getKeyValue().remove(cmItemKeyToDelete);
    }
    
    public void removeContainer(int containerID) {

    	if(containerID>0) {
	    	for (String key: containers.get(this.nbrContainer).getEnvVariables() ) {
	    		availableKeys.add(key);
	    	}
	    	containers.remove(containerID);
	    	this.nbrContainer--;
    	}
    }
    
    public void removeSecret(int secretID) {
    	
    	if(secretID>0) {
	    	for (String key: secrets.get(this.nbrSecrets).getKeyValue().keySet() ) {
	    		availableKeys.add(key);
	    	}
	    	secrets.remove(secretID);
	    	this.nbrSecrets--;
    	}
    }
    
    public void removeVolume(int volumeID) {
    	
    	if(volumeID>0) {
    		
    		volumes.remove(volumeID);
    		this.indexVolume--;
    	}
    	
    	// Reindex volumes
    	int idx=0;
    	for (Volumes vol:volumes) {
    		vol.setId(String.valueOf(idx).trim());
    		vol.setName("-vol-"+String.valueOf(idx).trim());
    		idx++;
    	}
    }
    public void fileUploadListener(FileUploadEvent e) {
    	UploadedFile file = e.getFile();
    	if (file!=null && file.getFileName()!=null) {
	    	FilesToAnalyse f = new FilesToAnalyse();
	    	f.setFileName(file.getFileName());
	    	if (file.getFileName().contains("properties")) {
	    		f.setType(FilesToAnalyse.PROPERTY_FILE);
	    	} else if (file.getFileName().contains(".sh") ) {
	    		f.setType(FilesToAnalyse.SCRIPT_FILE);
	    	}
	    	try {
	    		f.setContent(IOUtils.toString(file.getInputStream(), "UTF-8"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	filesForAnalyse.add(f);
    	
    	} else {
    		logger.debug("no file");
    	}
    }
    
    public void analyseFiles() {
    	for (FilesToAnalyse f: filesForAnalyse) {
    		try {
				analyseFile(f);
			} catch (IOException e1) {
				logger.error(e1.fillInStackTrace().getMessage());
			}			
    	}
	    	
    }
    
    public void addFileToCm(FileUploadEvent e) {
    	UploadedFile fileCmkey = e.getFile();
    	
    	String[] parts = e.getComponent().getParent().getClientId().split(":");    	
    	Volumes vol = volumes.get(Integer.parseInt(parts[1]));
    	ConfigMap cm = null;
    	if (vol.getConfigMap()== null) {
    		nbrConfigMap++;
	    	cm = new ConfigMap(nbrConfigMap,this.appName );
			vol.setConfigMap(cm);
			vol.setConfigMapName(cm.getName());
    	}
    	
    	if (fileCmkey!=null && fileCmkey.getFileName()!=null) {
    		
    		vol.getCmVol().put(fileCmkey.getFileName(), fileCmkey);    		
    		String content = new String(fileCmkey.getContent());
    		Mounting mounting = new Mounting(fileCmkey.getFileName(), content);
    		if (vol.getConfigMap().getSelectedKeyValue()==null) {
    			vol.getConfigMap().setSelectedKeyValue(new ArrayList<>());
    		}
    		vol.getConfigMap().getSelectedKeyValue().add(mounting);    		    		
    	}
    	
    }
    
    public void refreshConfigMapMounting() {
    	for (ConfigResource cgfr:configMaps) {
			generateConfigMap(cgfr);
		}
		for (ConfigResource cgfr:secrets) {
			generateConfigMap(cgfr);
		}
    }
    
    public void previousStep() {
    	if (this.activeIndex>0) this.activeIndex--;
    }
    
    public void goToPage() {
    	System.out.println("activeIndex = "+activeIndex);
    }
    
    public void nextStep() throws IOException, TemplateException {
    	
    	if (this.activeIndex==0) {
    		analyseFiles();
    	}

    	if (this.activeIndex==1) {
    		    		
    		refreshConfigMapMounting();
    	}
    	if (this.activeIndex == 4) {   
    		
    		refreshAllconf();
    	}
    	
    	if (this.activeIndex<6) {
    		this.activeIndex++; 
    	}
    }
    
    public void generateConfigMap(ConfigResource cgfr) {

			cgfr.setSelectedKeyValue(new ArrayList<Mounting>());
			for (String key:cgfr.getKeyValue().keySet()) {
				String[] conts = new String[]{containers.get(0).getName()}; 
				cgfr.getSelectedKeyValue().add(new Mounting(
						key,
						cgfr.getKeyValue().get(key),
						cgfr,
						conts,
						true));
			}    	
    }

    public void onRowSelect(SelectEvent<String> event) {
  
    }
    
    public void onRowEditCmMount(RowEditEvent<Mounting> event) {
 
    }
   
    public void generateDeployment(String env) throws IOException, TemplateException {
    	
    	logger.info("Generate NEW deployment TEMPLATE for : "+appName);
    	this.generator = new TemplateGenerator(pathTemplate);
    	model = new DeploymentModel();
    	model.setEnv(env);
    	model.setAppName(appName);
    	model.setConfigMaps(configMaps);
    	model.setTemplateGenerator(this.generator);
    	model.setOcpNamespace(appName+"-"+env);
    	model.setOcpRegistry(ocpInitializerBean.getRegistry());
    	model.setJoinfaces(mavenInitializerBean.isJoinfaces());
      	
    	if (runAsUser) {    		
	    	model.setServiceAccount(new ServiceAccount(appName+"-sa"));    	
	    	model.setUserUid(this.userUid);
	    	model.setUserGid(this.userGid);
	    	model.setIbmUser(this.user);
    	}
    	configMapsAsvol = new ArrayList<>();
    	List<Volumes> cmVolumes = new ArrayList<>();
    	List<Route> routesDepl= new ArrayList<>();
    	    	
    	for(Volumes vol:volumes) {

    		if (vol.getConfigMap()!=null) {
    			cmVolumes.add(vol);
    			configMapsAsvol.add(vol.getConfigMap());
    		}
    		if (vol.isEmptyDir()) {
    			model.getEmptyDirVolumes().add(vol);    		
    		}
    		if (vol.isPersistent()) {
    			model.getPersitentVolumes().add(vol);
    		}
    		if (vol.isANas()) {
    			model.getNas().add(vol);
    		}
    		
    	}
    	if (routeExposed) {
    		routesDepl.add(new Route(this.hostRouteName));
    	}
    	model.setCmVolumes(cmVolumes);
    	model.setConfigMapsAsvol(configMapsAsvol);
    	model.setSecrets(secrets);
    	model.setRoutes(routesDepl);
        this.templateAllinOne = generator.generateAllInOne(model);
        this.implementation_allInOne = generator.generateImplemenatationAllInOne(model);
        this.generatedTemplatesResources = generator.generateAllDeployments(model);
        
        InputStream targetStream = new ByteArrayInputStream(this.templateAllinOne.getResourceAsString().getBytes());
        fileDeployment = DefaultStreamedContent.builder()
                .name("deployment.yml")
                .contentType("application/yml")
                .stream(() -> targetStream)
                .build();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        for (TemplateResource resourceOcp : generatedTemplatesResources) {
        	ZipEntry entry = new ZipEntry(resourceOcp.getName());
        	entry.setSize(resourceOcp.getResourceAsString().getBytes().length);
        	zos.putNextEntry(entry);
        	zos.write(resourceOcp.getResourceAsString().getBytes());
        	zos.closeEntry();
        }
        
        zos.close();
        InputStream targetStreamSplit = new ByteArrayInputStream(baos.toByteArray());
        zipDeployment = DefaultStreamedContent.builder()
                .name("deployment.zip")
                .contentType("application/zip")
                .stream(() -> targetStreamSplit)
                .build();
        
        //publishJkube();
    }
    
    public void analyseFile(FilesToAnalyse f) throws IOException {
    	
        model.setAppName(this.appName);
    	String inputContent = f.getContent();
    	Pattern runtimeParam = null;
    	Pattern envParam = null;
    	boolean propertyFile = false;
    	if(f.getType().equalsIgnoreCase(FilesToAnalyse.SCRIPT_FILE)) {
	    	runtimeParam = Pattern.compile("-D(.*)=(.*) ");   
	        envParam = Pattern.compile("export (.*)='?\\{\\{(.*)\\}\\}");  
	        propertyFile=false;     
    	} 
    	if(f.getType().equalsIgnoreCase(FilesToAnalyse.PROPERTY_FILE)) {
    		runtimeParam = Pattern.compile("(.*)=\\$\\{(.*)\\}");
    		envParam = Pattern.compile("export (.*)='?\\{\\{(.*)\\}\\}");
    		propertyFile=true;
    	}
    	if (runtimeParam!=null && envParam!=null) {
    		parseFileWithSpecificPattern(inputContent,runtimeParam,envParam,propertyFile);
    	}

    }
    
    private void parseFileWithSpecificPattern(String inputContent,Pattern runtimeParam,Pattern envParam,boolean propertyFile) {

    	Matcher matcher = runtimeParam.matcher(inputContent);
    	
        while(matcher.find()){
        	String keyvalue;
        	if (propertyFile) {
        		keyvalue="{{"+matcher.group(2)+"}}";
        	} else {
        		keyvalue=matcher.group(2);
        	}
            model.getConfig().put(matcher.group(1),keyvalue);
            if (checkIfKeyCanBeAvailable(matcher.group(1))) {
            	availableKeys.add(matcher.group(1));
            	allKeys.add(matcher.group(1));
            	availableKeysAndValues.put(matcher.group(1),keyvalue);
            }
        }

        Matcher matcher3 = envParam.matcher(inputContent);

        while(matcher3.find()){

        	String valueKey=matcher3.group(1).replace('_', '.').toLowerCase();
            model.getSecret().put(valueKey,"{{"+matcher3.group(2)+"}}");
            if (checkIfKeyCanBeAvailable(valueKey)) {
            	availableKeys.add(valueKey);
            	allKeys.add(valueKey);
            	availableKeysAndValues.put(valueKey,"{{"+matcher3.group(2)+"}}");
            }
        }       	
    }
    
    public void prepareInitConfigResource() {
    	List<String> keysToRemove = new ArrayList<>();
        for (String cfg:model.getConfig().keySet()) {
        	if (cfg.toUpperCase().contains("PASSWORD")) {
        		if (!commonKeys.contains(cfg))
        			secrets.get(0).getKeyValue().put(cfg,"{{"+cfg+".b64}}");
        		keysToRemove.add(cfg);
        	}
        }
        for (String cfg:model.getSecret().keySet()) {        	
        	if (cfg.toUpperCase().contains("PASSWORD")) {
        		if (!commonKeys.contains(cfg))
        			secrets.get(0).getKeyValue().put(cfg,"{{"+cfg+".b64}}");
        		keysToRemove.add(cfg);
        	}
        }
        for (String keytoRemove:keysToRemove) {
        	model.getConfig().remove(keytoRemove);
        	availableKeys.remove(keytoRemove);
        }		     
        
        for (String keytoRemove:commonKeys) {
        	model.getConfig().remove(keytoRemove);
        	availableKeys.remove(keytoRemove);
        }      
    }
    
	public void changeValueKey(String key,String value) {

	}
    
    private boolean checkIfKeyCanBeAvailable(String key) {
    	return !(key.contains("{{") || key.contains("}}") || commonKeys.contains(key));
    }
    
    private List<String> readFileConfig(String pathFile) throws IOException {
    	Path path = Paths.get(pathFile);
    	List<String> myvalues = Files.readAllLines(path);
        return myvalues;
    }
   
    public void publishJkube() throws IOException, TemplateException {
    	if (model!=null && model.getAppName()!=null) {
	    	TreeNode jkube = mavenInitializerBean.getJkube();
	    	jkube.setExpanded(true);
	    	HashMap<String,TreeNode> mapTreeNodeEnv = new HashMap<String, TreeNode>();
	    	
	    	for (String keyEnv:ocpInitializerBean.getNamespaces().keySet()) {
	    		if (ocpInitializerBean.getNamespaces().get(keyEnv))
	    			mapTreeNodeEnv.put(keyEnv, new DefaultTreeNode(new ProjectArborescenceItem(model.getAppName()+"-"+keyEnv,"Folder",null),jkube));
	    	}
	    	/*
	    	mapTreeNodeEnv.put("dev", new DefaultTreeNode(new ProjectArborescenceItem("dev","Folder",null),jkube));
	    	mapTreeNodeEnv.put("tst", new DefaultTreeNode(new ProjectArborescenceItem("tst","Folder",null),jkube));
	    	mapTreeNodeEnv.put("int", new DefaultTreeNode(new ProjectArborescenceItem("int","Folder",null),jkube));
	    	*/
	    	TreeNode argo=  mavenInitializerBean.getArgo();
	    	String argoNameApp = "argoApp-"+model.getAppName()+".yaml";
	    	
	    	
	    	for (String keyenv:ocpInitializerBean.getNamespaces().keySet()) {
	 
	    		if (ocpInitializerBean.getNamespaces().get(keyenv)) {
	    			generateDeployment(keyenv);
			    	for (TemplateResource res: generatedTemplatesResources) { 
			    		// Argo only on DEV
			    		if (res.getName().equalsIgnoreCase(argoNameApp) && keyenv.equalsIgnoreCase("dev")) {
			    			TreeNode nodeArgoApp = new DefaultTreeNode("Text",new ProjectArborescenceItem(res.getName(),"Text",res),argo);
			    			gitInitializerBean.setNodeArgoApp(nodeArgoApp);
			    		} else {
			    			TreeNode parent= mapTreeNodeEnv.get(keyenv);
			    			parent.setExpanded(true);
			    			TreeNode newnode = new DefaultTreeNode("Text",new ProjectArborescenceItem(res.getName(),"Text",res),parent);
			    		}
			    	}
	    		}
	    	}
    	}
    }
    
    public String namespaceString(String env) {
		return appName+"-"+env;
	}
    
    
   
}
