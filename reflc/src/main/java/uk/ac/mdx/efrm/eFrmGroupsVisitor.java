package uk.ac.mdx.efrm;

import java.util.HashMap;
import java.util.Map;

import main.antlr.eFrmBaseVisitor;
import main.antlr.eFrmParser;
import main.antlr.eFrmParser.FieldsSectionContext;
import main.antlr.eFrmParser.FormContext;
import main.antlr.eFrmParser.GroupDeclContext;

public class eFrmGroupsVisitor extends eFrmBaseVisitor<Map<String, eFrmParser.GroupDeclContext>> {

	Map<String, GroupDeclContext> groups = new HashMap<String, GroupDeclContext>();
	
	
	@Override
	public Map<String, GroupDeclContext> visitFieldsSection(
			FieldsSectionContext ctx) {
		Map<String, GroupDeclContext> groups = new HashMap<String, GroupDeclContext>();
		for (GroupDeclContext gd : ctx.groupDecl()) {
			groups.putAll(visit(gd));
		}
		return groups;
	}


	@Override
	public Map<String, GroupDeclContext> visitForm(FormContext ctx) {
		return visit(ctx.fieldsSection());
	}


	@Override
	public Map<String, GroupDeclContext> visitGroupDecl(GroupDeclContext ctx) {
		Map<String, GroupDeclContext> groups = new HashMap<String, GroupDeclContext>();
		groups.put(ctx.ID().getText(), ctx);
		groups.putAll(visit(ctx.fieldsSection()));
		return groups;
	}
	
}
