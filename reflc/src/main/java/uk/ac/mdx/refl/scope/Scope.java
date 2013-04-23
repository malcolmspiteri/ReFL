package uk.ac.mdx.refl.scope;

public interface Scope {

    public String getScopeName();

    /** Where to look next for symbols */
    public Scope getEnclosingScope();

    /** Define a symbol in the current scope */
    public void define(Symbol sym);

    /** Define a sub-scope in the current scope */
    public void addSubScope(String name, Scope scope);

    /** Look up name in this scope or in enclosing scope if not here */
    public Symbol resolve(String name);

    /** Look up sub-scope in this scope by ies name */
    public Scope getSubScope(String name);

}
