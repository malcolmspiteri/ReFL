package uk.ac.mdx.efrm;

import java.util.ArrayList;
import java.util.List;

import main.antlr.eFrmParser;
import main.antlr.eFrmParser.AskStatContext;
import main.antlr.eFrmParser.FieldsSectionContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.GroupTypeContext;
import main.antlr.eFrmParser.HeaderStatContext;
import main.antlr.eFrmParser.LayoutContext;
import main.antlr.eFrmParser.LayoutStatContext;
import main.antlr.eFrmParser.NumberRangeTypeContext;
import main.antlr.eFrmParser.OptionDeclContext;
import main.antlr.eFrmParser.OptionTypeContext;
import main.antlr.eFrmParser.RulesSectionContext;
import main.antlr.eFrmParser.StringTypeContext;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

class FormJsGenerator extends FormBaseVisitor {

    STGroup group = new STGroupDir("st/js", '$', '$');

    @Override
    public String visitForm(final FormContext ctx) {
        final FieldDef fd = getFieldDef(ctx.formDecl().labeledId());

        final ST st = group.getInstanceOf("form");
        st.add("id", fd.getId());
        st.add("label", fd.getLabel());
        st.add("fieldsDecl", visit(ctx.fieldsAndRules().fieldsSection()));
        st.add("stats", visit(ctx.fieldsAndRules().rulesSection()));
        return st.render();
    }

    public FormJsGenerator() {
        super();
        colSizes.add("12");
    }

    @Override
    public String visitRulesSection(final RulesSectionContext ctx) {
        return generateFormRenderCode(ctx.layout());
    }

    private int noCols = 1;
    private int currCol = 1;
    private final List<String> colSizes = new ArrayList<String>();

    @Override
    public String visitLayoutStat(final LayoutStatContext ctx) {
        colSizes.clear();
        noCols = ctx.INT().size();
        currCol = 1;
        for (int i = 0; i < ctx.INT().size(); i++) {
            colSizes.add(ctx.INT(i).getText());
        }
        return null;
    }

    private String generateFormRenderCode(final List<LayoutContext> lcs) {
        final ST st = group.getInstanceOf("layout");
        for (final eFrmParser.LayoutContext lc : lcs) {
            if (lc instanceof LayoutStatContext) {
                visit(lc);
                continue;
            }
            final StringBuilder sb = new StringBuilder();
            if (currCol == 1) {
                sb.append(newRow());
            }
            sb.append(newCell());
            sb.append(visit(lc));
            st.add("stat", sb.toString());
            currCol = currCol == noCols ? 1 : currCol + 1;
        }
        return st.render();
    }

    private String newRow() {
        return group.getInstanceOf("row").render();
    }

    private String newCell() {
        final String colSize = colSizes.get(currCol - 1);
        final ST st = group.getInstanceOf("cell");
        st.add("size", colSize);
        return st.render();

    }

    @Override
    public String visitHeaderStat(final HeaderStatContext ctx) {
        final ST st = group.getInstanceOf("header");
        st.add("level", ctx.INT().getText());
        st.add("text", sanitiseString(ctx.STRING().getText()));
        return st.render();
    }

    @Override
    public String visitAskStat(final AskStatContext ctx) {
        return " this." + ctx.ID().getText() + ".render(cellEl);";
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
        return visit(ctx.type());
    }

    @Override
    public String visitStringType(final StringTypeContext ctx) {
        final ST stf = group.getInstanceOf("stringField");
        stf.add("id", getCurrFieldId());
        stf.add("label", getCurrFieldLabel());
        stf.add("maxlength", ctx.INT().getText());
        return stf.render();
    }

    @Override
    public String visitNumberRangeType(final NumberRangeTypeContext ctx) {
        final ST stf = group.getInstanceOf("numberRangeField");
        stf.add("id", getCurrFieldId());
        stf.add("label", getCurrFieldLabel());
        stf.add("min", ctx.INT(0).getText());
        stf.add("max", ctx.INT(1).getText());
        return stf.render();
    }

    @Override
    public String visitOptionType(final OptionTypeContext ctx) {
        final ST stf = group.getInstanceOf("optionField");
        stf.add("id", getCurrFieldId());
        stf.add("label", getCurrFieldLabel());
        for (final OptionDeclContext odc : ctx.optionDecl()) {
            stf.add("option", visit(odc));
        }
        return stf.render();
    }

    @Override
    public String visitOptionDecl(final OptionDeclContext ctx) {
        final ST stf = group.getInstanceOf("option");
        final FieldDef fd = getFieldDef(ctx.labeledId());
        stf.add("id", fd.getId());
        stf.add("label", fd.getLabel());
        return stf.render();
    }

    @Override
    public String visitGroupType(final GroupTypeContext ctx) {
        final ST stf = group.getInstanceOf("field");
        stf.add("id", getCurrFieldId());
        stf.add("label", getCurrFieldLabel());
        return stf.render();
    }

}
