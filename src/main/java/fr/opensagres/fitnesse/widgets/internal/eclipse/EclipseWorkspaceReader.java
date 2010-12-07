package fr.opensagres.fitnesse.widgets.internal.eclipse;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;

public final class EclipseWorkspaceReader implements WorkspaceReader {

	  private WorkspaceRepository workspaceRepository;

	  public EclipseWorkspaceReader() {
	    this.workspaceRepository = new WorkspaceRepository("ide", getClass());
	  }

	  @Override
	  public int hashCode() {
	    return getClass().hashCode(); // no state
	  }

	  @Override
	  public boolean equals(Object obj) {
	    return obj instanceof EclipseWorkspaceReader;
	  }

	  public WorkspaceRepository getRepository() {
	    return workspaceRepository;
	  }

	  public File findArtifact(org.sonatype.aether.artifact.Artifact artifact) {
	    return WorkspaceState.findArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getExtension(),
	        artifact.getBaseVersion());
	  }

	  public List<String> findVersions(org.sonatype.aether.artifact.Artifact artifact) {
	    return Collections.emptyList();
	  }

	}
