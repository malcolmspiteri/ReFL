package uk.ac.mdx.efrm;

import main.antlr.eFrmParser;
import main.antlr.eFrmParser.FieldsSectionContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.GroupTypeContext;
import main.antlr.eFrmParser.NumberRangeTypeContext;
import main.antlr.eFrmParser.OptionDeclContext;
import main.antlr.eFrmParser.OptionTypeContext;
import main.antlr.eFrmParser.StringTypeContext;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

class FormRendererVisitor extends FormBaseVisitor {

    STGroup group = new STGroupDir("st/render", '$', '$');

    @Override
    public String visitForm(final FormContext ctx) {
        final FieldDef fd = getFieldDef(ctx.formDecl().labeledId());

        final ST st = group.getInstanceOf("form");
        st.add("id", fd.getId());
        st.add("legend", fd.getLabel());
        st.add("fields", visit(ctx.fieldsAndRules().fieldsSection()));

        return st.render();
    }

    @Override
    public String visitFieldsSection(final FieldsSectionContext ctx) {
        // First we visit the groups
        for (final GroupDeclContext gdc : ctx.groupDecl()) {
        	visit(gdc);
        }
    	
    	final ST st = group.getInstanceOf("fieldsSection");
        for (final eFrmParser.FieldDeclContext fdc : ctx.fieldDecl()) {
            st.add("field", visit(fdc));
        }
        return st.render();
    }

    @Override
    public String doVisitFieldDecl(final eFrmParser.FieldDeclContext ctx) {
    	if (ctx.type() instanceof GroupTypeContext) {
    		return visit(ctx.type());
		}
    	
        final ST stf = group.getInstanceOf("field");

        stf.add("label", getCurrFieldLabel());
        stf.add("id", getCurrFieldNestedId("_"));
        stf.add("control", visit(ctx.type()));
        return stf.render();
        
    }

    @Override
    public String visitStringType(final StringTypeContext ctx) {
        final ST stf = group.getInstanceOf("stringControl");
        stf.add("label", getCurrFieldLabel());
        stf.add("id", getCurrFieldNestedId("_"));
        stf.add("maxlength", ctx.INT().getText());
        return stf.render();
    }

    @Override
    public String visitNumberRangeType(final NumberRangeTypeContext ctx) {
        final ST stf = group.getInstanceOf("numberControl");
        stf.add("label", getCurrFieldLabel());
        stf.add("id", getCurrFieldNestedId("_"));
        return stf.render();
    }

    @Override
    public String visitOptionType(final OptionTypeContext ctx) {
        final StringBuilder sb = new StringBuilder();
        for (final OptionDeclContext odc : ctx.optionDecl()) {
            sb.append(visit(odc));
        }
        return sb.toString();
    }

    @Override
    public String visitOptionDecl(final OptionDeclContext ctx) {
        final ST stf = group.getInstanceOf("optionControl");

        final FieldDef fd = getFieldDef(ctx.labeledId());

        stf.add("label", fd.getLabel());
        stf.add("id", fd.getId());
        stf.add("fid", getCurrFieldNestedId("_"));
        return stf.render();
    }

	@Override
	public String visitGroupType(GroupTypeContext ctx) {
        final ST stf = group.getInstanceOf("groupType");
        stf.add("id", getCurrFieldNestedId("_"));
        stf.add("fields", visit(getGroupContext(ctx.ID().getText()).fieldsAndRules().fieldsSection()));
        return stf.render();
	}
    
    

}
