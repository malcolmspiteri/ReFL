package uk.ac.mdx.efrm;

import main.antlr.eFrmParser;
import main.antlr.eFrmParser.FieldDeclContext;
import main.antlr.eFrmParser.FieldsSectionContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.NumberRangeTypeContext;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

class FormBaseValVisitor extends FormBaseVisitor {

    STGroup group = new STGroupDir("st/basval", '$', '$');

    @Override
    public String visitForm(final FormContext ctx) {
        final ST st = group.getInstanceOf("form");
        st.add("rules", visit(ctx.fieldsAndRules().fieldsSection()));

        return st.render();
    }

    @Override
    public String visitFieldsSection(final FieldsSectionContext ctx) {
        final StringBuilder sb = new StringBuilder();
        for (final eFrmParser.FieldDeclContext fdc : ctx.fieldDecl()) {
            final String r = visit(fdc);
            if (r != null) {
                sb.append(r);
            }
        }
        return sb.toString();
    }

    @Override
    public String doVisitFieldDecl(final FieldDeclContext ctx) {
        return visit(ctx.type());
    }

    @Override
    public String visitNumberRangeType(final NumberRangeTypeContext ctx) {
        final ST stf = group.getInstanceOf("numberControl");
        stf.add("id", getCurrFieldNestedId("_"));
        stf.add("lb", ctx.INT(0).getText());
        stf.add("ub", ctx.INT(1).getText());
        return stf.render();
    }

}
