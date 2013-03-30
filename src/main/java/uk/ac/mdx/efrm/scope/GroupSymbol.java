package uk.ac.mdx.efrm.scope;

import java.util.LinkedHashMap;
import java.util.Map;

public class GroupSymbol extends Symbol implements Scope {
    Map<String, Symbol> arguments = new LinkedHashMap<String, Symbol>();
    Map<String, Scope> subScopes = new LinkedHashMap<String, Scope>();
    Scope enclosingScope;

    public GroupSymbol(final String name, final Type retType, final Scope enclosingScope) {
        super(name, retType, Section.FIELDS);
        this.enclosingScope = enclosingScope;
    }

    @Override
    public Symbol resolve(final String name) {
        final Symbol s = arguments.get(name);
        if (s != null) {
            return s;
        }
        // if not here, check any enclosing scope
        // if (getEnclosingScope() != null) {
        // return getEnclosingScope().resolve(name);
        // }
        return null; // not found
    }

    @Override
    public void define(final Symbol sym) {
        arguments.put(sym.name, sym);
        sym.scope = this; // track the scope in each symbol
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return name;
    }

    @Override
    public String toString() {
        return "group" + super.toString() + ":" + arguments.values();
    }

    @Override
    public void defineSubScope(final String name, final Scope scope) {
        subScopes.put(name, scope);
    }

    @Override
    public Scope getSubScope(final String name) {
        return subScopes.get(name);
    }
}
