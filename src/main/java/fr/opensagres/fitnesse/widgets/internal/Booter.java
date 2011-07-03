package fr.opensagres.fitnesse.widgets.internal;
/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import org.sonatype.aether.RepositorySystem;

/**
 * A helper to boot the repository system and a repository system session.
 */
public class Booter
{

    public static RepositorySystem newRepositorySystem()
    {
        return ManualRepositorySystemFactory.newRepositorySystem();
    }
//
//    public static RepositorySystemSession newRepositorySystemSession( RepositorySystem system )
//    {
//        MavenRepositorySystemSession session = new MavenRepositorySystemSession();
//
//        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
//        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );
//
//        session.setTransferListener( new ConsoleTransferListener() );
//        session.setRepositoryListener( new ConsoleRepositoryListener() );
//
//        // uncomment to generate dirty trees
//        // session.setDependencyGraphTransformer( null );
//
//        session.setWorkspaceReader(new EclipseWorkspaceReader());
//        return session;
//    }

}
