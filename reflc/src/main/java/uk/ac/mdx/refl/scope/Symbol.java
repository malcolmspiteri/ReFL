package uk.ac.mdx.refl.scope;

public class Symbol {
    public static enum Type {
        tINVALID, tSUBFORM, tSUBFORM_REF, tNUMBER, tSTRING, tOPTION, tBOOLEAN,
        tSUBFORM_REF_ARRAY, tNUMBER_ARRAY, tSTRING_ARRAY, tOPTION_ARRAY;

    }

    protected final String name; // All symbols at least have a name
    protected Type type;
    protected Scope scope; // All symbols know what scope contains them.
    protected String custTypeName;

    public Symbol(final String name) {
        this.name = name;
    }

    public Symbol(final String name, final Type type, final String custTypeName) {
        this(name);
        this.type = type;
        this.custTypeName = custTypeName;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCustomTypeName() {
        return custTypeName;
    }

    @Override
    public String toString() {
        if (custTypeName != null) {
            return '<' + getName() + ":" + custTypeName + ">";
        } else {
            return '<' + getName() + ":" + type + '>';
        }
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(final Scope scope) {
        this.scope = scope;
    }

}
