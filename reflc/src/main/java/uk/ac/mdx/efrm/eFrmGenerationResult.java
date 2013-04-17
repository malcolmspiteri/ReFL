package uk.ac.mdx.efrm;

public class eFrmGenerationResult {

    private String name;
    private String label;
    private String generatedCode;

    public eFrmGenerationResult(final String name, final String label, final String generatedCode) {
        super();
        this.name = name;
        this.label = label;
        this.generatedCode = generatedCode;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(final String generatedCode) {
        this.generatedCode = generatedCode;
    }

}
