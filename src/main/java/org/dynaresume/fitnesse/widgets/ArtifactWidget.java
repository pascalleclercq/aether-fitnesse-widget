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

	final String userHome = System.getProperty("user.home");

	final File userMavenConfigurationHome = new File(userHome, ".m2");

	final File defaultUserLocalRepository = new File(userMavenConfigurationHome, "repository");

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
		if (remoteRepo == null)
			remoteRepo = defaultUserLocalRepository.getAbsolutePath();
		return remoteRepo;
	}

	protected String findPomFile(Matcher matcher) {
		if (matcher.find())
			return matcher.group(1);
		else
			return "";
	}

}
