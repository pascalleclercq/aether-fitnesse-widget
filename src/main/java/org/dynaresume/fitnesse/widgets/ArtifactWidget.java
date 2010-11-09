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
		PageData.classpathWidgetBuilder = new WidgetBuilder(
				new Class[] { ArtifactWidget.class });
	}

	public static final String REGEXP = "^!artifact [^\r\n]*";
	private static final Pattern pattern = Pattern.compile("^!artifact (.*)");

	
	private AetherResult result;
	
	public ArtifactWidget(ParentWidget parent, String inputText) throws Exception {
		super(parent, "");
		
		Matcher matcher = pattern.matcher(inputText);


		String coords = findPomFile(matcher);
		Aether aether = new Aether(null, defaultUserLocalRepository.getAbsolutePath());
		
		Artifact artifact = new DefaultArtifact(coords);
		 result = aether.resolve(artifact);
		

	}
	
	final String userHome = System.getProperty( "user.home" );

    final File userMavenConfigurationHome = new File( userHome, ".m2" );

    final File defaultUserLocalRepository = new File( userMavenConfigurationHome, "repository" );
	
//	@SuppressWarnings("unchecked")
//	protected void findArtifacts(MavenProject mavenProject, List<String> classpaths) throws Exception {
//		List<String> artifacts = mavenProject.getCompileClasspathElements();
//		if (artifacts != null) {
//			for (String name : artifacts) {
//									
//					String classpath = String.format("%s" ,  name);
//					if(name.contains("fitnesse"))
//						classpaths.add(0, classpath);
//					else
//						classpaths.add(classpath);
//				
//			}
//		}
//	}
//
//	protected void findOutputDirs(MavenProject mavenProject, List<String> classpaths) {
////		Build mavenBuild = mavenProject.getBuild();
////		mavenProject.getProperties().put("project.basedir","/");
////		mavenProject.setBasedir(new File("."));
////		classpaths.add(		mavenProject.getBuild().getDirectory()+"/classes" );
////		
////		classpaths.add(		mavenProject.getBuild().getDirectory()+"/test-classes" );
////		
//		
////			Build mavenBuild = mavenProject.getBuild();
////			if (mavenBuild != null) {
////				String outputDir = mavenBuild.getOutputDirectory();
////				
////				String testOurputDir = mavenBuild.getTestOutputDirectory();
////				classpaths.add(outputDir);
////				classpaths.add(testOurputDir);
////			}
//		}
//
//	public String render() throws Exception {
//
//		return HtmlUtil.metaText("fitness-pom-widget worked it's magic on: "
//				+ pomFile);
//	}

	@Override
	public String childHtml() throws Exception {
	
		return result.getResolvedClassPath();
	}
	
	protected String findPomFile(Matcher matcher) {
		if (matcher.find())
			return matcher.group(1);
		else
			return "";
	}

//	protected MavenProject readMavenProjectFromPom(MavenEmbedder mavenEmbedder,
//			String pomFile) throws ArtifactResolutionException,
//			ArtifactNotFoundException, ProjectBuildingException {
//
//		return mavenEmbedder.readProjectWithDependencies(new File(pomFile));
//	}
//
//	protected boolean startMavenEmbedder(MavenEmbedder mavenEmbedder)
//			throws MavenEmbedderException {
//		mavenEmbedder.setClassLoader(getClass().getClassLoader());
//		mavenEmbedder.start();
//		return true;
//	}
//
//	protected boolean stopMavenEmbedder(MavenEmbedder mavenEmbedder,
//			boolean embedderStarted) throws MavenEmbedderException {
//		if (embedderStarted) {
//			mavenEmbedder.stop();
//		}
//		return false;
//	}

	
}
