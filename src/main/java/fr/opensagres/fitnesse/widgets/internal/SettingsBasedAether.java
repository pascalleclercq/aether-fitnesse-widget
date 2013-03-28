/**
 * // Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
 * // Released under the terms of the CPL Common Public License version 1.0.
 */
package fr.opensagres.fitnesse.widgets.internal;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyGraphTransformer;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import org.sonatype.aether.util.graph.manager.ClassicDependencyManager;
import org.sonatype.aether.util.graph.selector.AndDependencySelector;
import org.sonatype.aether.util.graph.selector.ExclusionDependencySelector;
import org.sonatype.aether.util.graph.selector.OptionalDependencySelector;
import org.sonatype.aether.util.graph.selector.ScopeDependencySelector;
import org.sonatype.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.sonatype.aether.util.graph.transformer.ConflictMarker;
import org.sonatype.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.sonatype.aether.util.graph.transformer.JavaEffectiveScopeCalculator;
import org.sonatype.aether.util.graph.transformer.NearestVersionConflictResolver;
import org.sonatype.aether.util.graph.traverser.FatArtifactTraverser;
import org.sonatype.aether.util.repository.DefaultAuthenticationSelector;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;
import org.sonatype.aether.util.repository.DefaultProxySelector;

public class SettingsBasedAether extends Aether {

	public static final String userHome = System.getProperty("user.home");

	public static final File userMavenConfigurationHome = new File(userHome, ".m2");

	public static final File DEFAULT_USER_SETTINGS_FILE = new File(userMavenConfigurationHome, "settings.xml");

	public static final File DEFAULT_REPOSITORY = new File(userMavenConfigurationHome, "repository");

	public SettingsBasedAether() {
		super();

	}

	protected MavenRepositorySystemSession newSession() throws Exception {
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();

		session.setIgnoreInvalidArtifactDescriptor(true).setIgnoreMissingArtifactDescriptor(true);
		Map<Object, Object> configProps = new LinkedHashMap<Object, Object>();
		session.setOffline(false);
		Settings settings = getSettings();
		if (settings.getLocalRepository() != null) {
			LocalRepository localRepo = new LocalRepository(settings.getLocalRepository());
			session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(localRepo));
		} else {
			LocalRepository localRepo = new LocalRepository(DEFAULT_REPOSITORY.getPath());
			session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(localRepo));
		}

		DefaultMirrorSelector mirrorSelector = new DefaultMirrorSelector();
		for (Mirror mirror : settings.getMirrors()) {
			mirrorSelector.add(mirror.getId(), mirror.getUrl(), mirror.getLayout(), false, mirror.getMirrorOf(), mirror.getMirrorOfLayouts());
		}
		session.setMirrorSelector(mirrorSelector);

		DefaultProxySelector proxySelector = new DefaultProxySelector();
		for (Proxy proxy : settings.getProxies()) {
			Authentication proxyAuth = new Authentication(proxy.getUsername(), proxy.getPassword());
			proxySelector.add(new org.sonatype.aether.repository.Proxy(proxy.getProtocol(), proxy.getHost(), proxy.getPort(), proxyAuth), proxy.getNonProxyHosts());
		}
		session.setProxySelector(proxySelector);

		DefaultAuthenticationSelector authSelector = new DefaultAuthenticationSelector();
		for (Server server : settings.getServers()) {
			Authentication auth = new Authentication(server.getUsername(), server.getPassword(), server.getPrivateKey(), server.getPassphrase());
			authSelector.add(server.getId(), auth);

			if (server.getConfiguration() != null) {
				Xpp3Dom dom = (Xpp3Dom) server.getConfiguration();
				for (int i = dom.getChildCount() - 1; i >= 0; i--) {
					Xpp3Dom child = dom.getChild(i);
					if ("wagonProvider".equals(child.getName())) {
						dom.removeChild(i);
					}
				}

				XmlPlexusConfiguration config = new XmlPlexusConfiguration(dom);
				configProps.put("aether.connector.wagon.config." + server.getId(), config);
			}

			configProps.put("aether.connector.perms.fileMode." + server.getId(), server.getFilePermissions());
			configProps.put("aether.connector.perms.dirMode." + server.getId(), server.getDirectoryPermissions());
		}
		session.setAuthenticationSelector(authSelector);

		DependencyTraverser depTraverser = new FatArtifactTraverser();
		session.setDependencyTraverser(depTraverser);

		DependencyManager depManager = new ClassicDependencyManager();
		session.setDependencyManager(depManager);

		DependencySelector depFilter = new AndDependencySelector(new ScopeDependencySelector(JavaScopes.TEST), new OptionalDependencySelector(), new ExclusionDependencySelector());
		session.setDependencySelector(depFilter);

		DependencyGraphTransformer transformer = new ChainedDependencyGraphTransformer(new ConflictMarker(), new JavaEffectiveScopeCalculator(),
				new NearestVersionConflictResolver(), new JavaDependencyContextRefiner());
		session.setDependencyGraphTransformer(transformer);

		session.setSystemProps(System.getProperties());
		session.setConfigProps(configProps);

		return session;
	}
	
	public String resolve(Artifact artifact) throws Exception {
		RepositorySystemSession session = newSession();
		Dependency dependency = new Dependency(artifact, "runtime");
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		System.out.println(settings.getMirrors());
		System.out.println(settings.getActiveProfiles());
		//settings.getProfiles().get(0).getRepositories().get(0).get
		//RemoteRepository ee;
		
//		int id = 0;
//		if (remoteRepositories != null && !remoteRepositories.isEmpty()) {
//			for (String remoteRepository : remoteRepositories) {
//				collectRequest.addRepository(new RemoteRepository("repo" + id, "default", remoteRepository));
//				id++;
//			}
//		} else {
			collectRequest.addRepository(new RemoteRepository("central", "default", "http://repo1.maven.org/maven2"));
//		}

		DependencyRequest dependencyRequest = new DependencyRequest();
		
		dependencyRequest.setCollectRequest(collectRequest);
		dependencyRequest.setFilter(new DependencyFilter() {

			@Override
			public boolean accept(DependencyNode node, List<DependencyNode> parents) {
				// no test dependencies
				// no optional dependencies
				return !node.getDependency().getScope().equals(JavaScopes.TEST) && !node.getDependency().isOptional();
			}
		});

		DependencyNode rootNode = repositorySystem.resolveDependencies(session, dependencyRequest).getRoot();

		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		rootNode.accept(nlg);

		return nlg.getClassPath();
	}

	protected static Settings settings;

	/**
	 * this method takes some time.
	 * It's better to "cache" the result once Its created
	 * @return
	 * @throws SettingsBuildingException
	 */
	private Settings getSettings() throws SettingsBuildingException {
		if (settings == null) {
			
			SettingsBuilder builder = new DefaultSettingsBuilderFactory().newInstance();

			DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
			request.setSystemProperties(System.getProperties());

			request.setUserSettingsFile(DEFAULT_USER_SETTINGS_FILE);

			SettingsBuildingResult result = builder.build(request);
			settings = result.getEffectiveSettings();
			

		}
		return settings;
	}
}
