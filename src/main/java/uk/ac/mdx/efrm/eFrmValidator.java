package uk.ac.mdx.efrm;

import java.util.Stack;

import main.antlr.eFrmBaseListener;
import main.antlr.eFrmParser.ArithmeticExprContext;
import main.antlr.eFrmParser.AssignStatContext;
import main.antlr.eFrmParser.EqualityExprContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GridStatContext;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.GroupTypeContext;
import main.antlr.eFrmParser.HeaderStatContext;
import main.antlr.eFrmParser.IDExprContext;
import main.antlr.eFrmParser.IntegerLiteralExprContext;
import main.antlr.eFrmParser.RenderStatContext;
import main.antlr.eFrmParser.StringLiteralExprContext;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import uk.ac.mdx.efrm.error.eFrmErrorHandler;
import uk.ac.mdx.efrm.scope.FieldSymbol;
import uk.ac.mdx.efrm.scope.GroupSymbol;
import uk.ac.mdx.efrm.scope.Scope;
import uk.ac.mdx.efrm.scope.Symbol;
import uk.ac.mdx.efrm.scope.Symbol.Type;

public class eFrmValidator extends eFrmBaseListener {

    private final ParseTreeProperty<Scope> scopes;
    private final eFrmErrorHandler errorHandler;
    private Scope currentScope; // resolve symbols starting in this scope

    public eFrmValidator(final eFrmErrorHandler errorHandler, final ParseTreeProperty<Scope> scopes) {
        super();
        this.errorHandler = errorHandler;
        this.scopes = scopes;
    }

    @Override
    public void enterForm(final FormContext ctx) {
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitForm(final FormContext ctx) {
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterGroupDecl(final GroupDeclContext ctx) {
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitGroupDecl(final GroupDeclContext ctx) {
        currentScope = currentScope.getEnclosingScope();
    }

    // Validate

    private final Stack<Symbol.Type> evalExpr = new Stack<Symbol.Type>();

    @Override
    public void exitIDExpr(final IDExprContext ctx) {
        Scope scope = currentScope;
        Symbol prevVar = null;
        for (final TerminalNode tn : ctx.ID()) {
            final String name = tn.getSymbol().getText();
            if ((prevVar != null) && (prevVar.getType() == Symbol.Type.tGROUP_REF)) {
                final String grpName = ((FieldSymbol) prevVar).getGrpName();
                scope = scope.getSubScope(grpName);
            }
            final Symbol var = scope.resolve(name);
            if (var == null) {
                errorHandler.addError(tn.getSymbol(), "no such variable or field: " + name);
            } else {
                if (var instanceof GroupSymbol) {
                    errorHandler.addError(tn.getSymbol(), name + " is not a variable or field");
                }
                evalExpr.push(var.getType());
            }
            prevVar = var;
        }
    }

    @Override
    public void exitStringLiteralExpr(final StringLiteralExprContext ctx) {
        evalExpr.push(Type.tSTRING);
    }

    @Override
    public void exitIntegerLiteralExpr(final IntegerLiteralExprContext ctx) {
        evalExpr.push(Type.tNUMBER);
    }

    @Override
    public void exitArithmeticExpr(final ArithmeticExprContext ctx) {
        final Symbol.Type e2Type = evalExpr.pop();
        final Symbol.Type e1Type = evalExpr.pop();
        if ((((e2Type == Symbol.Type.tSTRING) && (e1Type == Symbol.Type.tNUMBER)) ||
            ((e2Type == Symbol.Type.tNUMBER) && (e1Type == Symbol.Type.tSTRING))) &&
            (ctx.op.getText().equals("+"))) {
            evalExpr.push(Type.tSTRING);
        } else {
            if ((e2Type != Symbol.Type.tNUMBER) || (e1Type != Symbol.Type.tNUMBER)) {
                errorHandler.addError(ctx.expr(1).getStart(), "Incompatible arithmetic operation");
            }
            evalExpr.push(Type.tNUMBER);
        }
    }

    @Override
    public void exitEqualityExpr(final EqualityExprContext ctx) {
        final Symbol.Type e2Type = evalExpr.pop();
        final Symbol.Type e1Type = evalExpr.pop();
        if (e2Type != e1Type) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible comparison");
        }
        evalExpr.push(Type.tBOOLEAN);
    }

    @Override
    public void exitAssignStat(final AssignStatContext ctx) {
        final Symbol.Type e2Type = evalExpr.pop();
        final Symbol.Type e1Type = evalExpr.pop();
        if (e2Type != e1Type) {
            errorHandler.addError(ctx.expr(1).getStart(), "Incompatible assignment");
        }
    }

    @Override
    public void exitRenderStat(final RenderStatContext ctx) {
        final String name = ctx.ID().getSymbol().getText();
        final Symbol var = currentScope.resolve(name);
        if (var == null) {
            errorHandler.addError(ctx.ID().getSymbol(), "no such field: " + name);
        } else {
            if (var instanceof GroupSymbol) {
                errorHandler.addError(ctx.ID().getSymbol(), name + " is not a field");
            }
        }
    }

    @Override
    public void exitGroupType(final GroupTypeContext ctx) {
        final String name = ctx.ID().getSymbol().getText();
        final Symbol var = currentScope.resolve(name);
        if (var == null) {
            errorHandler.addError(ctx.ID().getSymbol(), "no such group: " + name);
        }
    }

    @Override
    public void exitHeaderStat(final HeaderStatContext ctx) {
        final int s = Integer.parseInt(ctx.INT().getText());
        if ((s < 1) || (s > 6)) {
            errorHandler.addError(ctx.INT().getSymbol(), "A header must be in the range of 1 to 6");
        }
    }

    @Override
    public void exitGridStat(final GridStatContext ctx) {
        int total = 0;
        for (int i = 0; i < ctx.INT().size(); i++) {
            total += Integer.parseInt(ctx.INT(i).getText());
        }
        if (total > 12) {
            errorHandler.addError(ctx.GRID().getSymbol(), "The sum of all grid columns sizes must exceed 12");
        }
    }

}
