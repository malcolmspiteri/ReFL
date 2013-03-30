package uk.ac.mdx.efrm.scope;

public class VariableSymbol extends Symbol {

    private String grpName;

    public VariableSymbol(final String name, final Type type, final Section section) {
        super(name, type, section);
    }

    public VariableSymbol(final String name, final Type type, final Section section, final String grpName) {
        super(name, type, section);
        this.grpName = grpName;
    }

    public String getGrpName() {
        return grpName;
    }

    @Override
    public String toString() {
        if (grpName != null) {
            return '<' + getName() + ":" + type + "[" + grpName + "]>";
        } else {
            return super.toString();
        }
    }

}
