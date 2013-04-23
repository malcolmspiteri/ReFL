package uk.ac.mdx.refl.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestRules extends BaseReflUnitTest {

    @Test
    public void testVariables() throws Exception {

        final String res = getOutputFor("TestVariable");
        assertTrue(res.indexOf("var myNumVar = new Variable()") > 0);
        assertTrue(res.indexOf("var myStrVar = new Variable()") > 0);

    }

    @Test
    public void testVariablesAssign() throws Exception {

        final String res = getOutputFor("TestVariableAssign");
        assertTrue(res.indexOf("myNumVar.val(new Variable(10).val());") > 0);
        // System.out.println(res);

    }

    @Test
    public void testFieldAssign() throws Exception {

        final String res = getOutputFor("TestFieldAssign");
        assertTrue(res.indexOf("this.f1.val(new Variable(\"test\").val());") > 0);
        // System.out.println(res);

    }

    @Test
    public void testFieldVarAssign() throws Exception {

        final String res = getOutputFor("TestFieldVarAssign");
        assertTrue(res.indexOf("myVar.val(new Variable(\"Test\").val());") > 0);
        assertTrue(res.indexOf("this.f1.val(myVar.val());") > 0);

        // System.out.println(res);

    }

    @Test
    public void testArithmeticOperAssign() throws Exception {

        final String res = getOutputFor("TestArithmeticOperAssign");
        assertTrue(res
            .indexOf("this.f1.val((((new Variable(10).val()+new Variable(10).val())-new Variable(10).val())/new Variable(2).val())*new Variable(2).val());") > 0);
        // System.out.println(res);

    }

    @Test
    public void testIfCond() throws Exception {

        final String res = getOutputFor("TestIfCond");
        assertTrue(res.indexOf("areEqual(this.f1.val(),new Variable([\"yes\"]).val()") > 0);
        // System.out.println(res);

    }

    @Test
    public void testWhileCond() throws Exception {
        final String res = getOutputFor("TestWhile");
        assertTrue(res.indexOf("while ((lessThan(c.val(),new Variable(50).val())))") > 0);
        // System.out.println(res);

    }

    @Test
    public void testEquality() throws Exception {
        final String res = getOutputFor("TestEquality");
        assertTrue(res.indexOf("areEqual(this.f1.val(),new Variable([\"yes\"]).val())") > 0);
        // System.out.println(res);

    }

    @Test
    public void testInequality() throws Exception {
        final String res = getOutputFor("TestInequality");
        assertTrue(res.indexOf("!(areEqual(this.f1.val(),new Variable([\"yes\"]).val()))") > 0);
        // System.out.println(res);

    }

    @Test
    public void testGreaterThan() throws Exception {
        final String res = getOutputFor("TestGreaterThan");
        assertTrue(res.indexOf("((greaterThan(this.f1.val(),new Variable(10).val())))") > 0);
        // System.out.println(res);

    }

    @Test
    public void testGreaterOrEqualTo() throws Exception {
        final String res = getOutputFor("TestGreaterOrEqualTo");
        assertTrue(res
            .indexOf("(((greaterThan(this.f1.val(),new Variable(10).val()) || areEqual(this.f1.val(),new Variable(10).val())).val()))") > 0);
        // System.out.println(res);

    }

    @Test
    public void testLessThan() throws Exception {
        final String res = getOutputFor("TestLessThan");
        assertTrue(res.indexOf("((lessThan(this.f1.val(),new Variable(10).val())))") > 0);
        // System.out.println(res);
    }

    @Test
    public void testLessOrEqualTo() throws Exception {
        final String res = getOutputFor("TestLessOrEqualTo");
        assertTrue(res
            .indexOf("(((lessThan(this.f1.val(),new Variable(10).val()) || areEqual(this.f1.val(),new Variable(10).val())).val()))") > 0);
        // System.out.println(res);

    }

    @Test
    public void testNegation() throws Exception {

        final String res = getOutputFor("TestNegation");
        assertTrue(res.indexOf("((!(areEqual(this.f1.val(),new Variable([\"yes\"]).val())).val()))") > 0);
        // System.out.println(res);
    }

    @Test
    public void testAsk() throws Exception {

        final String res = getOutputFor("TestIfCond");
        assertTrue(res.indexOf("this.f1.enable();") > 0);

    }

    @Test
    public void testNoAsk() throws Exception {

        final String res = getOutputFor("TestIfCond");
        assertTrue(res.indexOf("this.f2.disable();") > 0);

    }

}
