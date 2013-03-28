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

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.DefaultServiceLocator;
=======
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git
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
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.DefaultRepositoryCache;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

import fr.opensagres.fitnesse.widgets.internal.eclipse.EclipseWorkspaceReader;

<<<<<<< HEAD
public class Aether
{

    private RepositorySystem repositorySystem;
    private LocalRepository localRepository;
=======
public class Aether {
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

<<<<<<< HEAD
    public Aether(  )
    {

        this.repositorySystem = newManualSystem();
        
    }
    public void setLocalRepository(LocalRepository localRepository) {
		this.localRepository = localRepository;
	}
=======
	private List<String> remoteRepositories;
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

	protected RepositorySystem repositorySystem;

<<<<<<< HEAD
    private RepositorySystemSession newSession()
    {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();
        session.setWorkspaceReader(new EclipseWorkspaceReader());
        session.setLocalRepositoryManager( repositorySystem.newLocalRepositoryManager( localRepository ) );
      
        session.setTransferListener( new ConsoleTransferListener( System.out ) );
        session.setRepositoryListener( new ConsoleRepositoryListener( System.out ) );
        return session;
    }
    
    public AetherResult resolve( Artifact artifact )
    throws DependencyCollectionException, ArtifactResolutionException
{
    RepositorySystemSession session = newSession();
    
    
    Dependency dependency = new Dependency( artifact, "runtime" );
    
   
    CollectRequest collectRequest = new CollectRequest();
    collectRequest.setRoot( dependency );
  
    collectRequest.setRepositories(repositories);
    
    DependencyNode rootNode = repositorySystem.collectDependencies( session, collectRequest ).getRoot();
=======
	private LocalRepository localRepository;
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

	public Aether() {
		this.repositorySystem = ManualRepositorySystemFactory.newRepositorySystem();
	}

	public void setRemoteRepositories(List<String> remoteRepositories) {
		this.remoteRepositories = remoteRepositories;
	}

<<<<<<< HEAD
    PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
    rootNode.accept( nlg );
    AetherResult aetherResult =new AetherResult( rootNode, nlg.getFiles(), nlg.getClassPath() );
    System.out.println(aetherResult.getResolvedClassPath());
    return aetherResult;
}
    
    public AetherResult resolve( String groupId, String artifactId, String version )
        throws DependencyCollectionException, ArtifactResolutionException
    {
        RepositorySystemSession session = newSession();
        
        
        Dependency dependency = new Dependency( new DefaultArtifact( groupId, artifactId, "", "jar", version ), "runtime" );
=======
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
		if(offline){
			session.setOffline(offline);
			session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_NEVER);
		}
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

<<<<<<< HEAD
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( dependency );
        collectRequest.setRepositories(repositories);
=======
		return session;
	}
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

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

    private List<RemoteRepository> repositories = new ArrayList<RemoteRepository>();
	public void addRemoteRepository(RemoteRepository remoteRepository) {
		repositories.add(remoteRepository);
		
	}

}
