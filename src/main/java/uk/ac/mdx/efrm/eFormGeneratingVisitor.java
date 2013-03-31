package uk.ac.mdx.efrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import main.antlr.eFrmBaseVisitor;
import main.antlr.eFrmParser;
import main.antlr.eFrmParser.ArithmeticExprContext;
import main.antlr.eFrmParser.AskStatContext;
import main.antlr.eFrmParser.AssignStatContext;
import main.antlr.eFrmParser.BracketedExprContext;
import main.antlr.eFrmParser.EqualityExprContext;
import main.antlr.eFrmParser.FieldDeclContext;
import main.antlr.eFrmParser.FieldsSectionContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GridStatContext;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.GroupTypeContext;
import main.antlr.eFrmParser.HeaderStatContext;
import main.antlr.eFrmParser.IDExprContext;
import main.antlr.eFrmParser.IdRefContext;
import main.antlr.eFrmParser.IfContStatContext;
import main.antlr.eFrmParser.InfoStatContext;
import main.antlr.eFrmParser.IntegerLiteralExprContext;
import main.antlr.eFrmParser.LabeledIdContext;
import main.antlr.eFrmParser.LayoutSectionContext;
import main.antlr.eFrmParser.NewRowStatContext;
import main.antlr.eFrmParser.NoAskStatContext;
import main.antlr.eFrmParser.NumberRangeTypeContext;
import main.antlr.eFrmParser.OptionDeclContext;
import main.antlr.eFrmParser.OptionExprContext;
import main.antlr.eFrmParser.OptionTypeContext;
import main.antlr.eFrmParser.RenderStatContext;
import main.antlr.eFrmParser.RulesSectionContext;
import main.antlr.eFrmParser.SkipStatContext;
import main.antlr.eFrmParser.StatContext;
import main.antlr.eFrmParser.StringLiteralExprContext;
import main.antlr.eFrmParser.StringTypeContext;
import main.antlr.eFrmParser.VarDecStatContext;
import main.antlr.eFrmParser.VarDeclContext;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import uk.ac.mdx.efrm.scope.Scope;
import uk.ac.mdx.efrm.scope.Symbol;
import uk.ac.mdx.efrm.scope.VariableSymbol;

class eFormGeneratingVisitor extends eFrmBaseVisitor<String> {

    private final ParseTreeProperty<Scope> scopes;
    private Scope currentScope;

    public eFormGeneratingVisitor(final ParseTreeProperty<Scope> scopes) {
        super();
        this.scopes = scopes;
    }

    STGroup group = new STGroupDir("st/js", '$', '$');

    private static class FieldDef {
        private final String label;
        private final String id;
        private final Symbol symbol;

        public FieldDef(final String label, final String id, final Symbol symbol) {
            this.label = label;
            this.id = id;
            this.symbol = symbol;
        }

        public String getLabel() {
            return label;
        }

        public String getId() {
            return id;
        }

        public Symbol getSymbol() {
            return symbol;
        }

    }

    private final Map<String, GroupDeclContext> groups = new HashMap<String, GroupDeclContext>();

    private GroupDeclContext getGroupContext(final String id) {
        return groups.get(id);
    }

    private String genSetter(final FieldDeclContext fdc) {
        final ST st = group.getInstanceOf(fdc.ARRAY() == null ? "setter" : "arraySetter");
        st.add("id", fdc.labeledId().ID().getText());
        return st.render();
    }

    private String genGetter(final FieldDeclContext fdc) {
        final ST st = group.getInstanceOf(fdc.ARRAY() == null ? "getter" : "arrayGetter");
        st.add("id", fdc.labeledId().ID().getText());
        return st.render();
    }

    @Override
    public String visitGroupDecl(final GroupDeclContext ctx) {
        // Get the scope
        currentScope = scopes.get(ctx);

        groups.put(ctx.ID().getText(), ctx);

        final ST st = group.getInstanceOf("groupDef");
        st.add("id", ctx.ID().getText());
        // Process any nested groups
        for (final GroupDeclContext gdc : ctx.fieldsSection().groupDecl()) {
            st.add("groups", visit(gdc));
        }
        st.add("fields", visit(ctx.fieldsSection()));
        // Setters
        for (final FieldDeclContext fdc : ctx.fieldsSection().fieldDecl()) {
            st.add("setter", genSetter(fdc));
        }
        // Getters
        for (final FieldDeclContext fdc : ctx.fieldsSection().fieldDecl()) {
            st.add("getter", genGetter(fdc));
        }
        // Layout
        if ((ctx.layoutSection() != null) && !ctx.layoutSection().isEmpty()) {
            st.add("layout", visit(ctx.layoutSection()));
        }
        // Rules
        if ((ctx.rulesSection() != null) && !ctx.rulesSection().isEmpty()) {
            st.add("rules", visit(ctx.rulesSection()));
        }

        currentScope = currentScope.getEnclosingScope();

        return st.render();
    }

