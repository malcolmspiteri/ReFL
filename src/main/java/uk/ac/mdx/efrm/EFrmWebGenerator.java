package uk.ac.mdx.efrm;

import java.io.Reader;

import main.antlr.eFrmLexer;
import main.antlr.eFrmParser;
import main.antlr.eFrmVisitor;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.tree.ParseTree;

public class EFrmWebGenerator {

    public String generate(final Reader r) throws Exception {

        final ParseTree pt = buildParseTree(r);
        final eFrmVisitor<String> renderer = new FormJsGenerator();

        return renderer.visit(pt);

    }

    private ParseTree buildParseTree(final Reader r) throws Exception {

        final ANTLRInputStream input = new ANTLRInputStream(r);

        final eFrmLexer lexer = new eFrmLexer(input);

        final CommonTokenStream tokens = new CommonTokenStream(lexer);

        final eFrmParser parser = new eFrmParser(tokens);

        return parser.form();

    }

}
