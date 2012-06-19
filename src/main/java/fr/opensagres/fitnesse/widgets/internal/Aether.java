/**
 * // Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
 * // Released under the terms of the CPL Common Public License version 1.0.
 */
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
import org.sonatype.aether.util.DefaultRepositoryCache;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

import fr.opensagres.fitnesse.widgets.internal.eclipse.EclipseWorkspaceReader;

public class Aether {

	private List<String> remoteRepositories;

	protected RepositorySystem repositorySystem;

	private LocalRepository localRepository;

	public Aether() {
		this.repositorySystem = ManualRepositorySystemFactory.newRepositorySystem();
	}

	public void setRemoteRepositories(List<String> remoteRepositories) {
		this.remoteRepositories = remoteRepositories;
	}

	public void setLocalRepository(String localRepository) {
		this.localRepository = new LocalRepository(localRepository);
	}
	private boolean debug="true".equals(System.getProperty("DEBUG"));
	private boolean offline="true".equals(System.getProperty("OFFLINE"));
	protected RepositorySystemSession newSession() throws Exception {
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
		session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(localRepository));
		if(debug){
		 session.setTransferListener(new ConsoleTransferListener());
		 session.setRepositoryListener(new ConsoleRepositoryListener());
		}
		if (System.getProperty("m2eclipse.workspace.state") != null) {
			session.setWorkspaceReader(new EclipseWorkspaceReader());
		}
		session.setNotFoundCachingEnabled(true);
		session.setIgnoreInvalidArtifactDescriptor(true).setIgnoreMissingArtifactDescriptor(true);
		session.setCache(new DefaultRepositoryCache());
		session.setOffline(offline);
		return session;
	}

	public String resolve(Artifact artifact) throws Exception {
		RepositorySystemSession session = newSession();
		Dependency dependency = new Dependency(artifact, "runtime");
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		int id = 0;
		if (remoteRepositories != null && !remoteRepositories.isEmpty()) {
			for (String remoteRepository : remoteRepositories) {
				collectRequest.addRepository(new RemoteRepository("repo" + id, "default", remoteRepository));
				id++;
			}
		} else {
			collectRequest.addRepository(new RemoteRepository("central", "default", "http://repo1.maven.org/maven2"));
		}

		DependencyRequest dependencyRequest = new DependencyRequest();

		dependencyRequest.setCollectRequest(collectRequest);
		dependencyRequest.setFilter(new DependencyFilter() {

			@Override
			public boolean accept(DependencyNode node, List<DependencyNode> parents) {
				// no test dependencies
				// no optional dependencies
				return !node.getDependency().getScope().equals(JavaScopes.TEST) && !node.getDependency().isOptional();
			}
		});

		DependencyNode rootNode = repositorySystem.resolveDependencies(session, dependencyRequest).getRoot();

		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		rootNode.accept(nlg);

		return nlg.getClassPath();
	}

}
