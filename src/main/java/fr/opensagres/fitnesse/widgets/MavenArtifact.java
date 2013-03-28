package fr.opensagres.fitnesse.widgets;

import java.util.Arrays;
import java.util.Collection;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import util.Maybe;
import fitnesse.wikitext.parser.HtmlBuilder;
import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Path;
import fitnesse.wikitext.parser.PathsProvider;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolProvider;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translator;
import fr.opensagres.fitnesse.widgets.internal.Aether;
import fr.opensagres.fitnesse.widgets.internal.AetherResult;

public class MavenArtifact extends SymbolType implements Rule, PathsProvider {
    public static final MavenArtifact symbolType = new MavenArtifact();

    public MavenArtifact() {
        super("MavenArtifact");
        wikiMatcher(new Matcher().startLineOrCell().string("!artifact"));
        wikiRule(this);
        htmlTranslation(new HtmlBuilder("span").body(0, "classpath: ").attribute("class", "meta").inline());
    }

    public Collection<String> providePaths(Translator translator, Symbol symbol) {
    	
    	final Aether aether = new Aether("", "");
    	String coords=symbol.childAt(0).childAt(0).getContent();
		final Artifact artifact = new DefaultArtifact(coords);
		AetherResult result =null;
		try {
			result = aether.resolve(artifact);
		} catch (DependencyCollectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArtifactResolutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        return Arrays.asList(translator.translate(new Symbol(Path.symbolType,result.getResolvedClassPath())));
    }

    public Maybe<Symbol> parse(Symbol current, Parser parser) {
        if (!parser.isMoveNext(SymbolType.Whitespace)) return Symbol.nothing;

        return new Maybe<Symbol>(current.add(parser.parseToEnds(0, SymbolProvider.pathRuleProvider, new SymbolType[] {SymbolType.Newline})));
    }
}