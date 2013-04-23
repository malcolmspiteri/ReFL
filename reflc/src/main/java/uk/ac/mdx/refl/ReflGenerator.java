package uk.ac.mdx.refl;

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

import uk.ac.mdx.refl.scope.Scope;
import uk.ac.mdx.refl.scope.Symbol;
import uk.ac.mdx.refl.scope.ReflSymbolTableBuilder;
import uk.ac.mdx.reflerror.ReflErrorException;
import uk.ac.mdx.reflerror.ReflErrorHandler;
import uk.ac.mdx.reflerror.ReflErrorListener;

public class ReflGenerator {

    private final ReflErrorHandler errorHandler = new ReflErrorHandler();

    public ReflGenerationResult generate(final Reader r) throws IOException, ReflErrorException {

        errorHandler.clear();

        final ParseTree tree = buildParseTree(r);

        if (errorHandler.hasErrors()) {
            throw new ReflErrorException(errorHandler.generateErrorReport());
        }

        final ParseTreeProperty<Scope> scopes = buildSymbolTable(tree);

        final eFrmVisitor<Symbol.Type> validator = new ReflValidatingVisitor(errorHandler, scopes);
        validator.visit(tree);

        if (errorHandler.hasErrors()) {
            throw new ReflErrorException(errorHandler.generateErrorReport());
        }

        final eFrmVisitor<String> renderer = new ReflGeneratingVisitor(
            scopes);

        final String output = renderer.visit(tree);

        return new ReflGenerationResult(((ReflGeneratingVisitor) renderer).getName(),
            ((ReflGeneratingVisitor) renderer).getLabel(), output);
    }

    private ParseTreeProperty<Scope> buildSymbolTable(final ParseTree tree) {

        final ParseTreeWalker walker = new ParseTreeWalker();
        final ReflSymbolTableBuilder def = new ReflSymbolTableBuilder();
        walker.walk(def, tree);

        return def.getScopes();

    }

    private ParseTree buildParseTree(final Reader r) throws IOException {

        final ANTLRInputStream input = new ANTLRInputStream(r);

        final eFrmLexer lexer = new eFrmLexer(input);

        final CommonTokenStream tokens = new CommonTokenStream(lexer);

        final eFrmParser parser = new eFrmParser(tokens);
        parser.setBuildParseTree(true);
        parser.addErrorListener(new ReflErrorListener(errorHandler));

        return parser.form();

    }

}
