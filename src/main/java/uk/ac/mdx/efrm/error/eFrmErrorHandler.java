package uk.ac.mdx.efrm.error;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

public class eFrmErrorHandler {

    List<eFrmError> errors = new ArrayList<eFrmError>();

    public void addError(final Token t, final String msg) {
        final String input = t.getTokenSource().getInputStream().toString();
        final String[] lines = input.split("\n");
        final eFrmError err = new eFrmError();
        err.setLineNumber(t.getLine());
        err.setLineText(lines[t.getLine() - 1]);
        err.setStart(t.getStartIndex());
        err.setStop(t.getStopIndex());
        err.setMessage(msg);
        err.setCharPositionInLine(t.getCharPositionInLine());

        errors.add(err);

    }

    public String reportError(final eFrmError error) {

        final StringBuilder sb = new StringBuilder();
        sb.append("<div class='container-fluid'>");
        sb.append("<div class='row-fluid'>");
        sb.append("<div class='span12'>");
        sb.append("<dl class='dl-horizontal'>");
        sb.append("<dt>Line number:</dt>");
        sb.append("<dd>" + error.getLineNumber() + "</dd>");
        sb.append("<dt>Error message:</dt>");
        sb.append("<dd>" + error.getMessage() + "</dd>");
        sb.append("</dl>");
        sb.append("<pre>");
        sb.append(error.getLineText());
        sb.append("\n");
        for (int i = 0; i < error.getCharPositionInLine(); i++) {
            sb.append("&nbsp;");
        }
        final int start = error.getStart();
        final int stop = error.getStop();
        if ((start >= 0) && (stop >= 0)) {
            for (int i = start; i <= stop; i++) {
                sb.append("^");
            }
        }
        sb.append("</pre>");
        sb.append("</div>"); // <div class='span12'>);
        sb.append("</div>"); // <div class='row-fluid'>);
        sb.append("</div>"); // <div class='container-fluid'>

        return sb.toString();

        // final CommonTokenStream tokens =
        // (CommonTokenStream) recognizer.getInputStream();
        //
        // final String input = tokens.getTokenSource().getInputStream().toString();
        // final String[] lines = input.split("\n");
        //
        // final String errorLine = lines[line - 1];
        // System.err.println(errorLine);
        // for (int i = 0; i < charPositionInLine; i++) {
        // System.err.print(" ");
        // }
        // final int start = offendingToken.getStartIndex();
        // final int stop = offendingToken.getStopIndex();
        // if ((start >= 0) && (stop >= 0)) {
        // for (int i = start; i <= stop; i++) {
        // System.err.print("^");
        // }
        // }
        // System.err.println();
    }

    public String generateErrorReport() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<div class='page-header'>");
        sb.append("<h4>Errors detected during compilation</h4>");
        sb.append("</div>");
        for (final eFrmError err : errors) {
            sb.append(reportError(err));
        }
        return sb.toString();
    }

    public boolean hasErrors() {
        if (errors.size() > 0) {
            return true;
        }

        return false;

    }

}
