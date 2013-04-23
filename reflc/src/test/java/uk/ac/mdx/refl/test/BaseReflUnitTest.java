package uk.ac.mdx.refl.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import main.antlr.eFrmLexer;
import main.antlr.eFrmParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import uk.ac.mdx.refl.ReflGenerator;
import uk.ac.mdx.refl.scope.ReflSymbolTableBuilder;
import uk.ac.mdx.refl.scope.Scope;

public class BaseReflUnitTest {

	public BaseReflUnitTest() {
		super();
	}

	protected String getOutputFor(final String res) throws Exception {
	    final InputStream is = Thread.currentThread().getContextClassLoader()
	            .getResourceAsStream(res + ".refl");
	
        final ReflGenerator generator = new ReflGenerator();
        return generator.generate(new InputStreamReader(is)).getGeneratedCode();
	
	}

	protected ParseTree buildParseTreeFor(final String res) throws IOException {
	    final InputStream is = Thread.currentThread().getContextClassLoader()
	            .getResourceAsStream(res + ".refl");
	
	    final ANTLRInputStream input = new ANTLRInputStream(is);
	    
	    return buildParseTreeFor(input);
	
	}

	protected ParseTree buildParseTreeFor(final Reader r) throws IOException {
	
	    final ANTLRInputStream input = new ANTLRInputStream(r);
	    
	    return buildParseTreeFor(input);
	    
	}

	protected ParseTree buildParseTreeFor(final ANTLRInputStream input) throws IOException {
	
	
	    final eFrmLexer lexer = new eFrmLexer(input);
	
	    final CommonTokenStream tokens = new CommonTokenStream(lexer);
	
	    final eFrmParser parser = new eFrmParser(tokens);
	    parser.setBuildParseTree(true);
	
	    return parser.form();
	
	}

	protected ParseTreeProperty<Scope> buildSymbolTable(
			ParseTree pt) {
		final ReflSymbolTableBuilder builder = new ReflSymbolTableBuilder();
		final ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(builder, pt);
        
        ParseTreeProperty<Scope> ptp = builder.getScopes();
		return ptp;
	}
	
	protected String stripWS(String s) {
		return s.replaceAll("\\s","");				
	}
	
}