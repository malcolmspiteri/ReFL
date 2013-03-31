package uk.ac.mdx.efrm.scope;

import main.antlr.eFrmBaseListener;
import main.antlr.eFrmParser;
import main.antlr.eFrmParser.FieldDeclContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.GroupTypeContext;
import main.antlr.eFrmParser.NumberRangeTypeContext;
import main.antlr.eFrmParser.OptionTypeContext;
import main.antlr.eFrmParser.ParamDeclContext;
import main.antlr.eFrmParser.StringTypeContext;
import main.antlr.eFrmParser.TypeContext;
import main.antlr.eFrmParser.VarDeclContext;
import main.antlr.eFrmParser.VarTypeContext;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class eFrmSymbolTableBuilder extends eFrmBaseListener {

    public static Symbol.Type getType(final int tokenType) {
        switch (tokenType) {
            case eFrmParser.GROUP:
                return Symbol.Type.tGROUP;
            case eFrmParser.NUMBER:
                return Symbol.Type.tNUMBER;
            case eFrmParser.INT:
                return Symbol.Type.tNUMBER;
            case eFrmParser.STRING_TYPE:
                return Symbol.Type.tSTRING;
            case eFrmParser.OPTION:
                return Symbol.Type.tOPTION;
        }
        return Symbol.Type.tINVALID;
    }

    private final ParseTreeProperty<Scope> scopes = new ParseTreeProperty<Scope>();
    private Scope currentScope; // define symbols in this scope

    public ParseTreeProperty<Scope> getScopes() {
        return scopes;
    }

    void saveScope(final ParserRuleContext ctx, final Scope s) {
        scopes.put(ctx, s);
    }

    @Override
    public void enterForm(final FormContext ctx) {
        final FormScope form = new FormScope(null);
        saveScope(ctx, form);
        currentScope = form;
    }

    @Override
    public void exitForm(final FormContext ctx) {
        System.out.println(currentScope);
    }

    @Override
    public void enterGroupDecl(final GroupDeclContext ctx) {
        final String name = ctx.ID().getText();
        final int typeTokenType = ctx.start.getType();
        final Symbol.Type type = getType(typeTokenType);

        // push new scope by making new one that points to enclosing scope
        final GroupSymbol group = new GroupSymbol(name, type, currentScope);
        currentScope.define(group); // Define group in current scope
        currentScope.defineSubScope(name, group);
        saveScope(ctx, group); // Push: set function's parent to current
        currentScope = group; // Current scope is now function scope
    }

    @Override
    public void exitGroupDecl(final GroupDeclContext ctx) {
        System.out.println(currentScope);
        currentScope = currentScope.getEnclosingScope(); // pop scope
    }

    @Override
    public void exitParamDecl(final ParamDeclContext ctx) {
        defineVar(ctx.varType(), ctx.ID().getSymbol());
    }

    @Override
    public void exitVarDecl(final VarDeclContext ctx) {
        defineVar(ctx.varType(), ctx.ID().getSymbol());
    }

    private Token currFieldId = null;

    @Override
    public void enterFieldDecl(final FieldDeclContext ctx) {
        currFieldId = ctx.labeledId().ID().getSymbol();
    }

    @Override
    public void exitOptionType(final OptionTypeContext ctx) {
        defineField(ctx, currFieldId);
    }

    @Override
    public void exitNumberRangeType(final NumberRangeTypeContext ctx) {
        defineField(ctx, currFieldId);
    }

    @Override
    public void exitGroupType(final GroupTypeContext ctx) {
        final Symbol grpSymbol = currentScope.resolve(ctx.ID().getText());
        if (grpSymbol != null) {
            defineGroupVar(ctx.ID().getText(), currFieldId);
        } else {
            defineField(ctx, currFieldId);
        }
    }

    @Override
    public void exitStringType(final StringTypeContext ctx) {
        defineField(ctx, currFieldId);
    }

    void defineGroupVar(final String grpName, final Token nameToken) {
        final FieldSymbol var = new FieldSymbol(nameToken.getText(), Symbol.Type.tGROUP_REF, grpName);
        currentScope.define(var); // Define symbol in current scope
    }

    void defineField(final TypeContext varTypeCtx, final Token nameToken) {
        final int typeTokenType = varTypeCtx.start.getType();
        final Symbol.Type type = getType(typeTokenType);
        final VariableSymbol var = new VariableSymbol(nameToken.getText(), type);
        currentScope.define(var); // Define symbol in current scope
    }

    void defineVar(final VarTypeContext varTypeCtx, final Token nameToken) {
        final int typeTokenType = varTypeCtx.start.getType();
        final Symbol.Type type = getType(typeTokenType);
        final VariableSymbol var = new VariableSymbol(nameToken.getText(), type);
        currentScope.define(var); // Define symbol in current scope
    }

}
