package org.dynaresume.fitnesse.widgets;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public class SandboxTest extends TestCase {

	//private MavenProject project;

	@Override
	protected void setUp() throws Exception {

		Model model = new Model();
		model.setGroupId("dummyGroupId");
		model.setArtifactId("dummyArtifactId");
		model.setVersion("1.0.0-SNAPSHOT");
		
		Dependency dependency= new Dependency();
		dependency.setArtifactId("slf4j-api");
		dependency.setGroupId("org.slf4j");
		dependency.setVersion("1.6.1");
		
		List<Dependency> dependencies= new ArrayList<Dependency>();
//		project = new MavenProject(model);
//		project.setDependencies(dependencies);
		
	}

	public void testToto() throws Exception {

//		Aether aether = new Aether(null,
//				RepositorySystem.defaultUserLocalRepository.getAbsolutePath());
//		
//		Artifact artifact = new DefaultArtifact(
//				"dummyGroupId:dummyArtifactId:1.0.0-SNAPSHOT");
//
//		AetherResult result = aether.resolve(artifact);
//
//		assertNotNull(result.getResolvedClassPath());
//		System.err.println(result.getResolvedClassPath());
	}

}
