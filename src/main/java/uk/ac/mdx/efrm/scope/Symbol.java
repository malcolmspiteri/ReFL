package uk.ac.mdx.efrm.scope;

public class Symbol {
    public static enum Type {
        tINVALID, tGROUP, tGROUP_REF, tNUMBER, tSTRING, tOPTION, tBOOLEAN;
    }

    String name; // All symbols at least have a name
    Type type;
    Scope scope; // All symbols know what scope contains them.

    public Symbol(final String name) {
        this.name = name;
    }

    public Symbol(final String name, final Type type) {
        this(name);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return '<' + getName() + ":" + type + '>';
    }

}
