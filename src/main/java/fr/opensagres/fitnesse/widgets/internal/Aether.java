package fr.opensagres.fitnesse.widgets.internal;

/*
 * Copyright (c) 2010 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0, 
 * and you may not use this file except in compliance with the Apache License Version 2.0. 
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the Apache License Version 2.0 is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

import fr.opensagres.fitnesse.widgets.internal.eclipse.EclipseWorkspaceReader;

public class Aether {
	private List<String> remoteRepositories;

	protected RepositorySystem repositorySystem;

	private LocalRepository localRepository;

	public Aether() {
		

		this.repositorySystem = Booter.newRepositorySystem();
		
	}

	
	public void setRemoteRepositories(List<String> remoteRepositories) {
		this.remoteRepositories = remoteRepositories;
	}


	public void setLocalRepository(String localRepository) {
		this.localRepository = new LocalRepository(localRepository);
	}


	protected  RepositorySystemSession newSession() throws Exception {
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
		session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(localRepository));
		session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepositoryListener());
		session.setWorkspaceReader(new EclipseWorkspaceReader());

		return session;
	}

	public AetherResult resolve(Artifact artifact) throws Exception {
		RepositorySystemSession session = newSession();
		Dependency dependency = new Dependency(artifact, "runtime");
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		for (String remoteRepository : remoteRepositories) {
			  URI uri = URI.create(remoteRepository);
			  if (uri != null) {
                  collectRequest.addRepository(new RemoteRepository(uri.getHost() + "/" + uri.getRawPath(),
                          "default", remoteRepository));
              }
		}
		collectRequest.addRepository(new RemoteRepository("central", "default", "http://repo1.maven.org/maven2"));

		DependencyRequest dependencyRequest = new DependencyRequest();

		dependencyRequest.setCollectRequest(collectRequest);
		dependencyRequest.setFilter(new DependencyFilter() {

			@Override
			public boolean accept(DependencyNode node, List<DependencyNode> parents) {
				// no test dependencies
				// no optional dependencies
				return !node.getDependency().getScope().equals("test") && !node.getDependency().isOptional();
			}
		});
		DependencyNode rootNode = repositorySystem.resolveDependencies(session, dependencyRequest).getRoot();

		StringBuilder dump = new StringBuilder();
		displayTree(rootNode, dump);

		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		rootNode.accept(nlg);

		return new AetherResult(rootNode, nlg.getFiles(), nlg.getClassPath());
	}

	private void displayTree(DependencyNode node, StringBuilder sb) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		node.accept(new ConsoleDependencyGraphDumper(new PrintStream(os)));
		sb.append(os.toString());
	}
}
