package fr.opensagres.fitnesse.widgets;


import java.io.File;
import java.util.List;
import java.util.Properties;

import util.RegexTestCase;
import fitnesse.ComponentFactory;
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;

public class ClassPathBuilderTest extends RegexTestCase {
	

	private PageCrawler crawler;

	private ComponentFactory factory;

	private String repoDir = new File(ClassPathBuilderTest.class.getResource("/").getFile()).getParent() + "/testRepo";

	private WikiPage root;
	private Properties testProperties;


	public void setUp() throws Exception {
		testProperties = new Properties();
		String symbolValues = MavenArtifact.class.getName();
		testProperties.setProperty(ComponentFactory.SYMBOL_TYPES, symbolValues);
		factory = new ComponentFactory(testProperties);
		factory.loadSymbolTypes();
		root = InMemoryPage.makeRoot("RooT");
		crawler = root.getPageCrawler();
	}
	
	public void grab_hibernate_core_on_jboss_repo() throws Exception {
		// Complex test : Full tree resolved from http://repository.jboss.org/maven2/
		WikiPage page = crawler.addPage(root, PathParser.parse("TestPage3"), 
				"!define settings {src/test/resources/settings.xml}\n"+
				"!artifact org.hibernate:hibernate-core:3.3.0.CR1\n");
		PageData data = page.getData();
		page.commit(data);
		List<String> paths = page.getData().getClasspaths();
		assertEquals(
				repoDir + "/org/hibernate/hibernate-core/3.3.0.CR1/hibernate-core-3.3.0.CR1.jar:" 
				+ repoDir + "/antlr/antlr/2.7.6/antlr-2.7.6.jar:"
				+ repoDir + "/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" 
				+ repoDir + "/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:" 
				+ repoDir + "/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:" 
				+ repoDir + "/javax/transaction/jta/1.1/jta-1.1.jar:" 
				+ repoDir + "/javassist/javassist/3.4.GA/javassist-3.4.GA.jar:" 
				+ repoDir + "/cglib/cglib/2.1_3/cglib-2.1_3.jar:" 
				+ repoDir + "/asm/asm/1.5.3/asm-1.5.3.jar:" 
				+ repoDir + "/asm/asm-attrs/1.5.3/asm-attrs-1.5.3.jar:" 
				+ repoDir + "/org/slf4j/slf4j-api/1.4.2/slf4j-api-1.4.2.jar",
				paths.get(0));

	}
	
	
	public void grab_Junit382_Default() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module
		WikiPage page = crawler.addPage(root, PathParser.parse("TestPage"), "!artifact junit:junit:3.8.2\n" + "!path my.jar");
		PageData data = page.getData();
		page.commit(data);

		List<String> paths = page.getData().getClasspaths();
		
		assertEquals(MavenArtifact.userMavenConfigurationHome + "/repository" + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));

	}
	public void grab_Junit382_With_Alternate_Settings() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module
		WikiPage page = crawler.addPage(root, PathParser.parse("TestPage2"), "!define settings {src/test/resources/settings.xml}\n"+"!artifact junit:junit:3.8.2\n" + "!path my.jar");
		PageData data = page.getData();
		page.commit(data);

	//	System.out.println(builder.getClasspath(root.getChildPage("TestPage2")));

		List<String> paths = page.getData().getClasspaths();
	//	System.out.println(paths.size());
		
		assertEquals( repoDir +"/junit/junit/3.8.2/junit-3.8.2.jar",
				paths.get(0));


	}
	
	
	
	
	
	
}


