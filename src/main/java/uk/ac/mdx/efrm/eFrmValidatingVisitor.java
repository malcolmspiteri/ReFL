package uk.ac.mdx.efrm;

import java.util.Iterator;
import java.util.Stack;

import main.antlr.eFrmBaseVisitor;
import main.antlr.eFrmParser.ArithmeticExprContext;
import main.antlr.eFrmParser.AssignStatContext;
import main.antlr.eFrmParser.BracketedExprContext;
import main.antlr.eFrmParser.ElseBlockContext;
import main.antlr.eFrmParser.EqualityExprContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GridStatContext;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.GroupTypeContext;
import main.antlr.eFrmParser.HeaderStatContext;
import main.antlr.eFrmParser.IDExprContext;
import main.antlr.eFrmParser.IdRefContext;
import main.antlr.eFrmParser.IfContStatContext;
import main.antlr.eFrmParser.IntegerLiteralExprContext;
import main.antlr.eFrmParser.OptionExprContext;
import main.antlr.eFrmParser.RenderStatContext;
import main.antlr.eFrmParser.StatContext;
import main.antlr.eFrmParser.StringLiteralExprContext;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import uk.ac.mdx.efrm.error.eFrmErrorHandler;
import uk.ac.mdx.efrm.scope.GroupSymbol;
import uk.ac.mdx.efrm.scope.Scope;
import uk.ac.mdx.efrm.scope.Symbol;
import uk.ac.mdx.efrm.scope.Symbol.Type;

public class eFrmValidatingVisitor extends eFrmBaseVisitor<Symbol.Type> {

    private final ParseTreeProperty<Scope> scopes;
    private final eFrmErrorHandler errorHandler;
    private Scope currentScope; // resolve symbols starting in this scope

    public eFrmValidatingVisitor(final eFrmErrorHandler errorHandler, final ParseTreeProperty<Scope> scopes) {
        super();
        this.errorHandler = errorHandler;
        this.scopes = scopes;
    }

    @Override
    public Symbol.Type visitForm(final FormContext ctx) {
        currentScope = scopes.get(ctx);
        visit(ctx.fieldsSection());
        if ((ctx.layoutSection() != null) && !ctx.layoutSection().isEmpty()) {
            visit(ctx.layoutSection());
        }
        if ((ctx.rulesSection() != null) && !ctx.rulesSection().isEmpty()) {
            visit(ctx.rulesSection());
        }
        currentScope = currentScope.getEnclosingScope();
        return null;
    }

    @Override
    public Symbol.Type visitGroupDecl(final GroupDeclContext ctx) {
        currentScope = scopes.get(ctx);
        visit(ctx.fieldsSection());
        if ((ctx.layoutSection() != null) && !ctx.layoutSection().isEmpty()) {
            visit(ctx.layoutSection());
        }
        if ((ctx.rulesSection() != null) && !ctx.rulesSection().isEmpty()) {
            visit(ctx.rulesSection());
        }
        currentScope = currentScope.getEnclosingScope();
        return null;
    }

    /****************** RULES ******************/

    private final Stack<String> ancestors = new Stack<String>();

