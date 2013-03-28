/**
 * // Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
 * // Released under the terms of the CPL Common Public License version 1.0.
 */
package fr.opensagres.fitnesse.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

<<<<<<< HEAD
import util.RegexTestCase;
import fitnesse.ComponentFactory;
import fitnesse.testutil.FitNesseUtil;
=======
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.transfer.ArtifactTransferException;

import fitnesse.responders.run.TestSystem;
import fitnesse.responders.run.TestSystem.Descriptor;
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
<<<<<<< HEAD
import fitnesse.wikitext.parser.ParseSpecification;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.ParsingPage;
import fitnesse.wikitext.parser.ScanString;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolMatch;
import fitnesse.wikitext.parser.SymbolProvider;
import fitnesse.wikitext.parser.SymbolStream;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.WikiSourcePage;
import fitnesse.wikitext.test.TestRoot;
=======
import fitnesse.wikitext.WidgetBuilder;
import fitnesse.wikitext.widgets.ClasspathWidget;
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

<<<<<<< HEAD
public class ArtifactWidgetTest extends RegexTestCase {
=======
public class ArtifactWidgetTest {
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

<<<<<<< HEAD
	
	 private Properties testProperties;
	  private ComponentFactory factory;
	  private SymbolProvider testProvider;
=======
	@BeforeClass
	public static void initClasspathWidgetBuilder() {
		PageData.classpathWidgetBuilder = new WidgetBuilder(new Class[] { ArtifactWidget.class,ClasspathWidget.class });
	}
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

	  @Override
	  public void setUp() throws Exception {
		  super.setUp();
	    testProperties = new Properties();
	    testProvider = new SymbolProvider(new SymbolType[] {});
	    factory = new ComponentFactory(testProperties, testProvider);
	  }

<<<<<<< HEAD
	  @Override
	  public void tearDown() throws Exception {
		  super.tearDown();
	    final File file = new File(ComponentFactory.PROPERTIES_FILE);
	    FileOutputStream out = new FileOutputStream(file);
	    out.write("".getBytes());
	    out.close();
	  }
	
	public void testWikiWidgetPlugins() throws Exception {
	    String symbolValues = MavenArtifact.class.getName();
	    testProperties.setProperty(ComponentFactory.SYMBOL_TYPES, symbolValues);
=======

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


	@Test
	public void testJunit382() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

	    String output = factory.loadSymbolTypes();

	    assertSubString(MavenArtifact.class.getName(), output);

<<<<<<< HEAD
	    assertMatch("!artifact", true);
	  }
	 private void assertMatch(String input, boolean expected) {
	        SymbolMatch match = new ParseSpecification().provider(testProvider).findMatch(new ScanString(input, 0), 0, new SymbolStream());
	        assertEquals(match.isMatch(), expected);
	    }
//	
//	private String repoDir = new File(ArtifactWidgetTest.class.getResource("/").getFile()).getParent() + "/repo";
//
//	public void testJunit382() throws Exception {
//		// Very simple test : only 1 dependency resolved, jar is a dependency of
//		// the current module
//
//	    String symbolValues = MavenArtifact.class.getName();
//	    testProperties.setProperty(ComponentFactory.SYMBOL_TYPES, symbolValues);
//	    
//	    String output = factory.loadSymbolTypes();
//System.out.println(output);
//
////		WikiPage root = InMemoryPage.makeRoot("RooT");
////
////		PageCrawler crawler = root.getPageCrawler();
//////!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n
////		//WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"), "!artifact junit:junit:3.8.2\n");
////		WikiPage page =crawler.addPage(root, PathParser.parse("ClassPath"));
////		
////		PageData data = page.getData();
////        data.setContent("!artifact junit:junit:3.8.2");
////        page.commit(data);
////        
//        
//        String input="!artifact junit:junit:3.8.2\n";
//        WikiPage page = new TestRoot().makePage("TestPage", input);
//        Symbol result = parse(page, input);
//  System.out.println(serialize(result));      
//        
////		List<?> paths = page.getData().getClasspaths();
//
////		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));
//
//	}
	
//	public static Symbol parse(WikiPage page, String input) {
//	    return Parser.make(new ParsingPage(new WikiSourcePage(page)), input).parse();
//	  }
//	  public static Symbol parse(WikiPage page) throws Exception {
//		    return Parser.make(new ParsingPage(new WikiSourcePage(page)), page.getData().getContent()).parse();
//		  }
//
//	  
//
//	  public static String serialize(Symbol symbol) {
//	    StringBuilder result = new StringBuilder();
//	    result.append(symbol.getType() != null ? symbol.getType().toString() : "?no type?");
//	    int i = 0;
//	    for (Symbol child : symbol.getChildren()) {
//	      result.append(i == 0 ? "[" : ", ");
//	      result.append(serialize(child));
//	      i++;
//	    }
//	    if (i > 0) result.append("]");
//	    return result.toString();
//	  }
=======
		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
				"!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact junit:junit:3.8.2\n");
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git

<<<<<<< HEAD
//
//	public void testComplexDependency() throws Exception {
//		// Complex test : Full tree resolved from http://repository.jboss.org/maven2/
//
//		WikiPage root = InMemoryPage.makeRoot("RooT");
//
//		PageCrawler crawler = root.getPageCrawler();
//
//		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"),
//				"!define REMOTE_REPO {http://repository.jboss.org/maven2/}\n!define LOCAL_REPO {target/repo}\n!artifact org.hibernate:hibernate-core:3.3.0.CR1\n");
//
//		List<?> paths = page.getData().getClasspaths();
//
//		assertEquals(repoDir + "/org/hibernate/hibernate-core/3.3.0.CR1/hibernate-core-3.3.0.CR1.jar:" + repoDir + "/antlr/antlr/2.7.6/antlr-2.7.6.jar:" + repoDir
//				+ "/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" + repoDir + "/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:" + repoDir
//				+ "/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:" + repoDir + "/javax/transaction/jta/1.1/jta-1.1.jar:" + repoDir + "/javassist/javassist/3.4.GA/javassist-3.4.GA.jar:" + repoDir
//				+ "/cglib/cglib/2.1_3/cglib-2.1_3.jar:" + repoDir + "/asm/asm/1.5.3/asm-1.5.3.jar:" + repoDir + "/asm/asm-attrs/1.5.3/asm-attrs-1.5.3.jar:" + repoDir
//				+ "/org/slf4j/slf4j-api/1.4.2/slf4j-api-1.4.2.jar", paths.get(0));
//
//	}
=======
		List<String> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));

	}


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
>>>>>>> branch 'master' of git@github.com:pascalleclercq/aether-fitnesse-widget.git
}
