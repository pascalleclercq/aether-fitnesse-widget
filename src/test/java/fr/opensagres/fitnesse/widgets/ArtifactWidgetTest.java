package fr.opensagres.fitnesse.widgets;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import util.RegexTestCase;
import fitnesse.ComponentFactory;
import fitnesse.testutil.FitNesseUtil;
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
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

public class ArtifactWidgetTest extends RegexTestCase {

	
	 private Properties testProperties;
	  private ComponentFactory factory;
	  private SymbolProvider testProvider;

	  @Override
	  public void setUp() throws Exception {
		  super.setUp();
	    testProperties = new Properties();
	    testProvider = new SymbolProvider(new SymbolType[] {});
	    factory = new ComponentFactory(testProperties, testProvider);
	  }

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

	    String output = factory.loadSymbolTypes();

	    assertSubString(MavenArtifact.class.getName(), output);

	    assertMatch("!artifact", true);
	  }
	 private void assertMatch(String input, boolean expected) {
	        SymbolMatch match = new ParseSpecification().provider(testProvider).findMatch(new ScanString(input, 0), 0, new SymbolStream());
	        assertEquals(match.isMatch(), expected);
	    }
//	
	private String repoDir = new File(ArtifactWidgetTest.class.getResource("/").getFile()).getParent() + "/repo";

	public void testJunit382() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

	    String symbolValues = MavenArtifact.class.getName();
	    testProperties.setProperty(ComponentFactory.SYMBOL_TYPES, symbolValues);
	    
	    String output = factory.loadSymbolTypes();
System.out.println(output);

//		WikiPage root = InMemoryPage.makeRoot("RooT");
//
//		PageCrawler crawler = root.getPageCrawler();
////!define REMOTE_REPO {http://repo1.maven.org/maven2/}\n!define LOCAL_REPO {target/repo}\n
//		//WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"), "!artifact junit:junit:3.8.2\n");
//		WikiPage page =crawler.addPage(root, PathParser.parse("ClassPath"));
//		
//		PageData data = page.getData();
//        data.setContent("!artifact junit:junit:3.8.2");
//        page.commit(data);
//        
        
        String input="!artifact junit:junit:3.8.2\n";
        WikiPage page = new TestRoot().makePage("TestPage", input);
        Symbol result = parse(page, input);
  System.out.println(serialize(result));      
        
//		List<?> paths = page.getData().getClasspaths();

//		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));

	}
	
	public static Symbol parse(WikiPage page, String input) {
	    return Parser.make(new ParsingPage(new WikiSourcePage(page)), input).parse();
	  }
	  public static Symbol parse(WikiPage page) throws Exception {
		    return Parser.make(new ParsingPage(new WikiSourcePage(page)), page.getData().getContent()).parse();
		  }

	  

	  public static String serialize(Symbol symbol) {
	    StringBuilder result = new StringBuilder();
	    result.append(symbol.getType() != null ? symbol.getType().toString() : "?no type?");
	    int i = 0;
	    for (Symbol child : symbol.getChildren()) {
	      result.append(i == 0 ? "[" : ", ");
	      result.append(serialize(child));
	      i++;
	    }
	    if (i > 0) result.append("]");
	    return result.toString();
	  }

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
}
