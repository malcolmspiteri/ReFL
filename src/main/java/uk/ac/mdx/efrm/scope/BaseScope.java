package uk.ac.mdx.efrm.scope;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseScope implements Scope {

    Scope enclosingScope; // null if global (outermost) scope
    Map<String, Symbol> symbols = new LinkedHashMap<String, Symbol>();
    Map<String, Scope> subScopes = new LinkedHashMap<String, Scope>();

    public BaseScope(final Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    @Override
    public Symbol resolve(final String name) {
        final Symbol s = symbols.get(name);
        if (s != null) {
            return s;
        }
        // if not here, check any enclosing scope
        if (enclosingScope != null) {
            return enclosingScope.resolve(name);
        }
        return null; // not found
    }

    @Override
    public Scope getSubScope(final String name) {
        return subScopes.get(name);
    }

    @Override
    public void define(final Symbol sym) {
        symbols.put(sym.name, sym);
        sym.scope = this; // track the scope in each symbol
    }

    @Override
    public void defineSubScope(final String name, final Scope scope) {
        subScopes.put(name, scope);
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String toString() {
        return getScopeName() + ":" + subScopes.values() + ";" + symbols.values();
    }
}