    private String ancestors() {
        final Iterator<String> iter = ancestors.iterator();
        final StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            sb.append(iter.next());
            sb.append('.');
        }
        return sb.toString();
    }

    @Override
    public Symbol.Type visitIDExpr(final IDExprContext ctx) {
        Symbol.Type ret = null;
        for (int i = 0; i < ctx.idRef().size(); i++) {
            ret = visit(ctx.idRef(i));
            ancestors.push(ctx.idRef(i).ID().getText());
        }
        ancestors.clear();
        return ret;
    }

    @Override
    public Type visitIdRef(final IdRefContext ctx) {
        final String name = ctx.ID().getSymbol().getText();
        final Symbol var = currentScope.resolve(ancestors() + name);
        if (var == null) {
            errorHandler.addError(ctx.ID().getSymbol(), "no such variable or field: " + name);
            return Type.tINVALID;
        } else {
            if (var instanceof GroupSymbol) {
                errorHandler.addError(ctx.ID().getSymbol(), name + " is not a variable or field");
            }
            if ((ctx.INT() != null) && !var.getType().isArray()) {
                errorHandler.addError(ctx.ID().getSymbol(), name + " is not an array");
            }
            if ((ctx.INT() == null) && var.getType().isArray()) {
                errorHandler.addError(ctx.ID().getSymbol(), name + " is an array");
            }
            if (var.getType().isArray() && (ctx.INT() != null)) {
                return toNonArrayType(var.getType());
            } else {
                return var.getType();
            }
        }
    }

    private Symbol.Type toNonArrayType(final Symbol.Type type) {
        switch (type) {
            case tGROUP_REF_ARRAY:
                return Symbol.Type.tGROUP_REF;
            case tNUMBER_ARRAY:
                return Symbol.Type.tNUMBER;
            case tOPTION_ARRAY:
                return Symbol.Type.tOPTION;
            case tSTRING_ARRAY:
                return Symbol.Type.tSTRING;
        }
        return Symbol.Type.tINVALID;
    }

    // @Override
    // public Type visitNoAskStat(NoAskStatContext ctx) {
    // final String name = ctx.ID().getSymbol().getText();
    // final Symbol var = currentScope.resolve(name);
    // if (var == null || var.getSection() == Section.RULES) {
    // errorHandler.addError(ctx.expr().getSymbol(), "no such field: " + name);
    // }
    // return super.visitNoAskStat(ctx);
    // }

    @Override
    public Type visitOptionExpr(final OptionExprContext ctx) {
        return Type.tOPTION;
    }

    @Override
    public Symbol.Type visitStringLiteralExpr(final StringLiteralExprContext ctx) {
        return Type.tSTRING;
    }

    @Override
    public Symbol.Type visitIntegerLiteralExpr(final IntegerLiteralExprContext ctx) {
        return Type.tNUMBER;
    }

    @Override
    public Symbol.Type visitArithmeticExpr(final ArithmeticExprContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));

        if ((
            (
                (e1Type == Symbol.Type.tSTRING) &&
                (e2Type == Symbol.Type.tNUMBER)
                ) ||
                (
                (e1Type == Symbol.Type.tNUMBER) &&
                (e2Type == Symbol.Type.tSTRING)
                ) ||
            (
            (e1Type == Symbol.Type.tSTRING) &&
            (e2Type == Symbol.Type.tSTRING)
            )
            ) &&
            ctx.op.getText().equals("+")) {
            return Type.tSTRING;
        } else if ((e1Type != Symbol.Type.tNUMBER) || (e2Type != Symbol.Type.tNUMBER)) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible arithmetic operation");

            return Type.tINVALID;
        } else {
            return Type.tNUMBER;
        }
    }

    @Override
    public Symbol.Type visitEqualityExpr(final EqualityExprContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));
        if (e1Type != e2Type) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible comparison");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Type visitBracketedExpr(final BracketedExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitIfContStat(final IfContStatContext ctx) {
        final Type et = visit(ctx.expr());
        if (et != Type.tBOOLEAN) {
            errorHandler.addError(ctx.expr().getStart(), "Invalid expression");
        }
        for (final StatContext sc : ctx.stat()) {
            visit(sc);
        }
        visit(ctx.stat(0));
        if ((ctx.elseBlock() != null) && !ctx.elseBlock().isEmpty()) {
            visit(ctx.elseBlock());
        }
        return null;
    }

    @Override
    public Type visitElseBlock(final ElseBlockContext ctx) {
        for (final StatContext sc : ctx.stat()) {
            visit(sc);
        }
        return null;
    }

    @Override
    public Symbol.Type visitAssignStat(final AssignStatContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));
        if (e1Type != e2Type) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible assignment");
        }
        return null;
    }

    /****************** FIELDS ******************/

    @Override
    public Symbol.Type visitGroupType(final GroupTypeContext ctx) {
        final String name = ctx.ID().getText();
        final Symbol var = currentScope.resolve(name);
        if (var == null) {
            errorHandler.addError(ctx.ID().getSymbol(), "no such group: " + name);
            return Type.tINVALID;
        }
        return var.getType();
    }

    /****************** LAYOUT ******************/

    @Override
    public Symbol.Type visitRenderStat(final RenderStatContext ctx) {
        Symbol.Type ret = null;
        ret = visit(ctx.idRef());
        return ret;
    }

    @Override
    public Symbol.Type visitHeaderStat(final HeaderStatContext ctx) {
        final int s = Integer.parseInt(ctx.INT().getText());
        if ((s < 1) || (s > 6)) {
            errorHandler.addError(ctx.INT().getSymbol(), "A header must be in the range of 1 to 6");
        }
        return null;
    }

    @Override
    public Symbol.Type visitGridStat(final GridStatContext ctx) {
        int total = 0;
        for (int i = 0; i < ctx.INT().size(); i++) {
            total += Integer.parseInt(ctx.INT(i).getText());
        }
        if (total > 12) {
            errorHandler.addError(ctx.GRID().getSymbol(), "The sum of all grid columns sizes must exceed 12");
        }
        return null;
    }

}
