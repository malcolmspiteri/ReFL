package uk.ac.mdx.efrm.scope;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import uk.ac.mdx.efrm.scope.Symbol.Type;

public class DefaultScope implements Scope {

    Scope enclosingScope; // null if global (outermost) scope
    Map<String, Symbol> symbols = new LinkedHashMap<String, Symbol>();
    Map<String, Scope> subScopes = new LinkedHashMap<String, Scope>();

    public DefaultScope(final Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    @Override
    public Symbol resolve(final String name) {
        Symbol s = null;
        if (name.indexOf('.') == -1) {
            s = symbols.get(name);
        } else {
            final StringTokenizer st = new StringTokenizer(name, ".");
            Scope scp = this;
            while (st.hasMoreTokens()) {
                final String n = st.nextToken();
                s = scp.resolve(n);
                if (s == null) {
                    break;
                }
                if ((s.getType() == Type.tGROUP_REF) || (s.getType() == Type.tGROUP_REF_ARRAY)) {
                    scp = scp.getSubScope(s.getCustomTypeName());
                }
            }
        }

        if (s != null) {
            return s;
        }

        return null; // not found
    }

    @Override
    public Scope getSubScope(final String name) {
        return subScopes.get(name);
    }

    @Override
    public void define(final Symbol sym) {
        symbols.put(sym.getName(), sym);
        sym.setScope(this); // track the scope in each symbol
    }

    @Override
    public void addSubScope(final String name, final Scope scope) {
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

    @Override
    public String getScopeName() {
        return "default";
    }
}
