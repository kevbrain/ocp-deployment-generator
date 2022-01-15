package com.its4u.buildfactory.beans;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.ServiceUnavailableException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHHook;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.its4u.buildfactory.maven.resources.ProjectArborescenceItem;
import com.its4u.buildfactory.utils.CopyDir;

import lombok.Data;

@Data
@Component
public class GitInitializerBean {
	
	@Autowired
    private MavenInitializerBean mavenInitializerBean;
	
	private String gitUrlPrefix="https://github.com/kevbrain/";
	
	private String gitUrl = "";
	
	private String gitOpsUrl = "https://github.com/kevbrain/ocp-gitops.git";
	
	private String gitOpsAppsDeployUrl = "https://github.com/kevbrain/ocp-gitops-apps-deploy.git";
	
	private String gitRevision ="master";
	
	private String gitSubDirectory;
	
	private String gitUser="kevbrain";
	
	private String gitPassword= "";
	
	@Value("${path.workspace}")
	private String pathWorkspace;
	
	@Value("${path.resource}")
	private String pathResource;
	
	private TreeNode nodeArgoApp;
	
	public void setGitUrl(String appName) {
		this.gitUrl= gitUrlPrefix+appName+".git";
	}
	
	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	public void createGitAppsDeploy(String project) throws IllegalStateException, GitAPIException, IOException, URISyntaxException {
		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//ocp-gitops-apps-deploy-"+uuid;
		Git git = null;
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
		
		try {	
			git = Git.cloneRepository()
					  .setURI(gitOpsAppsDeployUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {

			git = Git.init().setDirectory(workingDirectory).call();
		}	
		File folder =  new File(path+"//"+project);
		folder.mkdir();
		readNodeMavenProjectAndCreateArtifact(mavenInitializerBean.getJkube(),path+"//"+project);
		git.add().addFilepattern(".").call();

		
		// Now, we do the commit with a message
		
		RevCommit rev =	git.commit().setAuthor("ksc", "ksc@example.com").setMessage("Creation App By OCP - GitOps Application BootStrapper").call();
	
		RemoteAddCommand remoteAddCommand = git.remoteAdd();
	    remoteAddCommand.setName("origin");
	    remoteAddCommand.setUri(new URIish(gitOpsAppsDeployUrl));
	    remoteAddCommand.call();

	    // push to remote:
	    PushCommand pushCommand = git.push();
	    pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser, gitPassword));
	    // you can add more settings here if needed
	    pushCommand.call();
	}
	
	
	public void createArgoApp() throws IllegalStateException, GitAPIException, IOException, URISyntaxException {
		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//ocp-gitops-"+uuid;
		Git git = null;
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
		
		try {	
			git = Git.cloneRepository()
					  .setURI(gitOpsUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {

			git = Git.init().setDirectory(workingDirectory).call();
		}	
		
		
		readNodeMavenProjectAndCreateArtifact(nodeArgoApp,path+"//cluster");
		git.add().addFilepattern(".").call();

		
		// Now, we do the commit with a message
		
		RevCommit rev =	git.commit().setAuthor("ksc", "ksc@example.com").setMessage("Creation App By OCP - GitOps Application BootStrapper").call();
	
		RemoteAddCommand remoteAddCommand = git.remoteAdd();
	    remoteAddCommand.setName("origin");
	    remoteAddCommand.setUri(new URIish(gitOpsUrl));
	    remoteAddCommand.call();

	    // push to remote:
	    PushCommand pushCommand = git.push();
	    pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser, gitPassword));
	    // you can add more settings here if needed
	    pushCommand.call();
		
	}
	
