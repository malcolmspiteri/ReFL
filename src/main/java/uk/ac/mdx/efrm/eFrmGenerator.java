package uk.ac.mdx.efrm;

import java.io.IOException;
import java.io.Reader;

import main.antlr.eFrmLexer;
import main.antlr.eFrmParser;
import main.antlr.eFrmVisitor;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import uk.ac.mdx.efrm.error.eFrmErrorException;
import uk.ac.mdx.efrm.error.eFrmErrorHandler;
import uk.ac.mdx.efrm.error.eFrmErrorListener;
import uk.ac.mdx.efrm.scope.Scope;
import uk.ac.mdx.efrm.scope.Symbol;
import uk.ac.mdx.efrm.scope.eFrmSymbolTableBuilder;

public class eFrmGenerator {

    private final eFrmErrorHandler errorHandler = new eFrmErrorHandler();

    public eFrmGenerationResult generate(final Reader r) throws IOException, eFrmErrorException {

        errorHandler.clear();

        final ParseTree tree = buildParseTree(r);

        if (errorHandler.hasErrors()) {
            throw new eFrmErrorException(errorHandler.generateErrorReport());
        }

        final ParseTreeProperty<Scope> scopes = buildSymbolTable(tree);

        final eFrmVisitor<Symbol.Type> validator = new eFrmValidatingVisitor(errorHandler, scopes);
        validator.visit(tree);

        if (errorHandler.hasErrors()) {
            throw new eFrmErrorException(errorHandler.generateErrorReport());
        }

        final eFrmVisitor<String> renderer = new eFrmGeneratingVisitor(
            scopes);

        final String output = renderer.visit(tree);

        return new eFrmGenerationResult(((eFrmGeneratingVisitor) renderer).getName(),
            ((eFrmGeneratingVisitor) renderer).getLabel(), output);
    }

    private ParseTreeProperty<Scope> buildSymbolTable(final ParseTree tree) {

        final ParseTreeWalker walker = new ParseTreeWalker();
        final eFrmSymbolTableBuilder def = new eFrmSymbolTableBuilder();
        walker.walk(def, tree);

        return def.getScopes();

    }

    private ParseTree buildParseTree(final Reader r) throws IOException {

        final ANTLRInputStream input = new ANTLRInputStream(r);

        final eFrmLexer lexer = new eFrmLexer(input);

        final CommonTokenStream tokens = new CommonTokenStream(lexer);

        final eFrmParser parser = new eFrmParser(tokens);
        parser.setBuildParseTree(true);
        parser.addErrorListener(new eFrmErrorListener(errorHandler));

        return parser.form();

    }

}
