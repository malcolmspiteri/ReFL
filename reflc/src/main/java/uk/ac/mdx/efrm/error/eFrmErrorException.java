package uk.ac.mdx.efrm.error;

public class eFrmErrorException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String errorMsgHtml;

    public eFrmErrorException(final String errorMsgHtml) {
        super();
        this.errorMsgHtml = errorMsgHtml;
    }

    public String getErrorMsgHtml() {
        return errorMsgHtml;
    }

}
