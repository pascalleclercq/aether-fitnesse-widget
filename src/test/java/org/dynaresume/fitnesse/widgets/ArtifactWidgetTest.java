package org.dynaresume.fitnesse.widgets;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.WidgetBuilder;

public class ArtifactWidgetTest extends TestCase {

	static {
		PageData.classpathWidgetBuilder = new WidgetBuilder(new Class[] { ArtifactWidget.class });
	}

	private String repoDir = new File(ArtifactWidgetTest.class.getResource("/").getFile()).getParent() + "/repo";

	public void testJunit382() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();

		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"), "!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact junit:junit:3.8.2\n");

		List<?> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));

	}

	public void testComplexDependency() throws Exception {
		// Complex test : Full tree resolved from http://repository.jboss.org/maven2/

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();

		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
				"!define REMOTE_REPO {http://repository.jboss.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact org.hibernate:hibernate-core:3.3.0.CR1\n");

		List<?> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/org/hibernate/hibernate-core/3.3.0.CR1/hibernate-core-3.3.0.CR1.jar:" + repoDir + "/antlr/antlr/2.7.6/antlr-2.7.6.jar:" + repoDir
				+ "/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" + repoDir + "/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:" + repoDir
				+ "/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:" + repoDir + "/javax/transaction/jta/1.1/jta-1.1.jar:" + repoDir + "/javassist/javassist/3.4.GA/javassist-3.4.GA.jar:" + repoDir
				+ "/cglib/cglib/2.1_3/cglib-2.1_3.jar:" + repoDir + "/asm/asm/1.5.3/asm-1.5.3.jar:" + repoDir + "/asm/asm-attrs/1.5.3/asm-attrs-1.5.3.jar:" + repoDir
				+ "/org/slf4j/slf4j-api/1.4.2/slf4j-api-1.4.2.jar", paths.get(0));

	}
}
