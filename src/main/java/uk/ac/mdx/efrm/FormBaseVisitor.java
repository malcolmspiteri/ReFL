package uk.ac.mdx.efrm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.antlr.eFrmBaseVisitor;
import main.antlr.eFrmParser;
import main.antlr.eFrmParser.GroupDeclContext;
import main.antlr.eFrmParser.LabeledIdContext;

abstract class FormBaseVisitor extends eFrmBaseVisitor<String> {

	protected static class FieldDef {
		private final String label;
		private final String id;

		public FieldDef(final String label, final String id) {
			this.label = label;
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public String getId() {
			return id;
		}

	}

	protected FieldDef getFieldDef(final LabeledIdContext ctx) {
		final String label = ctx.STRING() == null ? ctx.ID().getText() : ctx
				.STRING().getText().substring(1, ctx.STRING().getText().lastIndexOf("\""));
		final String id = ctx.ID().getText();
		return new FieldDef(label, id);
	}

	public abstract String doVisitFieldDecl(
			final eFrmParser.FieldDeclContext ctx);

	@Override
	public final String visitFieldDecl(final eFrmParser.FieldDeclContext ctx) {

		FieldDef fd = getFieldDef(ctx.labeledId());
		try {
			fieldsTree.add(fd);
			return doVisitFieldDecl(ctx);
		} finally {
			fieldsTree.remove(fd);
		}

	}

	List<FieldDef> fieldsTree = new LinkedList<FieldDef>();

	protected String getCurrFieldId() {
    	return fieldsTree.get(fieldsTree.size()-1).getId();
    }

	protected String getCurrFieldLabel() {
    	return fieldsTree.get(fieldsTree.size()-1).getLabel();
    }

	protected String getCurrFieldNestedId(String separator) {
		if (fieldsTree.size() == 1) {
			return fieldsTree.get(0).getId();
		}
		StringBuilder sb = new StringBuilder();
		for (FieldDef df : fieldsTree) {
			sb.append(df.getId() + separator);
		}
		String ret = sb.toString();
		return ret.substring(0, ret.length() - 1);
	}

	private Map<String, GroupDeclContext> groups = new HashMap<String, GroupDeclContext>();
	
	protected GroupDeclContext getGroupContext(String id) {
		return groups.get(id);
	}
	
	@Override
	public String visitGroupDecl(GroupDeclContext ctx) {
		groups.put(ctx.ID().getText(), ctx);
		return null;
	}
	
	

}
