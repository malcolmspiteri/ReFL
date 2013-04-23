package uk.ac.mdx.refl.workshop.model;

import java.util.Date;

public class ReflForm {

    private int id;
    private String name;
    private String label;
    private int version;
    private String definition;
    private String compiled;
    private Date created;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public String getCompiled() {
        return compiled;
    }

    public void setCompiled(final String compiled) {
        this.compiled = compiled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
