package uk.ac.mdx.efrm.scope;

public class FormScope extends DefaultScope {
    public FormScope(final Scope enclosingScope) {
        super(enclosingScope);
    }

    @Override
    public String getScopeName() {
        return "form";
    }
}
