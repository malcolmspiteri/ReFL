package uk.ac.mdx.refl.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import uk.ac.mdx.refl.ReflGeneratingVisitor;

public class TestLayout extends BaseReflUnitTest {

    @Test
    public void testGrid() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestGridRender");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        final int i1 = res.indexOf("els.push(jQuery(\"<div class='span4'></div>\")");
        final int i2 = res.indexOf("els.push(jQuery(\"<div class='span4'></div>\")", i1 + 1);
        final int i3 = res.indexOf("els.push(jQuery(\"<div class='span4'></div>\")", i2 + 1);
        assertNotEquals(i1, i2);
        assertNotEquals(i1, i3);
        assertNotEquals(i3, i2);
        assertNotEquals(i1, -1);
        assertNotEquals(i2, -1);
        assertNotEquals(i3, -1);

    }

    @Test
    public void testTable() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestTableRender");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(stripWS(res)
            .indexOf(
                "els.push(jQuery(\"<tableclass='tabletable-striped'><thead><tr><td><strong>F1</strong></td><td><strong>F2</strong></td><td><strong>F3</strong></td></tr></thead><tbody></tbody></table>\").appendTo(els[els.length-1]).children(\"tbody\"));") > 0);

    }

    @Test
    public void testInlineSubform() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestInlineSubform");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        System.out.println(res);
        // assertTrue(stripWS(res).indexOf("els.push(jQuery(\"<tableclass='tabletable-striped'><thead><tr><td><strong>F1</strong></td><td><strong>F2</strong></td><td><strong>F3</strong></td></tr></thead><tbody></tbody></table>\").appendTo(els[els.length-1]).children(\"tbody\"));")
        // > 0);

    }

    @Test
    public void testHeader() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestHeaderRender");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(res.indexOf("jQuery(\"<h1>This is a test</h1>\")") > 0);
        assertTrue(res.indexOf("jQuery(\"<h2>This is a test</h2>\")") > 0);
        assertTrue(res.indexOf("jQuery(\"<h3>This is a test</h3>\")") > 0);
        assertTrue(res.indexOf("jQuery(\"<h4>This is a test</h4>\")") > 0);
        assertTrue(res.indexOf("jQuery(\"<h5>This is a test</h5>\")") > 0);
        assertTrue(res.indexOf("jQuery(\"<h6>This is a test</h6>\")") > 0);
        // System.out.println(res);

    }

    @Test
    public void testInfo() throws Exception {

        final ParseTree pt = buildParseTreeFor("TestInfoRender");
        final ReflGeneratingVisitor generator = new ReflGeneratingVisitor(buildSymbolTable(pt));
        final String res = generator.visit(pt);
        assertTrue(res.indexOf("jQuery(\"<div class='alert alert-info'>This is a test</div>\").appendTo(els.pop());") > 0);
        // System.out.println(res);

    }

}
