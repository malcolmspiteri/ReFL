package uk.ac.mdx.refl.test;

import static org.junit.Assert.assertTrue;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import uk.ac.mdx.refl.ReflGeneratingVisitor;

public class TestFields extends BaseReflUnitTest {

    @Test
    public void testStringField() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestStringField");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(res.indexOf("this.f1 = new StringField(\"f1\",\"f1\",\"10\");") > 0);

    }

    @Test
    public void testNumberField() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestNumberField");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(res.indexOf("this.f1 = new NumberRangeField(\"f1\",\"f1\",10, 20);") > 0);

    }

    @Test
    public void testOptionField() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestOptionField");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(stripWS(res)
            .indexOf(
                "this.f1=newOptionField(\"f1\",\"f1\",[{id:\"one\",label:\"one\"},{id:\"two\",label:\"two\"},{id:\"tre\",label:\"tre\"},{id:\"for\",label:\"for\"}]);") > 0);

    }

    @Test
    public void testArrayField() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestArrayField");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(res
            .indexOf(
            "this.f1 = [new StringField(\"f1\",\"f1\",\"10\"),new StringField(\"f1\",\"f1\",\"10\")")
        > 0);
        // System.out.println(res);

    }

    @Test
    public void testMultiOptionField() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestOptionField");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(stripWS(res)
            .indexOf(
                "this.f2=newOptionField(\"f2\",\"f2\",[{id:\"one\",label:\"one\"},{id:\"two\",label:\"two\"},{id:\"tre\",label:\"tre\"},{id:\"for\",label:\"for\"}],2);") > 0);

    }

    @Test
    public void testTestSubFormField() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestGroupField");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(res.indexOf("var grp1 = SubForm.extend({") > 0);
        assertTrue(res.indexOf("this.g1f1 = new NumberRangeField(\"g1f1\",\"Field number 1\",1, 2);") > 0);
        assertTrue(res.indexOf("this.f2 = new grp1('f2','f2');") > 0);

    }

    @Test
    public void testTestNestedSubFormsField() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestNestedGroupField");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(res.indexOf("var grp1 = SubForm.extend({") > 0);
        assertTrue(res.indexOf("this.g1f1 = new NumberRangeField(\"g1f1\",\"Field number 1\",1, 2);") > 0);
        assertTrue(res.indexOf("this.f2 = new grp1('f2','f2');") > 0);
        assertTrue(res.indexOf("var grp2 = SubForm.extend({") > 0);

    }

}