//public void testGetClasspath() throws Exception {
//	crawler.addPage(root, PathParser.parse("TestPage"), "!path fitnesse.jar\n" + "!path my.jar");
//	String expected = "fitnesse.jar" + pathSeparator + "my.jar";
//	assertEquals(expected, builder.getClasspath(root.getChildPage("TestPage")));
//}
//
//public void testPathSeparatorVariable() throws Exception {
//	WikiPage page = crawler.addPage(root, PathParser.parse("TestPage"), "!define PATH_SEPARATOR {|}\n" + "!path fitnesse.jar\n" + "!path my.jar");
//	PageData data = page.getData();
//	page.commit(data);
//
//	String expected = "fitnesse.jar" + "|" + "my.jar";
//	assertEquals(expected, builder.getClasspath(root.getChildPage("TestPage")));
//}
//
//public void testGetPaths_OneLevel() throws Exception {
//	String pageContent = "This is some content\n" + "!path aPath\n" + "end of conent\n";
//	WikiPage root = InMemoryPage.makeRoot("RooT");
//	WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"), pageContent);
//	String path = builder.getClasspath(page);
//	assertEquals("aPath", path);
//}
//
//public void testGetClassPathMultiLevel() throws Exception {
//	WikiPage root = InMemoryPage.makeRoot("RooT");
//	crawler.addPage(root, PathParser.parse("ProjectOne"), "!path path2\n" + "!path path 3");
//	crawler.addPage(root, PathParser.parse("ProjectOne.TesT"), "!path path1");
//
//	String cp = builder.getClasspath(crawler.getPage(root, PathParser.parse("ProjectOne.TesT")));
//	assertSubString("path1", cp);
//	assertSubString("path2", cp);
//	assertSubString("\"path 3\"", cp);
//}
//
//public void testLinearClassPath() throws Exception {
//	WikiPage root = InMemoryPage.makeRoot("RooT");
//	WikiPage superPage = crawler.addPage(root, PathParser.parse("SuperPage"), "!path superPagePath");
//	WikiPage subPage = crawler.addPage(superPage, PathParser.parse("SubPage"), "!path subPagePath");
//	String cp = builder.getClasspath(subPage);
//	assertEquals("subPagePath" + pathSeparator + "superPagePath", cp);
//
//}
//
//public void testGetClassPathFromPageThatDoesntExist() throws Exception {
//	String classPath = makeClassPathFromSimpleStructure("somePath");
//
//	assertEquals("somePath", classPath);
//}
//
//private String makeClassPathFromSimpleStructure(String path) throws Exception {
//	PageData data = root.getData();
//	data.setContent("!path " + path);
//	root.commit(data);
//	crawler = root.getPageCrawler();
//	crawler.setDeadEndStrategy(new MockingPageCrawler());
//	WikiPage page = crawler.getPage(root, somePagePath);
//	String classPath = builder.getClasspath(page);
//	return classPath;
//}
//
//public void testThatPathsWithSpacesGetQuoted() throws Exception {
//	crawler.addPage(root, somePagePath, "!path Some File.jar");
//	crawler = root.getPageCrawler();
//	WikiPage page = crawler.getPage(root, somePagePath);
//
//	assertEquals("\"Some File.jar\"", builder.getClasspath(page));
//
//	crawler.addPage(root, somePagePath, "!path somefile.jar\n!path Some Dir/someFile.jar");
//	assertEquals("somefile.jar" + pathSeparator + "\"Some Dir/someFile.jar\"", builder.getClasspath(page));
//}
//
//public void testWildCardExpansion() throws Exception {
//	try {
//		makeSampleFiles();
//
//		String classPath = makeClassPathFromSimpleStructure("testDir/*.jar");
//		assertHasRegexp("one\\.jar", classPath);
//		assertHasRegexp("two\\.jar", classPath);
//
//		classPath = makeClassPathFromSimpleStructure("testDir/*.dll");
//		assertHasRegexp("one\\.dll", classPath);
//		assertHasRegexp("two\\.dll", classPath);
//
//		classPath = makeClassPathFromSimpleStructure("testDir/one*");
//		assertHasRegexp("one\\.dll", classPath);
//		assertHasRegexp("one\\.jar", classPath);
//		assertHasRegexp("oneA", classPath);
//
//		classPath = makeClassPathFromSimpleStructure("testDir/**.jar");
//		assertHasRegexp("one\\.jar", classPath);
//		assertHasRegexp("two\\.jar", classPath);
//		assertHasRegexp("subdir(?:\\\\|/)sub1\\.jar", classPath);
//		assertHasRegexp("subdir(?:\\\\|/)sub2\\.jar", classPath);
//	} finally {
//		deleteSampleFiles();
//	}
//}
