package com.bil.buildfactory.beans;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import javax.faces.bean.ManagedBean;

import org.apache.commons.io.FileUtils;
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
import org.eclipse.jgit.errors.NoRemoteRepositoryException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.bil.buildfactory.maven.resources.ProjectArborescenceItem;
import com.bil.buildfactory.ocp.resources.TemplateGenerator;

import lombok.Data;

@Data
@Component
public class GitInitializerBean {
	
	@Autowired
    private MavenInitializerBean mavenInitializerBean;
	
	private String gitUrlPrefix="https://github.com/kevbrain/";
	
	private String gitUrl = "https://github.com/kevbrain/lab-workspace.git";
	
	private String gitOpsUrl = "https://github.com/kevbrain/ocp-gitops.git";
	
	private String gitRevision ="master";
	
	private String gitSubDirectory;
	
	private String gitUser="kevbrain";
	
	private String gitPassword= "";
	
	@Value("${path.workspace}")
	private String pathWorkspace;
	
	private TreeNode nodeArgoApp;
	
	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	public void createApp() throws IllegalStateException, GitAPIException, IOException, URISyntaxException {
		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//ocp-gitops"+uuid;
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
		
		RevCommit rev =	git.commit().setAuthor("ksc", "ksc@example.com").setMessage("My commit").call();
	
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
	
	public void createRepo(String project) throws AbortedByHookException, ConcurrentRefUpdateException, NoHeadException, NoMessageException, ServiceUnavailableException, UnmergedPathsException, WrongRepositoryStateException, GitAPIException, IOException, URISyntaxException {
		
		UUID uuid = UUID.randomUUID();
		
		this.gitSubDirectory=project;

		//boolean deleted = deleteDirectory(new File(pathWorkspace+"/ocp-lab-workspace-"+uuid));
		boolean deleted = deleteDirectory(new File(pathWorkspace+"/"+project+"-"+uuid));
		System.out.println("workspace deleted : "+deleted);
		
		String path = pathWorkspace+"//"+project+"-"+uuid;
		
		gitUrl= gitUrlPrefix+project+".git";
		
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
		
		// Create project
		File newFile = new File(workingDirectory, project);
		newFile.mkdir();
		
			
		// Read Maven Project and Create structure
		TreeNode nodeProject = mavenInitializerBean.getRoot();
		
		readNodeMavenProjectAndCreateArtifact(nodeProject,path);
		
		git.add().addFilepattern(".").call();

				
		// Now, we do the commit with a message
		
		git.commit().setAuthor("ksc", "ksc@example.com").setMessage("My commit").call();
	
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
		
		createApp();
	}
	
	public void createNewRemoteRepository(String project) throws IOException {
		System.out.println("*** create new repo ***");
		
		GitHub github = new GitHubBuilder().withOAuthToken(gitPassword).build();
		//github.connect();

		GHRepository repo = github.createRepository(
		  project,"this is my new repository",
		  "https://github.com/kevbrain/",true/*public*/);
		//repo.addCollaborators(github.getUser("kevbrain"));
		//repo.delete();
		System.out.println("*** Repo created ***");
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
