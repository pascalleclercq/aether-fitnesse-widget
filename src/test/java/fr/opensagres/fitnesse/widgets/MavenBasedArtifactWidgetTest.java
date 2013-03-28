/**
 * // Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
 * // Released under the terms of the CPL Common Public License version 1.0.
 */
package fr.opensagres.fitnesse.widgets;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;

public class MavenBasedArtifactWidgetTest {

//	@BeforeClass
//	public static void initClasspathWidgetBuilder() {
//		PageData.classpathWidgetBuilder = new WidgetBuilder(new Class[] { ArtifactWidget.class });
//		String root = new File(MavenBasedArtifactWidgetTest.class.getClassLoader().getResource("changeLocalRepo").getFile()).getPath();
//
//		System.setProperty("user.home", root);
//	}

	private String repoDir = new File(MavenBasedArtifactWidgetTest.class.getResource("/").getFile()).getParent() + "/repo2";

	@Ignore
	@Test
	public void testJunit382NoRemoteRepo() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage pageRoot = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = pageRoot.getPageCrawler();

		WikiPage page = crawler.addPage(pageRoot, PathParser.parse("ClassPath"), "!define USE_SETTINGS_XML {true}\n!artifact junit:junit:3.8.1\n");

		List<?> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.1/junit-3.8.1.jar", paths.get(0));

	}

	@Ignore
	@Test
	public void testJunit382NoRemoteRepo2() throws Exception {
		// Very simple test : only 1 dependency resolved, jar is a dependency of
		// the current module

		WikiPage root = InMemoryPage.makeRoot("RooT");

		PageCrawler crawler = root.getPageCrawler();

		WikiPage page = crawler.addPage(root, PathParser.parse("ClassPath"), "!define USE_SETTINGS_XML {true}\n!artifact junit:junit:3.8.2\n");

		List<?> paths = page.getData().getClasspaths();

		assertEquals(repoDir + "/junit/junit/3.8.2/junit-3.8.2.jar", paths.get(0));

	}
}
