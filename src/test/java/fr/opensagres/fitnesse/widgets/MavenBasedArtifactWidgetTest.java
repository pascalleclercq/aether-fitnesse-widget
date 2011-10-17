package fr.opensagres.fitnesse.widgets;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.WidgetBuilder;

public class MavenBasedArtifactWidgetTest {

	@BeforeClass
	public static void initClasspathWidgetBuilder() {
		PageData.classpathWidgetBuilder = new WidgetBuilder(new Class[] { ArtifactWidget.class });

	}

	private String repoDir = new File(MavenBasedArtifactWidgetTest.class.getResource("/").getFile()).getParent() + "/repo2";

	@Ignore
	@Test
	public void testJunit382NoRemoteRepo() throws Exception {
		String root = new File(MavenBasedArtifactWidgetTest.class.getClassLoader().getResource("changeLocalRepo").getFile()).getPath();

		System.setProperty("user.home", root);
		WikiPage pageRoot = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = pageRoot.getPageCrawler();

		WikiPage page = crawler.addPage(pageRoot, PathParser.parse("ClassPath"), "!define USE_SETTINGS_XML {true}\n!artifact junit:junit:3.8.1\n");

		List<?> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.1/junit-3.8.1.jar", paths.get(0));

	}

	@Test
	public void testComplexDependency() throws Exception {
		// Complex test : Full tree resolved from
		// http://repository.jboss.org/nexus/content/groups/public
		String root = new File(MavenBasedArtifactWidgetTest.class.getClassLoader().getResource("changeMirror").getFile()).getPath();

		System.setProperty("user.home", root);

		WikiPage pageRoot = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = pageRoot.getPageCrawler();

		WikiPage page = crawler.addPage(pageRoot, PathParser.parse("ClassPath"), "!define USE_SETTINGS_XML {true}\n!artifact org.hibernate:hibernate-core:3.3.0.CR1\n");

		List<String> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/org/hibernate/hibernate-core/3.3.0.CR1/hibernate-core-3.3.0.CR1.jar:" + repoDir + "/antlr/antlr/2.7.6/antlr-2.7.6.jar:" + repoDir
				+ "/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" + repoDir + "/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:" + repoDir
				+ "/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:" + repoDir + "/javax/transaction/jta/1.1/jta-1.1.jar:" + repoDir + "/asm/asm/1.5.3/asm-1.5.3.jar:" + repoDir
				+ "/org/slf4j/slf4j-api/1.4.2/slf4j-api-1.4.2.jar", paths.get(0));

	}
}
