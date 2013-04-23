package uk.ac.mdx.refl.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import uk.ac.mdx.refl.ReflValidatingVisitor;
import uk.ac.mdx.reflerror.ReflErrorHandler;

public class TestErrors extends BaseReflUnitTest {

    @Test
    public void testInvalidId() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidId");

    }

    private void testErrorsAreDetectedIn(final String filename) throws IOException {
        final ParseTree pt = buildParseTreeFor(filename);
        final ReflErrorHandler handler = new ReflErrorHandler();
        final ReflValidatingVisitor validator = new ReflValidatingVisitor(handler, buildSymbolTable(pt));
        validator.visit(pt);
        assertTrue(handler.hasErrors());
    }

    @Test
    public void testInvalidSubformRef() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidSubformRef");

    }

    @Test
    public void testInvalidArrayDimRef() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidArrayDimRef");

    }

    @Test
    public void testInvalidNonArrayDimRef() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidNonArrayDimRef");

    }

    @Test
    public void testInvalidArithmeticOperation() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidArithmeticOperation");

    }

    @Test
    public void testInvalidIsEmptyExpression() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidIsEmptyExpression");

    }

    @Test
    public void testInvalidNotExpression() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidNotExpression");

    }

    @Test
    public void testInvalidAndExpression() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidAndExpression");

    }

    @Test
    public void testInvalidOrExpression() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidOrExpression");

    }

    @Test
    public void testInvalidEquality() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidEquality");

    }

    @Test
    public void testInvalidInequality() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidInequality");

    }

    @Test
    public void testInvalidGreaterThan() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidGreaterThan");

    }

    @Test
    public void testInvalidGreaterOrEqualTo() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidGreaterOrEqualTo");

    }

    @Test
    public void testInvalidLessThan() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidLessThan");

    }

    @Test
    public void testInvalidLessOrEqualTo() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidLessOrEqualTo");

    }

    @Test
    public void testInvalidIfCond() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidIfCond");

    }

    @Test
    public void testInvalidWhileCond() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidWhileCond");

    }

    @Test
    public void testInvalidHeaderStat() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidHeaderStat");

    }

    @Test
    public void testInvalidGridStat() throws Exception {

        testErrorsAreDetectedIn("TestErrorInvalidGridStat");

    }

}
