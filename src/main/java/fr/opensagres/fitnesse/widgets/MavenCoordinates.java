package fr.opensagres.fitnesse.widgets;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilder;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import util.Maybe;
import fitnesse.wikitext.parser.HtmlBuilder;
import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.PathsProvider;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolProvider;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translator;
import fr.opensagres.fitnesse.widgets.internal.Aether;
import fr.opensagres.fitnesse.widgets.internal.AetherResult;

public class MavenCoordinates extends SymbolType implements Rule, PathsProvider {
	public static final MavenCoordinates symbolType = new MavenCoordinates();

	public MavenCoordinates() {
		super("MavenCoordinates");

		wikiMatcher(new Matcher().startLineOrCell().string("!coordinates"));
		wikiRule(this);
		htmlTranslation(new HtmlBuilder("span").body(0, "classpath: ").attribute("class", "meta").inline());
	}

	public Collection<String> providePaths(Translator translator, Symbol symbol) {
		AetherResult result = null;
		try {

			Settings settings = getFromSettings();
			List<Mirror> mirrors = settings.getMirrors();
			final Aether aether = new Aether();
			if (mirrors.isEmpty()) {
				aether.addRemoteRepository(new RemoteRepository("central", "default", "http://repo1.maven.org/maven2"));
			}
			List<String> profiles = settings.getActiveProfiles();
			for (String profileId : profiles) {
				Map<String, Profile> profilesAsMap = settings.getProfilesAsMap();
				Profile profile = profilesAsMap.get(profileId);
				System.err.println(profileId);
				if (profile != null) {
					List<Repository> repositories = profile.getRepositories();
					for (Repository repository : repositories) {
						aether.addRemoteRepository(new RemoteRepository(repository.getId(), repository.getLayout(), repository.getUrl()));
					}
				}
			}
			aether.setLocalRepository(new LocalRepository(settings.getLocalRepository()));

			String coords = symbol.childAt(0).childAt(0).getContent();
			final Artifact artifact = new DefaultArtifact(coords);
			result = aether.resolve(artifact);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		symbol.add(result.getResolvedClassPath());
		return Arrays.asList(translator.translate(symbol.childAt(1)));
	}

	public Maybe<Symbol> parse(Symbol current, Parser parser) {
		settingsPath = parser.getVariableSource().findVariable("settings").getValue();

		if (!parser.isMoveNext(SymbolType.Whitespace))
			return Symbol.nothing;

		return new Maybe<Symbol>(current.add(parser.parseToEnds(0, SymbolProvider.pathRuleProvider, new SymbolType[] { SymbolType.Newline })));
	}

	public static final String userHome = System.getProperty("user.home");

	public static final File userMavenConfigurationHome = new File(userHome, ".m2");

	public static final File DEFAULT_USER_SETTINGS_FILE = new File(userMavenConfigurationHome, "settings.xml");

	private String settingsPath;

	void setSettingsPath(String settingsPath) {
		this.settingsPath = settingsPath;
	}

	public Settings getFromSettings() throws SettingsBuildingException {
		File userSettingsFile = getSettingsFile(settingsPath);
		if (userSettingsFile.exists()) {

			DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
			request.setUserSettingsFile(userSettingsFile);

			DefaultSettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();

			SettingsBuildingResult result = settingsBuilder.build(request);

			Settings settings = result.getEffectiveSettings();
			if (settings.getLocalRepository() == null) {
				settings.setLocalRepository(userMavenConfigurationHome + "/repository");
			}
			return settings;
		} else {
			// mock a default settings file...
			Settings settings = new Settings();
			settings.setLocalRepository(userMavenConfigurationHome + "/repository");

			return settings;
		}
	}

	private File getSettingsFile(String path) {
		if (path != null) {
			return new File(path);
		}
		// else...
		return DEFAULT_USER_SETTINGS_FILE;
	}
}