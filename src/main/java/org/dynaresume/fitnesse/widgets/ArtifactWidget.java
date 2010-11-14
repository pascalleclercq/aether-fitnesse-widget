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

public class ArtifactWidget extends ClasspathWidget {

	static {
		PageData.classpathWidgetBuilder = new WidgetBuilder(new Class[] { ArtifactWidget.class });
	}

	public static final String REGEXP = "^!artifact [^\r\n]*";
	private static final Pattern pattern = Pattern.compile("^!artifact (.*)");

	private AetherResult result;
	private String coords;

	public ArtifactWidget(ParentWidget parent, String inputText) throws Exception {
		super(parent, "");
		Matcher matcher = pattern.matcher(inputText);
		coords = findPomFile(matcher);
	}

	private static final String USER_HOME = System.getProperty("user.home");

	private static final File USER_MAVEN_CONFIGURATION_HOME = new File(USER_HOME, ".m2");

	protected static final File DEFAULT_USER_LOCAL_REPOSITORY = new File(USER_MAVEN_CONFIGURATION_HOME, "repository");

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

	protected String findPomFile(Matcher matcher) {
		if (matcher.find())
			return matcher.group(1);
		else
			return "";
	}

}
