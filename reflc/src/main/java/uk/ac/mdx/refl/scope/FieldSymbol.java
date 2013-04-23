package uk.ac.mdx.refl.scope;

public class FieldSymbol extends Symbol {

    public FieldSymbol(final String name, final Type type) {
        super(name, type, null);
    }

    public FieldSymbol(final String name, final Type type, final String custTypeName) {
        super(name, type, custTypeName);
    }

    @Override
    public String toString() {
        return super.toString();
    }
    

}
