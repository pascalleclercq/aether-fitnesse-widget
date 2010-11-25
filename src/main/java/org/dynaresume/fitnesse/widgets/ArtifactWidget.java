/*
 * Copyright (c) 2010 Pascal Leclercq. All rights reserved.
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
package org.dynaresume.fitnesse.widgets;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dynaresume.fitnesse.widgets.internal.Aether;
import org.dynaresume.fitnesse.widgets.internal.AetherResult;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import fitnesse.wiki.PageData;
import fitnesse.wikitext.WidgetBuilder;
import fitnesse.wikitext.widgets.ClasspathWidget;
import fitnesse.wikitext.widgets.ParentWidget;

/**
 * @author pascalleclercq
 * 1. Grab aether-fitnesse-widget-<version>-jar-with-dependencies.jar from maven central repo or from GitHub.
 * 2. Create/edit a file named "plugins.properties" in the FitNesse server root directory. This must contain: WikiWidgets=org.dynaresume.fitnesse.widgets.ArtifactWidget
 * 3. Start FitNesse. this way : java -cp fitnesse.jar:aether-fitnesse-widget-<version>-jar-with-dependencies.jar fitnesseMain.FitNesseMain
 * 4. You should be able to see :
 *    FitNesse (v20101101) Started...
 *       port:              8087
 * 	  root page:         fitnesse.wiki.FileSystemPage at ./FitNesseRoot
 * 	  logger:            none
 * 	  authenticator:     fitnesse.authentication.PromiscuousAuthenticator
 * 	  html page factory: fitnesse.html.HtmlPageFactory
 * 	  page version expiration set to 14 days.
 * 	  Custom wiki widgets loaded:
 * 		org.dynaresume.fitnesse.widgets.ArtifactWidget
 * 
 * 5. Create a FitNesse page with a reference to a artifact :
 * 
 * !artifact groupId:artifactId:version
 * 
 * This will load your projects classpath into the page. 
 * You can customize the place of the local repo by providing a value for "LOCAL_REPO" (eg : !define LOCAL_REPO {/Users/pascalleclercq/my_alternate_repo}).
 * If not set, the maven's default value will be use : ${user.home}/.m2/repository.
 * You can also define a remote repo where artifact can be downloaded if needed by providing a value for "REMOTE_REPO" (eg : !define REMOTE_REPO {http://mycompany:9080/nexus}).
 *
 */
public class ArtifactWidget extends ClasspathWidget {

	static {
		PageData.classpathWidgetBuilder = new WidgetBuilder(new Class[] { ArtifactWidget.class });
	}

	private static final Pattern pattern = Pattern.compile("^!artifact (.*)");

	private AetherResult result;
	private String coords;

	/**
	 * @param parent
	 * @param inputText
	 * @throws Exception
	 */
	public ArtifactWidget(ParentWidget parent, String inputText) throws Exception {
		super(parent, "");
		
		
		Matcher matcher = pattern.matcher(inputText);
		coords = findPomFile(matcher);
	}

	private static final String USER_HOME = System.getProperty("user.home");

	private static final File USER_MAVEN_CONFIGURATION_HOME = new File(USER_HOME, ".m2");

	private static final File DEFAULT_USER_LOCAL_REPOSITORY = new File(USER_MAVEN_CONFIGURATION_HOME, "repository");

	@Override
	public String childHtml() throws Exception {

		final Aether aether = new Aether(getRemoteRepo(), getLocalRepo());

		final Artifact artifact = new DefaultArtifact(coords);
		result = aether.resolve(artifact);
		return result.getResolvedClassPath();
	}

	private String getRemoteRepo() throws Exception {

		return getVariable("REMOTE_REPO");
	}

	private String getLocalRepo() throws Exception {
		String remoteRepo = getVariable("LOCAL_REPO");
		if (remoteRepo == null){
			remoteRepo = DEFAULT_USER_LOCAL_REPOSITORY.getAbsolutePath();
			addVariable("LOCAL_REPO", remoteRepo);
		}
			
			
		return remoteRepo;
	}

	private String findPomFile(Matcher matcher) {
		if (matcher.find())
			return matcher.group(1);
		else
			return "";
	}

}