	public void createRepo(String project) throws AbortedByHookException, ConcurrentRefUpdateException, NoHeadException, NoMessageException, ServiceUnavailableException, UnmergedPathsException, WrongRepositoryStateException, GitAPIException, IOException, URISyntaxException, InterruptedException {
		
		UUID uuid = UUID.randomUUID();
		
		this.gitSubDirectory=project;

		boolean deleted = deleteDirectory(new File(pathWorkspace+"/"+project+"-"+uuid));
		System.out.println("workspace deleted : "+deleted);
		
		String path = pathWorkspace+"//"+project+"-"+uuid;
			
		
		this.gitUrl= gitUrlPrefix+project+".git";
		
		
		// clone Git Project
		Git git = null;
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();				
		try {	
			git = Git.cloneRepository()
					  .setURI(gitUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {

			git = Git.init().setDirectory(workingDirectory).call();
		}	
		
			
		// Create project in Git Project
		File newFile = new File(workingDirectory, project);
		newFile.mkdir();
		
			
		// Read Maven Project and Create structure
		TreeNode nodeProject = mavenInitializerBean.getRoot();
		
		readNodeMavenProjectAndCreateArtifact(nodeProject,path);
		
		// if joinfaces project we copy resources to meta-inf
		if (mavenInitializerBean.isJoinfaces()) {
			copyResourcesFromClassPath(pathResource,project,path);
		}
				
		// Now, we do the commit with a message
		
		git.add().addFilepattern(".").call();
		git.commit().setAuthor("ksc", "ksc@example.com").setMessage("Creation App By OCP - GitOps Application BootStrapper").call();
	
		RemoteAddCommand remoteAddCommand = git.remoteAdd();
	    remoteAddCommand.setName("origin");
	    remoteAddCommand.setUri(new URIish(gitUrl));
	    remoteAddCommand.call();

	    // push to remote:
	    PushCommand pushCommand = git.push();
	    pushCommand.add("master");
	    pushCommand.setRemote("origin");
	    pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser, gitPassword));
	    try {		    
		    // you can add more settings here if needed
		    pushCommand.call();
	    } catch (Exception e) {
			System.out.println(e.getCause().getMessage());
			createNewRemoteRepository(project);
		} finally {
			pushCommand.call();
		}
		
	    createArgoApp();
	    createGitAppsDeploy(project);
	}
	
	public void copyResourcesFromClassPath(String pathResource,String project,String pathDest)  {
	    	
			pathDest = pathDest+"//"+project+"//src//main//resources//META-INF//resources";
	    	System.out.println("COPY RESSOURCE FROM CLASSPATH TO "+pathDest);
	    	
			Path pathDestination = Paths.get(pathDest);
	    	Path pathOrigin = Paths.get(pathResource);
	    	try {
				Files.walkFileTree(pathOrigin, new CopyDir(pathOrigin, pathDestination));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    		    
	}
	
	public void copyFile(Path pathSource,String destination, String project) {
		destination = destination+"//"+project+"//src//main//resources//META-INF";
		Path pathDestination = Paths.get(destination);
		Path targetFile = pathDestination.resolve(pathSource.getFileName());
		
		System.out.println("fileName:"+pathSource.getFileName()+"|");
		if (!pathSource.getFileName().toString().equalsIgnoreCase("ocp-deployment-initializer.xhtml")) {
			System.out.println("copy "+pathSource+ " to "+ destination);
			try {
				Files.copy(pathSource, targetFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public void createNewRemoteRepository(String project) throws IOException, InterruptedException {
		System.out.println("*** create new repo ***");
		
		GitHub github = new GitHubBuilder().withOAuthToken(gitPassword).build();
		//github.connect();

		GHRepository repo = github.createRepository(
		  project,"Created By Ocp InitialiZr",
		  "https://github.com/kevbrain/",true/*public*/);
		
		System.out.println("*** Repo created ***");
		
		
		String urlWebHook="http://el-"+project+"-"+project+"-dev.apps.ocp-lab.its4u.eu/";
		System.out.println("wait 4s ...");
		System.out.println("Try to create Webhook");
		Thread.sleep(4000);
		
		final HashMap<String, String> config = new HashMap<>();
	      config.put("url",urlWebHook);
	      config.put("content_type", "json");
	    Collection collectionEvents = new ArrayList();
	    collectionEvents.add(GHEvent.ALL);
		GHHook hook =repo.createHook("web",config,collectionEvents,true);
		
		System.out.println("*** WebHook created with "+urlWebHook);
	}
	
	public void readNodeMavenProjectAndCreateArtifact(TreeNode node,String path) throws IOException {
		
		
		if (node!=null && node.getData() instanceof ProjectArborescenceItem) {
			ProjectArborescenceItem item = (ProjectArborescenceItem) node.getData();
			if (item.getType().equalsIgnoreCase("Folder")) {
				File folder =  new File(path+"//"+item.getName());
				folder.mkdir();
			} else if (item.getType().equalsIgnoreCase("Text")) {
				Path filePath = Paths.get(path, item.getName());
				Files.writeString(filePath,item.getTemplateResource().getResourceAsString() );
			}
			
			if (node.getChildren()!=null && !node.getChildren().isEmpty()) {
				path =path + "/"+item.getName();
				for (TreeNode childNode: node.getChildren()) {
					readNodeMavenProjectAndCreateArtifact(childNode,path);
				}
			}
		}
		
	}
}
