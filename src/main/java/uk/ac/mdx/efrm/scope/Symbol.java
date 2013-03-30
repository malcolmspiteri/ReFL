package uk.ac.mdx.efrm.scope;

public class Symbol {
    public static enum Type {
        tINVALID, tGROUP, tGROUP_REF, tNUMBER, tSTRING, tOPTION, tBOOLEAN;
    }

    public static enum Section {
        FIELDS, RULES
    }

    String name; // All symbols at least have a name
    Type type;
    Scope scope; // All symbols know what scope contains them.
    Section section; // The section where the symbol is declared

    public Symbol(final String name) {
        this.name = name;
    }

    public Symbol(final String name, final Type type, final Section section) {
        this(name);
        this.type = type;
        this.section = section;
    }

    public Type getType() {
        return type;
    }

    public Section getSection() {
        return section;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return '<' + getName() + ":" + type + '>';
    }

}
