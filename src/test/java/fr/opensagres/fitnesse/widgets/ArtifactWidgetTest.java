package fr.opensagres.fitnesse.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.transfer.ArtifactTransferException;

import fitnesse.responders.run.TestSystem;
import fitnesse.responders.run.TestSystem.Descriptor;
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.WidgetBuilder;
import fitnesse.wikitext.widgets.ClasspathWidget;

public class ArtifactWidgetTest {

	@BeforeClass
	public static void initClasspathWidgetBuilder() {
		PageData.classpathWidgetBuilder = new WidgetBuilder(new Class[] { ArtifactWidget.class,ClasspathWidget.class });
	}

	/*@BeforeClass
	public static void initClasspathWidgetBuilder() {
		WidgetBuilder.htmlWidgetBuilder.addWidgetClass(ArtifactWidget.class);	
	}*/
	private String repoDir = new File(ArtifactWidgetTest.class.getResource("/").getFile()).getParent() + "/repo";
	
	@Ignore
	@Test
	public void testJunit382NoRemoteRepo() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();

		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"), "!define LOCAL_REPO {target/repo}\n!artifact junit:junit:3.8.1\n");
		
		List<?> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.1/junit-3.8.1.jar", paths.get(0));

	}
	
	@Ignore
	@Test
	public void testJunit382() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();

		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
				"!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact junit:junit:3.8.2\n");

		List<String> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));

	}

	@Ignore
	@Test
	public void multiplePathIssues() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();

		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
				"!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact junit:junit:3.8.2\n!path target/classes\n");

		List<String> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));
		assertEquals( "target/classes", paths.get(1));

	}
	
	@Test
	public void commandPatternClassical() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage root = InMemoryPage.makeRoot("RooT");
		
		PageCrawler crawler = root.getPageCrawler();
		
		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
				"!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact junit:junit:3.8.2\n!define TEST_SYSTEM {slim}\n");
		Descriptor defaultDescriptor = TestSystem.getDescriptor(page.getData(), false);
		String sep = System.getProperty("path.separator");
		assertEquals("java -cp fitnesse.jar" + sep + "%p %m", defaultDescriptor.commandPattern);
		List<String> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));
		

	}

	@Test
	public void commandPatternSpecialParam() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage root = InMemoryPage.makeRoot("RooT");
		
		PageCrawler crawler = root.getPageCrawler();
		
		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
				"!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact junit:junit:3.8.2\n!define COMMAND_PATTERN {java -specialParam -cp %p %m}\n");
		Descriptor defaultDescriptor = TestSystem.getDescriptor(page.getData(), false);
		String sep = System.getProperty("path.separator");
		assertEquals("java -specialParam -cp %p %m", defaultDescriptor.commandPattern);
		List<String> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));
		

	}
	  @Test
	  public void testCommandPatternWithVariable() throws Exception {
	   

		WikiPage root = InMemoryPage.makeRoot("RooT");
		
		PageCrawler crawler = root.getPageCrawler();
		
		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
				"!define COMMAND_PATTERN (${MY_RUNNER} %p %m)\n!define MY_RUNNER {rubyslim}\n");
		

	    Descriptor myDescriptor = TestSystem.getDescriptor(page.getData(), false);
	    assertEquals("rubyslim %p %m", myDescriptor.commandPattern);
	  }

	
	@Ignore
	@Test
	public void testComplexDependency() throws Exception {
		// Complex test : Full tree resolved from
		// http://repository.jboss.org/nexus/content/groups/public

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();

		WikiPage page = crawler
				.addPage(root, PathParser.parse("ClassPath"),
						"!define REMOTE_REPO { http://repository.jboss.org/nexus/content/groups/public }\n!define LOCAL_REPO {target/repo}\n!artifact org.hibernate:hibernate-core:3.3.0.CR1\n");

		List<String> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/org/hibernate/hibernate-core/3.3.0.CR1/hibernate-core-3.3.0.CR1.jar:" + repoDir + "/antlr/antlr/2.7.6/antlr-2.7.6.jar:" + repoDir
				+ "/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" + repoDir + "/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:" + repoDir
				+ "/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:" + repoDir + "/javax/transaction/jta/1.1/jta-1.1.jar:" + repoDir + "/asm/asm/1.5.3/asm-1.5.3.jar:" + repoDir
				+ "/org/slf4j/slf4j-api/1.4.2/slf4j-api-1.4.2.jar", paths.get(0));

	}
	
	@Ignore
	@Test
	public void testMoreComplexDependency() throws Exception {
		// Complex test : Full tree resolved from
		// http://repository.jboss.org/maven2/

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();
		// http://repository.jboss.org/nexus/content/groups/public
		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),

		"!define REMOTE_REPO {http://maven.springframework.org/milestone;  }\n!define LOCAL_REPO {target/repo}\n!artifact org.springframework:spring-core:3.0.0.RC1\n");

		List<String> paths = page.getData().getClasspaths();
		System.out.println(paths);
		assertEquals(repoDir + "/org/springframework/spring-core/3.0.0.RC1/spring-core-3.0.0.RC1.jar:" + repoDir
				+ "/org/springframework/spring-asm/3.0.0.RC1/spring-asm-3.0.0.RC1.jar:" + repoDir + "/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:" + repoDir
				+ "/org/jboss/logging/com.springsource.org.jboss.logging/2.0.5.GA/com.springsource.org.jboss.logging-2.0.5.GA.jar:"

				+ repoDir + "/org/jboss/util/com.springsource.org.jboss.util/2.2.9.GA/com.springsource.org.jboss.util-2.2.9.GA.jar", paths.get(0));
	}

	/**
	 * com.sun.jdmk:jmxtools:jar:1.2.1 com.sun.jmx:jmxri:jar:1.2.1 are not
	 * available on any repo : Too bad...
	 * 
	 * @throws Exception
	 */
	@Ignore("This test is time consuming and not very usefull on Jenkins...") 
	@Test(expected = DependencyResolutionException.class)
	public void testSpring() throws Exception {
		// Complex test : Full tree resolved from

		try {

			WikiPage root = InMemoryPage.makeRoot("RooT");

			PageCrawler crawler = root.getPageCrawler();
			// http://repository.springsource.com/maven/bundles/external;
			WikiPage page = crawler
					.addPage(root,
							PathParser.parse("ClassPath"),
							"!define REMOTE_REPO {http://repo1.maven.org/maven2;http://oss.sonatype.org/content/repositories/JBoss/;https://oss.sonatype.org/content/repositories/appfuse-releases/}\n!define LOCAL_REPO {target/repo}\n!artifact org.springframework:spring:2.5.6.SEC02\n");

			List<String> paths = page.getData().getClasspaths();
			System.out.println(paths);

		} catch (DependencyResolutionException e) {
			DependencyResult res = e.getResult();

			// System.out.println(res.getCollectExceptions());
			List<ArtifactResult> results = res.getArtifactResults();
			for (ArtifactResult artifactResult : results) {

				if (!artifactResult.getExceptions().isEmpty()) {

					List<Exception> ee = artifactResult.getExceptions();

					for (Exception exception : ee) {
						System.err.println(exception);
						assertTrue(exception instanceof ArtifactTransferException);
						ArtifactTransferException artifactNotFoundException = (ArtifactTransferException) exception;
						System.out.println(artifactNotFoundException.getArtifact());
//XXX It's difficult to create assertion here....
						// System.out.println(artifactNotFoundException.getArtifact().getGroupId());
						// "com.sun.jdmk" or ""com.sun.jmx"
						// if
						// (!"org.springframework".equals(artifactNotFoundException.getArtifact().getGroupId()))
						// {
						// assertEquals("com.sun.j",
						// artifactNotFoundException.getArtifact().getGroupId().substring(0,
						// 9));
						// }
						//
						// else {
						// assertEquals("spring",
						// artifactNotFoundException.getArtifact().getArtifactId());
						// }
					}

				}

			}

			throw e;
		}

	}
}