    @Override
    public String visitForm(final FormContext ctx) {
        // Get the scope
        currentScope = scopes.get(ctx);

        final FieldDef fd = getFieldDef(ctx.formDecl().labeledId());

        final ST st = group.getInstanceOf("form");
        st.add("id", fd.getId());
        st.add("label", fd.getLabel());
        for (final GroupDeclContext gdc : ctx.fieldsSection().groupDecl()) {
            st.add("groups", visit(gdc));
        }
        st.add("fields", visit(ctx.fieldsSection()));
        // Setters
        for (final FieldDeclContext fdc : ctx.fieldsSection().fieldDecl()) {
            st.add("setter", genSetter(fdc));
        }
        // Getters
        for (final FieldDeclContext fdc : ctx.fieldsSection().fieldDecl()) {
            st.add("getter", genGetter(fdc));
        }
        // Layout
        if ((ctx.layoutSection() != null) && !ctx.layoutSection().isEmpty()) {
            st.add("layout", visit(ctx.layoutSection()));
        }
        // Rules
        if ((ctx.rulesSection() != null) && !ctx.rulesSection().isEmpty()) {
            st.add("rules", visit(ctx.rulesSection()));
        }
        return st.render();
    }

    @Override
    public String visitLayoutSection(final LayoutSectionContext ctx) {
        if (gridState != null) {
            gridStates.push(gridState);
        }
        gridState = new GridState();
        try {
            final ST st = group.getInstanceOf("layout");
            for (final eFrmParser.LayoutContext lc : ctx.layout()) {
                if ((lc instanceof GridStatContext)
                    || (lc instanceof NewRowStatContext)) {
                    visit(lc);
                    continue;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("els.push(this.el);");
                if (gridState.currCol == 1) {
                    sb.append(newRow());
                }
                sb.append(newCell());
                sb.append(visit(lc));
                st.add("stat", sb.toString());
                gridState.incrementCurrCol();
            }
            return st.render();
        } finally {
            if (gridStates.size() > 0) {
                gridState = gridStates.pop();
            }

        }
    }

    @Override
    public String visitRulesSection(final RulesSectionContext ctx) {
        final ST st = group.getInstanceOf("rulesSection");
        for (final StatContext sc : ctx.stat()) {
            final String stat = visit(sc);
            st.add("stat", stat);
        }
        return st.render();
    }

    @Override
    public String visitAssignStat(final AssignStatContext ctx) {
        final String e0 = visit(ctx.expr(0));
        final String e1 = visit(ctx.expr(1));
        return e0 + ".val(" + addValIfNecessary(e1) + ");";
    }

    @Override
    public String visitVarDecStat(final VarDecStatContext ctx) {
        return visit(ctx.varDecl());
    }

    private String addValIfNecessary(final String expr) {
        if (!(expr.endsWith(".val()") || expr.endsWith(".val())"))) {
            return expr + ".val()";
        }
        return expr;
    }

    @Override
    public String visitVarDecl(final VarDeclContext ctx) {
        final StringBuilder sb = new StringBuilder("var " + ctx.ID().getText());
        sb.append(" = new Variable(");
        if (ctx.expr() != null) {
            sb.append(addValIfNecessary(visit(ctx.expr())));
        }
        sb.append(");");
        return sb.toString();
    }

    @Override
    public String visitIDExpr(final IDExprContext ctx) {
        final Symbol s = currentScope.resolve(ctx.idRef(0).ID().getText());
        final StringBuilder sb = new StringBuilder();
        if (s instanceof VariableSymbol) {
            return ctx.idRef(0).ID().getText();
        } else {
            sb.append("this.");
            for (int i = 0; i < ctx.idRef().size(); i++) {
                sb.append(visitIdRef(ctx.idRef(i)));
                if (i < (ctx.idRef().size() - 1)) {
                    sb.append(".");
                }
            }
            return sb.toString();
        }
    }

    @Override
    public String visitIdRef(final IdRefContext ctx) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ctx.ID().getText());
        if (ctx.INT() != null) {
            sb.append(String.format("[%s]", ctx.INT().getText()));
        }
        return sb.toString();
    }

    @Override
    public String visitStringLiteralExpr(final StringLiteralExprContext ctx) {
        return "new Variable(" + ctx.STRING().getText() + ")";
    }

    @Override
    public String visitIntegerLiteralExpr(final IntegerLiteralExprContext ctx) {
        return "new Variable(" + ctx.INT().getText() + ")";
    }

    private final Stack<GridState> gridStates = new Stack<GridState>();

    private static class GridState {
        int noCols = 1;
        int currCol = 1;
        List<String> colSizes = new ArrayList<String>();

        void incrementCurrCol() {
            currCol = currCol != noCols ?
                currCol + 1 :
                1;
        }

        GridState() {
            super();
            colSizes.add("12");
        }

    }

    private GridState gridState;

    @Override
    public String visitGridStat(final GridStatContext ctx) {
        gridState.colSizes.clear();
        gridState.noCols = ctx.INT().size();
        gridState.currCol = 1;
        for (int i = 0; i < ctx.INT().size(); i++) {
            gridState.colSizes.add(ctx.INT(i).getText());
        }
        return null;
    }

    @Override
    public String visitSkipStat(final SkipStatContext ctx) {
        final ST st = group.getInstanceOf("skip");
        return st.render();
    }

    @Override
    public String visitNewRowStat(final NewRowStatContext ctx) {
        gridState.currCol = 1;
        return null;
    }

    private String newRow() {
        return group.getInstanceOf("row").render();
    }

    private String newCell() {
        final String colSize = gridState.colSizes.get(gridState.currCol - 1);
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
    public String visitInfoStat(final InfoStatContext ctx) {
        final ST st = group.getInstanceOf("info");
        st.add("text", sanitiseString(ctx.STRING().getText()));
        return st.render();
    }

    @Override
    public String visitRenderStat(final RenderStatContext ctx) {
        return "els.push(this." + visit(ctx.idRef()) + ".render(els.pop()));";
    }

    @Override
    public String visitFieldsSection(final FieldsSectionContext ctx) {
        final ST st = group.getInstanceOf("fieldsSection");
        for (final eFrmParser.FieldDeclContext fdc : ctx.fieldDecl()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("this." + fdc.labeledId().ID().getText() + " = ");
            if (fdc.ARRAY() != null) {
                final int c = Integer.parseInt(fdc.INT().getText());
                sb.append("[");
                for (int i = 0; i < c; i++) {
                	String fr = visit(fdc);
                    sb.append(fr + ',');
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sb.append("];");
                st.add("field", sb.toString());
            } else {
                sb.append(visit(fdc) + ';');
                st.add("field", sb.toString());
            }
        }
        return st.render();
    }

    protected String sanitiseString(final String s) {
        return s.substring(1, s.lastIndexOf("\""))
            .replace("\"", "&quot;");
    }

    protected FieldDef getFieldDef(final LabeledIdContext ctx) {
        final String label = ctx.STRING() == null ? ctx.ID().getText() : sanitiseString(ctx.STRING().getText());
        final String id = ctx.ID().getText();
        final Symbol s = currentScope.resolve(ctx.ID().getText());
        return new FieldDef(label, id, s);
    }

    @Override
    public final String visitFieldDecl(final eFrmParser.FieldDeclContext ctx) {

        currField = getFieldDef(ctx.labeledId());
        return visit(ctx.type());

    }

    private FieldDef currField = null;

    private String getCurrFieldId() {
        return currField.getId();
    }

    private String getCurrFieldLabel() {
        return currField.getLabel();
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
        if (ctx.INT() != null) {
            stf.add("numSelectable", ctx.INT().getText());
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
        return "new " +
            getGroupContext(ctx.ID().getText()).ID().getText() +
            "('" + getCurrFieldId() + "','" + getCurrFieldLabel() + "')";
    }

    @Override
    public String visitArithmeticExpr(final ArithmeticExprContext ctx) {
        return addValIfNecessary(visit(ctx.expr(0))) +
            ctx.op.getText() +
            addValIfNecessary(visit(ctx.expr(1)));
    }

    @Override
    public String visitBracketedExpr(final BracketedExprContext ctx) {
        return "(" + addValIfNecessary(visit(ctx.expr())) + ")";
    }

    @Override
    public String visitOptionExpr(final OptionExprContext ctx) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < ctx.ID().size(); i++) {
            sb.append("\"");
            sb.append(ctx.ID(i).getText());
            sb.append("\"");
            if (i < (ctx.ID().size() - 1)) {
                sb.append(",");
            }
        }
        sb.append("]");
        return String.format("new Variable(%s)", sb.toString());
    }

    @Override
    public String visitEqualityExpr(final EqualityExprContext ctx) {
        return String.format("areEqual(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
            addValIfNecessary(visit(ctx.expr(1))));
    }

    @Override
    public String visitIfContStat(final IfContStatContext ctx) {
        final ST stf = group.getInstanceOf("ifStmt");
        stf.add("expr", visit(ctx.expr()));
        for (final StatContext sc : ctx.stat()) {
            stf.add("stat1", visit(sc));
        }
        if ((ctx.elseBlock() != null) && !ctx.elseBlock().isEmpty()) {
            for (final StatContext sc : ctx.elseBlock().stat()) {
                stf.add("stat2", visit(sc));
            }
        }
        return stf.render();
    }

    @Override
    public String visitNoAskStat(final NoAskStatContext ctx) {
        return visit(ctx.expr()) + ".disable();";
    }

    @Override
    public String visitAskStat(final AskStatContext ctx) {
        return visit(ctx.expr()) + ".enable();";
    }

}
