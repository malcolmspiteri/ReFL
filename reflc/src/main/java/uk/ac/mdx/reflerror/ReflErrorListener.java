package uk.ac.mdx.reflerror;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class ReflErrorListener extends BaseErrorListener {

    private final ReflErrorHandler errorHandler;

    public ReflErrorListener(final ReflErrorHandler errorHandler) {
        super();
        this.errorHandler = errorHandler;
    }

    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer,
        final Object offendingSymbol,
        final int line, final int charPositionInLine,
        final String msg,
        final RecognitionException e)
    {
        errorHandler.addError((Token) offendingSymbol, msg);
    }

}