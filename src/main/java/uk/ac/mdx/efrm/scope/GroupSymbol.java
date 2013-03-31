package uk.ac.mdx.efrm.scope;

public class GroupSymbol extends Symbol implements Scope {
    private final DefaultScope defaultScope;

    public GroupSymbol(final String name, final Type retType, final Scope enclosingScope) {
        super(name, retType, null);
        defaultScope = new DefaultScope(enclosingScope);
    }

    @Override
    public Symbol resolve(final String name) {
        return defaultScope.resolve(name);
    }

    @Override
    public void define(final Symbol sym) {
        defaultScope.define(sym);
    }

    @Override
    public Scope getEnclosingScope() {
        return defaultScope.getEnclosingScope();
    }

    @Override
    public String getScopeName() {
        return "group";
    }

    @Override
    public String toString() {
        return "group" + super.toString() + ":" + defaultScope.symbols.values();
    }

    @Override
    public void addSubScope(final String name, final Scope scope) {
        defaultScope.addSubScope(name, scope);
    }

    @Override
    public Scope getSubScope(final String name) {
        return defaultScope.getSubScope(name);
    }
}
