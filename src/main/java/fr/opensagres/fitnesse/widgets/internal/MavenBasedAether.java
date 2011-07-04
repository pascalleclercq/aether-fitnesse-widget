package fr.opensagres.fitnesse.widgets.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.RuntimeInfo;
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
import org.sonatype.aether.collection.DependencyGraphTransformer;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
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

public class MavenBasedAether extends Aether {

	public MavenBasedAether() {
		super();

	}

	protected MavenRepositorySystemSession newSession() throws Exception {
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();

		session.setIgnoreInvalidArtifactDescriptor(true).setIgnoreMissingArtifactDescriptor(true);

		Map<Object, Object> configProps = new LinkedHashMap<Object, Object>();

		session.setOffline(false);
		SettingsBuilder builder = new DefaultSettingsBuilderFactory().newInstance();

		DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
		request.setSystemProperties(System.getProperties());

		request.setUserSettingsFile(RuntimeInfo.DEFAULT_USER_SETTINGS_FILE);

		SettingsBuildingResult result = builder.build(request);
		Settings settings = result.getEffectiveSettings();

		if (settings.getLocalRepository() != null) {
			LocalRepository localRepo = new LocalRepository(settings.getLocalRepository());
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

		DependencySelector depFilter = new AndDependencySelector(new ScopeDependencySelector("test", "provided"), new OptionalDependencySelector(),
				new ExclusionDependencySelector());
		session.setDependencySelector(depFilter);

		DependencyGraphTransformer transformer = new ChainedDependencyGraphTransformer(new ConflictMarker(), new JavaEffectiveScopeCalculator(),
				new NearestVersionConflictResolver(), new JavaDependencyContextRefiner());
		session.setDependencyGraphTransformer(transformer);

		session.setSystemProps(System.getProperties());
		session.setConfigProps(configProps);

		return session;
	}

}
