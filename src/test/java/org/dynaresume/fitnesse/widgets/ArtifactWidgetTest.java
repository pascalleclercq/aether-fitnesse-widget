package org.dynaresume.fitnesse.widgets;

import junit.framework.TestCase;
import fitnesse.wikitext.widgets.MockWidgetRoot;
import fitnesse.wikitext.widgets.ParentWidget;

public class ArtifactWidgetTest extends TestCase {

	private ParentWidget parent;

	@Override
	protected void setUp() throws Exception {

		parent = new MockWidgetRoot();

	}

	public void testJunit382() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module
		ArtifactWidget artifactWidget = new ArtifactWidget(parent, "!artifact junit:junit:3.8.2");
		String resolvedClassPath = artifactWidget.childHtml();
		System.out.println(resolvedClassPath);

		assertEquals(artifactWidget.getVariable("LOCAL_REPO") + "/junit/junit/3.8.2/junit-3.8.2.jar", resolvedClassPath);

	}

	public void testJunit482() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is not a
		// dependency of the current module
		System.setProperty("REMOTE_REPO", "http://repo1.maven.org/maven2");
		ArtifactWidget artifactWidget = new ArtifactWidget(parent, "!artifact junit:junit:4.8.2");
		String resolvedClassPath = artifactWidget.childHtml();
		System.out.println(resolvedClassPath);

		assertEquals(artifactWidget.getVariable("LOCAL_REPO") + "/junit/junit/4.8.2/junit-4.8.2.jar", resolvedClassPath);

	}

	public void testComplexDependency() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is not a
		// dependency of the current module
		System.setProperty("REMOTE_REPO", "http://repository.jboss.org/maven2/");
		ArtifactWidget artifactWidget = new ArtifactWidget(parent, "!artifact org.hibernate:hibernate-core:3.3.0.CR1");
		String resolvedClassPath = artifactWidget.childHtml();
		System.out.println(resolvedClassPath);
		assertEquals(
				artifactWidget.getVariable("LOCAL_REPO") + "/org/hibernate/hibernate-core/3.3.0.CR1/hibernate-core-3.3.0.CR1.jar:"
						+ artifactWidget.getVariable("LOCAL_REPO") + "/antlr/antlr/2.7.6/antlr-2.7.6.jar:" + artifactWidget.getVariable("LOCAL_REPO")
						+ "/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" + artifactWidget.getVariable("LOCAL_REPO")
						+ "/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:" + artifactWidget.getVariable("LOCAL_REPO")
						+ "/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:" + artifactWidget.getVariable("LOCAL_REPO")
						+ "/javax/transaction/jta/1.1/jta-1.1.jar:" + artifactWidget.getVariable("LOCAL_REPO")
						+ "/javassist/javassist/3.4.GA/javassist-3.4.GA.jar:" + artifactWidget.getVariable("LOCAL_REPO")
						+ "/cglib/cglib/2.1_3/cglib-2.1_3.jar:" + artifactWidget.getVariable("LOCAL_REPO") + "/asm/asm/1.5.3/asm-1.5.3.jar:"
						+ artifactWidget.getVariable("LOCAL_REPO") + "/asm/asm-attrs/1.5.3/asm-attrs-1.5.3.jar:" + artifactWidget.getVariable("LOCAL_REPO")
						+ "/org/slf4j/slf4j-api/1.4.2/slf4j-api-1.4.2.jar", resolvedClassPath);

	}
}
