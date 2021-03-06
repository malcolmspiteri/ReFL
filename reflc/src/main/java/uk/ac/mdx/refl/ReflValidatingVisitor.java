package uk.ac.mdx.refl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import main.antlr.eFrmBaseVisitor;
import main.antlr.eFrmParser.AndExprContext;
import main.antlr.eFrmParser.ArithmeticExprContext;
import main.antlr.eFrmParser.AssignStatContext;
import main.antlr.eFrmParser.BracketedExprContext;
import main.antlr.eFrmParser.ElseBlockContext;
import main.antlr.eFrmParser.EqualityExprContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GreaterThanExprContext;
import main.antlr.eFrmParser.GreaterThanOrEqualExprContext;
import main.antlr.eFrmParser.GridStatContext;
import main.antlr.eFrmParser.HeaderStatContext;
import main.antlr.eFrmParser.IDExprContext;
import main.antlr.eFrmParser.IdRefContext;
import main.antlr.eFrmParser.IfContStatContext;
import main.antlr.eFrmParser.InequalityExprContext;
import main.antlr.eFrmParser.IntegerLiteralExprContext;
import main.antlr.eFrmParser.IsEmptyExprContext;
import main.antlr.eFrmParser.LessThanExprContext;
import main.antlr.eFrmParser.LessThanOrEqualExprContext;
import main.antlr.eFrmParser.NotExprContext;
import main.antlr.eFrmParser.OptionExprContext;
import main.antlr.eFrmParser.OrExprContext;
import main.antlr.eFrmParser.RenderStatContext;
import main.antlr.eFrmParser.StatContext;
import main.antlr.eFrmParser.StringLiteralExprContext;
import main.antlr.eFrmParser.SubformDeclContext;
import main.antlr.eFrmParser.SubformTypeContext;
import main.antlr.eFrmParser.WhileStatContext;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import uk.ac.mdx.refl.scope.ArrayFieldSymbol;
import uk.ac.mdx.refl.scope.Scope;
import uk.ac.mdx.refl.scope.SubformSymbol;
import uk.ac.mdx.refl.scope.Symbol;
import uk.ac.mdx.refl.scope.Symbol.Type;
import uk.ac.mdx.reflerror.ReflErrorHandler;

public class ReflValidatingVisitor extends eFrmBaseVisitor<Symbol.Type> {

    private final ParseTreeProperty<Scope> scopes;
    private final ReflErrorHandler errorHandler;
    private Scope currentScope; // resolve symbols starting in this scope

    public ReflValidatingVisitor(final ReflErrorHandler errorHandler, final ParseTreeProperty<Scope> scopes) {
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
    public Symbol.Type visitSubformDecl(final SubformDeclContext ctx) {
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

    private final List<String> ancestors = new ArrayList<String>();

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
            ancestors.add(ctx.idRef(i).ID().getText());
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
            if (var instanceof SubformSymbol) {
                errorHandler.addError(ctx.ID().getSymbol(), name + " is not a variable or field");
            }
            if (ctx.expr() != null) {
                // Temporary stash ancestors
                final String[] tmp = ancestors.toArray(new String[] {});
                ancestors.clear();
                final Symbol.Type e = visit(ctx.expr());
                ancestors.addAll(Arrays.asList(tmp));
                if (e != Type.tNUMBER) {
                    errorHandler.addError(ctx.expr().getStart(), "dimension is not a number");
                }

            }
            if ((ctx.expr() != null) && !(var instanceof ArrayFieldSymbol)) {
                errorHandler.addError(ctx.ID().getSymbol(), name + " is not an array");
            }
            if ((ctx.expr() == null) && (var instanceof ArrayFieldSymbol)) {
                // errorHandler.addError(ctx.ID().getSymbol(), name + " is an array");
            }
            if ((var instanceof ArrayFieldSymbol) && (ctx.expr() != null)) {
                return toNonArrayType(var.getType());
            } else {
                return var.getType();
            }
        }
    }

    private Symbol.Type toNonArrayType(final Symbol.Type type) {
        switch (type) {
            case tSUBFORM_REF_ARRAY:
                return Symbol.Type.tSUBFORM_REF;
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
    public Type visitIsEmptyExpr(final IsEmptyExprContext ctx) {
        if (!(ctx.expr() instanceof IDExprContext)) {
            errorHandler.addError(ctx.expr().getStart(), ctx.expr().getText() + " is not a field or variable");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Type visitNotExpr(final NotExprContext ctx) {
        final Type et = visit(ctx.expr());
        if (et != Type.tBOOLEAN) {
            errorHandler.addError(ctx.expr().getStart(), "Invalid expression");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Type visitAndExpr(final AndExprContext ctx) {
        final Type et1 = visit(ctx.expr(0));
        if (et1 != Type.tBOOLEAN) {
            errorHandler.addError(ctx.expr(0).getStart(), "Invalid expression");
        }
        final Type et2 = visit(ctx.expr(1));
        if (et2 != Type.tBOOLEAN) {
            errorHandler.addError(ctx.expr(1).getStart(), "Invalid expression");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Type visitOrExpr(final OrExprContext ctx) {
        final Type et1 = visit(ctx.expr(0));
        if (et1 != Type.tBOOLEAN) {
            errorHandler.addError(ctx.expr(0).getStart(), "Invalid expression");
        }
        final Type et2 = visit(ctx.expr(1));
        if (et2 != Type.tBOOLEAN) {
            errorHandler.addError(ctx.expr(1).getStart(), "Invalid expression");
        }
        return Type.tBOOLEAN;
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
    public Symbol.Type visitInequalityExpr(final InequalityExprContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));
        if (e1Type != e2Type) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible comparison");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Symbol.Type visitLessThanExpr(final LessThanExprContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));
        if (!((e1Type == Type.tNUMBER) && (e2Type == Type.tNUMBER)) &&
            !((e1Type == Type.tSTRING) && (e2Type == Type.tSTRING))) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible comparison");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Symbol.Type visitLessThanOrEqualExpr(final LessThanOrEqualExprContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));
        if (!((e1Type == Type.tNUMBER) && (e2Type == Type.tNUMBER)) &&
            !((e1Type == Type.tSTRING) && (e2Type == Type.tSTRING))) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible comparison");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Symbol.Type visitGreaterThanExpr(final GreaterThanExprContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));
        if (!((e1Type == Type.tNUMBER) && (e2Type == Type.tNUMBER)) &&
            !((e1Type == Type.tSTRING) && (e2Type == Type.tSTRING))) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible comparison");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Symbol.Type visitGreaterThanOrEqualExpr(final GreaterThanOrEqualExprContext ctx) {
        final Symbol.Type e1Type = visit(ctx.expr(0));
        final Symbol.Type e2Type = visit(ctx.expr(1));
        if (!((e1Type == Type.tNUMBER) && (e2Type == Type.tNUMBER)) &&
            !((e1Type == Type.tSTRING) && (e2Type == Type.tSTRING))) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible comparison");
        }
        return Type.tBOOLEAN;
    }

    @Override
    public Type visitBracketedExpr(final BracketedExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitWhileStat(final WhileStatContext ctx) {
        final Type et = visit(ctx.expr());
        if (et != Type.tBOOLEAN) {
            errorHandler.addError(ctx.expr().getStart(), "Invalid expression");
        }
        for (final StatContext sc : ctx.stat()) {
            visit(sc);
        }
        return null;
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
    public Symbol.Type visitSubformType(final SubformTypeContext ctx) {
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
