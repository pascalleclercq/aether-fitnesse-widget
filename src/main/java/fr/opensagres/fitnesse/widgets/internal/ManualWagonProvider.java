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

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.sonatype.aether.connector.wagon.WagonProvider;

/**
 * A simplistic provider for wagon instances when no Plexus-compatible IoC container is used.
 */
public class ManualWagonProvider
implements WagonProvider
{

public Wagon lookup( String roleHint )
    throws Exception
{
    if ( "http".equals( roleHint ) )
    {
        return new LightweightHttpWagon();
    }
    if ( "https".equals( roleHint ) )
    {
        return new LightweightHttpWagon();
    }
    return null;
}

public void release( Wagon wagon )
{

}

}
