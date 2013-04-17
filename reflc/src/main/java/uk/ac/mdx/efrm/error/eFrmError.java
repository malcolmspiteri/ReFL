package uk.ac.mdx.efrm.error;

public class eFrmError {

    private int lineNumber;
    private String lineText;
    private int start;
    private int stop;
    private String message;
    private int charPositionInLine;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLineText() {
        return lineText;
    }

    public void setLineText(final String lineText) {
        this.lineText = lineText;
    }

    public int getStart() {
        return start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(final int stop) {
        this.stop = stop;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    public void setCharPositionInLine(final int charPositionInLine) {
        this.charPositionInLine = charPositionInLine;
    }

}
