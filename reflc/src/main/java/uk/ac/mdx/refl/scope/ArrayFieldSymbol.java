package uk.ac.mdx.refl.scope;

public class ArrayFieldSymbol extends Symbol {

	private int size;
	
    public ArrayFieldSymbol(final String name, final Type type, int size) {
        super(name, type, null);
        this.setSize(size);
    }

    public ArrayFieldSymbol(final String name, final Type type, int size, final String custTypeName) {
        super(name, type, custTypeName);
        this.setSize(size);
    }

    @Override
    public String toString() {
        return super.toString();
    }

	public int getSize() {
		return size;
	}

	private void setSize(int size) {
		this.size = size;
	}
    

}
