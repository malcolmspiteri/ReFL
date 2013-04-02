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
import main.antlr.eFrmParser.GreaterThanExprContext;
import main.antlr.eFrmParser.GreaterThanOrEqualExprContext;
import main.antlr.eFrmParser.GridStatContext;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.GroupTypeContext;
import main.antlr.eFrmParser.HeaderStatContext;
import main.antlr.eFrmParser.IDExprContext;
import main.antlr.eFrmParser.IdRefContext;
import main.antlr.eFrmParser.IfContStatContext;
import main.antlr.eFrmParser.InequalityExprContext;
import main.antlr.eFrmParser.InfoStatContext;
import main.antlr.eFrmParser.IntegerLiteralExprContext;
import main.antlr.eFrmParser.IsEmptyExprContext;
import main.antlr.eFrmParser.LabeledIdContext;
import main.antlr.eFrmParser.LayoutSectionContext;
import main.antlr.eFrmParser.LessThanExprContext;
import main.antlr.eFrmParser.LessThanOrEqualExprContext;
import main.antlr.eFrmParser.NewRowStatContext;
import main.antlr.eFrmParser.NoAskStatContext;
import main.antlr.eFrmParser.NotExprContext;
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
import main.antlr.eFrmParser.TableStatContext;
import main.antlr.eFrmParser.VarDecStatContext;
import main.antlr.eFrmParser.VarDeclContext;
import main.antlr.eFrmParser.WhileStatContext;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import uk.ac.mdx.efrm.scope.ArrayFieldSymbol;
import uk.ac.mdx.efrm.scope.GroupSymbol;
import uk.ac.mdx.efrm.scope.Scope;
import uk.ac.mdx.efrm.scope.Symbol;
import uk.ac.mdx.efrm.scope.VariableSymbol;
import uk.ac.mdx.efrm.scope.Symbol.Type;

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

    private String renderFormElement(String element) {
        final StringBuilder sb = new StringBuilder();
        if (gridState.currCol() == 1) {
            sb.append(gridState.newRow());
        }
        sb.append(gridState.newCell());
        sb.append(element);
        gridState.incrementCurrCol();
        return sb.toString();    	
    }
    
    private String renderField(String parent, FieldDeclContext fdc) {
    	StringBuilder sb = new StringBuilder();
    	String fullName = parent + "." + fdc.labeledId().ID().getText();
    	Symbol s = currentScope.resolve(fdc.labeledId().ID().getText());    	
		if (s instanceof ArrayFieldSymbol) {    	
    		// We have to render the whole damn array
    		// Let's get the size first
    		int size = ((ArrayFieldSymbol) s).getSize();
    		for (int i = 0; i < size; i++) {
    	    	if (s.getCustomTypeName() != null) {
    	    		// Inline rendering of group
    	    		GroupDeclContext gdc = groups.get(s.getCustomTypeName());
    	    		Scope tmpScope = currentScope;
    	    		currentScope = scopes.get(gdc);
    	            for (final eFrmParser.FieldDeclContext lc : gdc.fieldsSection().fieldDecl()) {
    	                sb.append(renderField(fullName + "[" + i + "]", lc));
    	            }
    	            currentScope = tmpScope;
    	    	} else {
    	    		sb.append(renderFormElement("this." + fullName + "[" + i + "].render(els.pop());") + "\n");
    	    	}
    		}
    	} else {
	    	if (s.getCustomTypeName() != null) {
	    		// Inline rendering of group
	    		GroupDeclContext gdc = groups.get(s.getCustomTypeName());
	    		Scope tmpScope = currentScope;
	    		currentScope = scopes.get(gdc);
	            for (final eFrmParser.FieldDeclContext lc : gdc.fieldsSection().fieldDecl()) {
	                sb.append(renderField(fullName, lc));
	            }
	            currentScope = tmpScope;
	    	} else {
	    		sb.append(renderFormElement("this." + fullName + ".render(els.pop());"));
	    	}
    	}
    	return sb.toString();
    }
    
    @Override
    public String visitRenderStat(final RenderStatContext ctx) {    	
    	StringBuilder sb = new StringBuilder();
    	Symbol s = currentScope.resolve(ctx.idRef().ID().getText());
        if (gridState.mode() == GridMode.INLINE) {		                	
    	
			if (s instanceof ArrayFieldSymbol && ctx.idRef().expr() == null) {    	
		    	if (s.getCustomTypeName() != null) {
		    		// Inline rendering of group
		    		GroupDeclContext gdc = groups.get(s.getCustomTypeName());
		    		Scope tmpScope = currentScope;
		    		currentScope = scopes.get(gdc);
		    		int size = ((ArrayFieldSymbol) s).getSize();
		    		for (int i = 0; i < size; i++) {
			            for (final eFrmParser.FieldDeclContext lc : gdc.fieldsSection().fieldDecl()) {
			                sb.append(renderField(ctx.idRef().getText() + "[" + i + "]", lc));		                	
			            }
		    		}
		            currentScope = tmpScope;
		    	} else {
		    		// We have to render the whole damn array
		    		// Let's get the size first
		    		int size = ((ArrayFieldSymbol) s).getSize();
		    		for (int i = 0; i < size; i++) {
		    			sb.append(renderFormElement("this." + visit(ctx.idRef()) + "[" + i + "].render(els.pop());") + "\n");
		    		}
		    	}
	    	} else {
		    	if (s.getCustomTypeName() != null) {
		    		// Inline rendering of group
		    		GroupDeclContext gdc = groups.get(s.getCustomTypeName());
		    		Scope tmpScope = currentScope;
		    		currentScope = scopes.get(gdc);
		            for (final eFrmParser.FieldDeclContext lc : gdc.fieldsSection().fieldDecl()) {
		                sb.append(renderField(ctx.idRef().getText(), lc));
		            }
		            currentScope = tmpScope;
		    	} else {
		    		sb.append(renderFormElement("this." + visit(ctx.idRef()) + ".render(els.pop());"));
		    	}
	    	}
        } else {
			if (s instanceof ArrayFieldSymbol && ctx.idRef().expr() == null) {
	    		int size = ((ArrayFieldSymbol) s).getSize();
	    		for (int i = 0; i < size; i++) {
	            	sb.append(renderFormElement("this." + visit(ctx.idRef()) + "[" + i + "].render(els.pop());"));
	    		}				
			} else {
	    		sb.append(renderFormElement("this." + visit(ctx.idRef()) + ".render(els.pop());"));				
			}        	
        }
    	return sb.toString();
    }

    
    @Override
    public String visitGridStat(final GridStatContext ctx) {
    	DefaultGridState dgs = new DefaultGridState();
    	dgs.colSizes.clear();
    	dgs.numCols(ctx.INT().size());
    	dgs.currCol(1);
        for (int i = 0; i < ctx.INT().size(); i++) {
        	dgs.colSizes.add(ctx.INT(i).getText());
        }
        if (ctx.INLINE() != null) {
        	dgs.mode(GridMode.INLINE);
        } else {
        	dgs.mode(GridMode.BLOCK);        	
        }
        gridState = dgs;
        return ""; // nothing to generate
    }

    @Override
    public String visitSkipStat(final SkipStatContext ctx) {
        final ST st = group.getInstanceOf("skip");
        return renderFormElement(st.render());
    }

    @Override
    public String visitNewRowStat(final NewRowStatContext ctx) {
        gridState.currCol(1);
        return null;
    }

    @Override
    public String visitHeaderStat(final HeaderStatContext ctx) {
        final ST st = group.getInstanceOf("header");
        st.add("level", ctx.INT().getText());
        st.add("text", sanitiseString(ctx.STRING().getText()));
        return renderFormElement(st.render());
    }

    @Override
    public String visitInfoStat(final InfoStatContext ctx) {
        final ST st = group.getInstanceOf("info");
        st.add("text", sanitiseString(ctx.STRING().getText()));
        return renderFormElement(st.render());
    }

    @Override
	public String visitTableStat(TableStatContext ctx) {
    	TableGridState tgs = new TableGridState();
    	tgs.numCols(ctx.STRING().size());
        final ST st = group.getInstanceOf("table");
    	for (int i = 0; i < ctx.STRING().size(); i++) {    		
            st.add("header", "<td><strong>" + sanitiseString(ctx.STRING(i).getText()) + "</strong></td>");
    	}
    	tgs.currCol(1);
        if (ctx.INLINE() != null) {
        	tgs.mode(GridMode.INLINE);
        } else {
        	tgs.mode(GridMode.BLOCK);        	
        }
    	gridState = tgs;
		return st.render();
	}

	@Override
    public String visitLayoutSection(final LayoutSectionContext ctx) {
        if (gridState != null) {
            gridStates.push(gridState);    			
        }
        gridState = new DefaultGridState();
        try {
            final ST st = group.getInstanceOf("layout");
            for (final eFrmParser.LayoutContext lc : ctx.layout()) {
                st.add("stat", visit(lc));
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
        if (ctx.expr() != null) {
            sb.append(String.format("[%s]", addValIfNecessary(visit(ctx.expr()))));
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

    
    private enum GridMode {
    	BLOCK, INLINE
    }
    
    private interface GridState {
    	
    	void incrementCurrCol();
    	
    	void numCols(int val);

		void currCol(int v);

		int numCols();

    	int currCol();

	    String newRow();

	    String newCell();
	    
	    GridMode mode();
	    
	    void mode(GridMode mode);
	    
    }
    
    private class DefaultGridState implements GridState {
        private int noCols = 1;
        private int currCol = 1;
        List<String> colSizes = new ArrayList<String>();

        public void incrementCurrCol() {
            currCol = currCol != noCols ?
                currCol + 1 :
                1;
        }

        DefaultGridState() {
            super();
            colSizes.add("12");
        }

		@Override
		public int currCol() {
			return currCol;
		}

		@Override
		public void currCol(int v) {
			currCol = v;
		}

		@Override
		public int numCols() {
			return noCols;
		}

		@Override
		public void numCols(int val) {
			noCols = val;
		}

		@Override
		public String newRow() {
	        return group.getInstanceOf("row").render();
	    }

		@Override
		public String newCell() {
	    	DefaultGridState dgs = (DefaultGridState) gridState;
	        final String colSize = dgs.colSizes.get(dgs.currCol() - 1);
	        final ST st = group.getInstanceOf("cell");
	        st.add("size", colSize);
	        return st.render();

	    }

        private GridMode mode;

        @Override
		public GridMode mode() {
			return mode;
		}

		@Override
		public void mode(GridMode mode) {
			this.mode = mode;
		}
		
    }

    private class TableGridState implements GridState {
        private int noCols = 1;
        private int currCol = 1;

        public void incrementCurrCol() {
            currCol = currCol != noCols ?
                currCol + 1 :
                1;
        }

        TableGridState() {
            super();            
        }

		@Override
		public int currCol() {
			return currCol;
		}

		@Override
		public void currCol(int v) {
			currCol = v;
		}

		@Override
		public int numCols() {
			return noCols;
		}

		@Override
		public void numCols(int val) {
			noCols = val;
		}

		@Override
		public String newRow() {
	        return group.getInstanceOf("tableRow").render();
	    }

		@Override
		public String newCell() {
	        final ST st = group.getInstanceOf("tableCell");
	        return st.render();

	    }

        private GridMode mode;

        @Override
		public GridMode mode() {
			return mode;
		}

		@Override
		public void mode(GridMode mode) {
			this.mode = mode;
		}

    }

    private GridState gridState;

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
	public String visitNotExpr(NotExprContext ctx) {
        return "!(" + visit(ctx.expr()) + ")";
	}
    
    @Override
	public String visitIsEmptyExpr(IsEmptyExprContext ctx) {
		return visit(ctx.expr()) + ".isEmpty()";
	}

	@Override
    public String visitEqualityExpr(final EqualityExprContext ctx) {
        return String.format("areEqual(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
            addValIfNecessary(visit(ctx.expr(1))));
    }

	@Override
    public String visitInequalityExpr(final InequalityExprContext ctx) {
        return String.format("!(areEqual(%s,%s))", addValIfNecessary(visit(ctx.expr(0))),
            addValIfNecessary(visit(ctx.expr(1))));
    }

	@Override
    public String visitLessThanExpr(final LessThanExprContext ctx) {
        return String.format("lessThan(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
            addValIfNecessary(visit(ctx.expr(1))));
    }

	@Override
    public String visitLessThanOrEqualExpr(final LessThanOrEqualExprContext ctx) {
        return "(" + String.format("lessThan(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
            addValIfNecessary(visit(ctx.expr(1)))) + " || " +
            String.format("areEqual(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
                    addValIfNecessary(visit(ctx.expr(1)))) + ")";
    }

	@Override
    public String visitGreaterThanExpr(final GreaterThanExprContext ctx) {
        return String.format("greaterThan(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
            addValIfNecessary(visit(ctx.expr(1))));
    }

	@Override
    public String visitGreaterThanOrEqualExpr(final GreaterThanOrEqualExprContext ctx) {
        return "(" + String.format("greaterThan(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
                addValIfNecessary(visit(ctx.expr(1)))) + " || " +
                String.format("areEqual(%s,%s)", addValIfNecessary(visit(ctx.expr(0))),
                        addValIfNecessary(visit(ctx.expr(1)))) + ")";
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
    public String visitWhileStat(final WhileStatContext ctx) {
        final ST stf = group.getInstanceOf("whileStmt");
        stf.add("expr", visit(ctx.expr()));
        for (final StatContext sc : ctx.stat()) {
            stf.add("stat1", visit(sc));
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
