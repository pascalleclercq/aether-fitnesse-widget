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
package fr.opensagres.fitnesse.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.StringUtils;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import fitnesse.html.HtmlUtil;
import fitnesse.wiki.PageData;
import fitnesse.wikitext.WidgetBuilder;
import fitnesse.wikitext.widgets.ClasspathWidget;
import fitnesse.wikitext.widgets.ParentWidget;
import fr.opensagres.fitnesse.widgets.internal.Aether;
import fr.opensagres.fitnesse.widgets.internal.AetherResult;

/**
 * @author pascalleclercq {@link https
 *         ://github.com/pascalleclercq/aether-fitnesse-widget} for more
 *         installation instructions
 * @see ClasspathWidget
 * 
 */
public class ArtifactWidget extends ClasspathWidget {

	static {
		PageData.classpathWidgetBuilder = new WidgetBuilder(
				new Class[] { ArtifactWidget.class });
	}

	public static final String REGEXP = "^!artifact [^\r\n]*";
	private static final Pattern pattern = Pattern.compile("^!artifact (.*)");

	private String coords;

	/**
	 * @param parent
	 * @param inputText
	 *            {@code !artifact <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
	 * @throws Exception
	 */
	public ArtifactWidget(ParentWidget parent, String inputText)
			throws Exception {
		super(parent, "");

		Matcher matcher = pattern.matcher(inputText);
		coords = findCoordinate(matcher);

	}

	private static final String USER_HOME = System.getProperty("user.home");

	private static final File USER_MAVEN_CONFIGURATION_HOME = new File(
			USER_HOME, ".m2");

	private static final File DEFAULT_USER_LOCAL_REPOSITORY = new File(
			USER_MAVEN_CONFIGURATION_HOME, "repository");

	@Override
	public String render() throws Exception {
		try {
			return HtmlUtil.metaText("classpath: " + getText());
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage()
					+ "\n"
					+ e.getCause()
					+ " please check check that your artifiact is installed or is available in your LOCAL_REPO parameter";
		}

	}

	@Override
	public String getText() throws Exception {
		final Aether aether = new Aether(getRemoteRepo(), getLocalRepo());
		final Artifact artifact = new DefaultArtifact(coords);

		AetherResult result = aether.resolve(artifact);
		return result.getResolvedClassPath();

	}

	private List<String> getRemoteRepo() throws Exception {
		List<String> result=new ArrayList<String>();
		//add the default repo in any case...
		result.add("http://repo1.maven.org/maven2/");
		
		String remoteReposStr = getWikiPage().getData().getVariable(
				"REMOTE_REPO");
		if (!StringUtils.isEmpty(remoteReposStr)) {
			String[] array = remoteReposStr.split(";");
			result.addAll(Arrays.asList(array));
			
		} 
			//prevent nullpointerException...
			return result;

	}

	public String getLocalRepo() throws Exception {
		// this is the only way to be sure variable will be initialized during
		// "test"...
		String localRepo = getWikiPage().getData().getVariable("LOCAL_REPO");
		if (localRepo == null) {
			localRepo = DEFAULT_USER_LOCAL_REPOSITORY.getAbsolutePath();
		}

		return localRepo;
	}

	public String asWikiText() throws Exception {
		return "!artifact " + coords;
	}

	private String findCoordinate(Matcher matcher) {
		if (matcher.find())
			return matcher.group(1);
		else
			return "";
	}

}
