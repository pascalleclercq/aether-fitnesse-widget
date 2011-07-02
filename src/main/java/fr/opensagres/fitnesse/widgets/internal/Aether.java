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
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

public class Aether
{
    private List<String> remoteRepositories;

    private RepositorySystem repositorySystem;

    private LocalRepository localRepository;

    public Aether( List<String> remoteRepository, String localRepository )
    {
        this.remoteRepositories = remoteRepository;
        this.repositorySystem = Booter.newRepositorySystem();
        this.localRepository = new LocalRepository( localRepository );
    }

    private RepositorySystemSession newSession()
    {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();
        session.setLocalRepositoryManager( repositorySystem.newLocalRepositoryManager( localRepository ) );
        session.setTransferListener( new ConsoleTransferListener() );
        session.setRepositoryListener( new ConsoleRepositoryListener() );
        return session;
    }

//    public AetherResult resolve( String groupId, String artifactId, String version )
//        throws DependencyResolutionException
//    {
//        RepositorySystemSession session = newSession();
//        Dependency dependency =
//            new Dependency( new DefaultArtifact( groupId, artifactId, "", "jar", version ), "runtime" );
//        CollectRequest collectRequest = new CollectRequest();
//        collectRequest.setRoot( dependency );
//        for (String remoteRepository : remoteRepositories) {
//        	collectRequest.addRepository( new RemoteRepository(null, "default", remoteRepository ) );
//		}
// 
//
//
//
//        
//
//        DependencyRequest dependencyRequest = new DependencyRequest();
//        dependencyRequest.setCollectRequest( collectRequest );
//
//        DependencyNode rootNode = repositorySystem.resolveDependencies( session, dependencyRequest ).getRoot();
//
//        StringBuilder dump = new StringBuilder();
//        displayTree( rootNode, dump );
//
//        PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
//        rootNode.accept( nlg );
//
//        return new AetherResult( rootNode, nlg.getFiles(), nlg.getClassPath() );
//    }
//    
    public AetherResult resolve( Artifact artifact )
            throws DependencyResolutionException
        {
            RepositorySystemSession session = newSession();
            Dependency dependency =
                new Dependency( artifact, "runtime" );
            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot( dependency );
            for (String remoteRepository : remoteRepositories) {
            	collectRequest.addRepository( new RemoteRepository(remoteRepository, "default", remoteRepository ) );
    		}
        
            DependencyRequest dependencyRequest = new DependencyRequest();
            dependencyRequest.setCollectRequest( collectRequest );
            dependencyRequest.setFilter(new DependencyFilter() {
				
				@Override
				public boolean accept(DependencyNode node, List<DependencyNode> parents) {
					//no test dependencies
					//no optional dependencies
					return !node.getDependency().getScope().equals("test") && !node.getDependency().isOptional();
				}
			});
            DependencyNode rootNode = repositorySystem.resolveDependencies( session, dependencyRequest ).getRoot();

            StringBuilder dump = new StringBuilder();
            displayTree( rootNode, dump );

            PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
            rootNode.accept( nlg );

            return new AetherResult( rootNode, nlg.getFiles(), nlg.getClassPath() );
        }

//    public void install( Artifact artifact, Artifact pom )
//        throws InstallationException
//    {
//        RepositorySystemSession session = newSession();
//
//        InstallRequest installRequest = new InstallRequest();
//        installRequest.addArtifact( artifact ).addArtifact( pom );
//
//        repositorySystem.install( session, installRequest );
//    }
//
//    public void deploy( Artifact artifact, Artifact pom, String remoteRepository )
//        throws DeploymentException
//    {
//        RepositorySystemSession session = newSession();
//
//        RemoteRepository nexus = new RemoteRepository( "nexus", "default", remoteRepository );
//        Authentication authentication = new Authentication( "admin", "admin123" );
//        nexus.setAuthentication( authentication );
//
//        DeployRequest deployRequest = new DeployRequest();
//        deployRequest.addArtifact( artifact ).addArtifact( pom );
//        deployRequest.setRepository( nexus );
//
//        repositorySystem.deploy( session, deployRequest );
//    }
//
    private void displayTree( DependencyNode node, StringBuilder sb )
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream( 1024 );
        node.accept( new ConsoleDependencyGraphDumper( new PrintStream( os ) ) );
        sb.append( os.toString() );
    }

}
