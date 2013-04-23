package uk.ac.mdx.reflerror;

public class ReflErrorException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String errorMsgHtml;

    public ReflErrorException(final String errorMsgHtml) {
        super();
        this.errorMsgHtml = errorMsgHtml;
    }

    public String getErrorMsgHtml() {
        return errorMsgHtml;
    }

}
