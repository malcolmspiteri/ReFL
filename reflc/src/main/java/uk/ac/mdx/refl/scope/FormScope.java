package uk.ac.mdx.refl.scope;

public class FormScope extends DefaultScope {
    public FormScope(final Scope enclosingScope) {
        super(enclosingScope);
    }

    @Override
    public String getScopeName() {
        return "form";
    }
}
