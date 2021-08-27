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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.bil.buildfactory.maven.resources.ProjectArborescenceItem;

import lombok.Data;

@Data
@Component
public class GitInitializerBean {
	
	@Autowired
    private MavenInitializerBean mavenInitializerBean;

	private String gitUrl = "https://github.com/kevbrain/lab-workspace.git";
	
	private String gitRevision ="master";
	
	private String gitSubDirectory;
	
	private String gitUser="kevbrain";
	
	private String gitPassword= "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";
	
	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	public void createRepo(String project) throws AbortedByHookException, ConcurrentRefUpdateException, NoHeadException, NoMessageException, ServiceUnavailableException, UnmergedPathsException, WrongRepositoryStateException, GitAPIException, IOException, URISyntaxException {
		
		UUID uuid = UUID.randomUUID();
		
		this.gitSubDirectory=project;

		boolean deleted = deleteDirectory(new File("c:/Tmp/ocp-lab-workspace-"+uuid));
		System.out.println("workspace deleted : "+deleted);
		
		String path = "c:\\Tmp\\ocp-lab-workspace"+uuid;
		String tech_path = "Tmp/ocp-lab-workspace"+uuid;
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
		
		readNodeMavenProjectAndCreateArtifact(nodeProject,tech_path);
		
		git.add().addFilepattern(".").call();

				
		// Now, we do the commit with a message
		
		RevCommit rev =	git.commit().setAuthor("ksc", "ksc@example.com").setMessage("My commit").call();
	
		RemoteAddCommand remoteAddCommand = git.remoteAdd();
	    remoteAddCommand.setName("origin");
	    remoteAddCommand.setUri(new URIish("https://github.com/kevbrain/lab-workspace.git"));
	    remoteAddCommand.call();

	    // push to remote:
	    PushCommand pushCommand = git.push();
	    pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider("kevbrain", "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"));
	    // you can add more settings here if needed
	    pushCommand.call();
		
		
		
	}
	
	public void readNodeMavenProjectAndCreateArtifact(TreeNode node,String path) throws IOException {
		
		
		if (node!=null && node.getData() instanceof ProjectArborescenceItem) {
			ProjectArborescenceItem item = (ProjectArborescenceItem) node.getData();
			if (item.getType().equalsIgnoreCase("Folder")) {
				File folder =  new File("C:/"+path+"/"+item.getName());
				folder.mkdir();
			} else if (item.getType().equalsIgnoreCase("Text")) {
				Path filePath = Paths.get("C:/", path, item.getName());
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
