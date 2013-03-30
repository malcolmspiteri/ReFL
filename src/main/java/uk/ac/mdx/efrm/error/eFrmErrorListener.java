package uk.ac.mdx.efrm.error;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class eFrmErrorListener extends BaseErrorListener {

    private final eFrmErrorHandler errorHandler;

    public eFrmErrorListener(final eFrmErrorHandler errorHandler) {
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