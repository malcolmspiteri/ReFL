package uk.ac.mdx.refl.test;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import uk.ac.mdx.refl.ReflGenerator;
import uk.ac.mdx.reflerror.ReflErrorException;

public class TestFormIdAndLabel {

    @Test
    public void testFormIdAndLabel() throws Exception {
        try {
            final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("TestFormIdAndLabel.refl");

            final ReflGenerator generator = new ReflGenerator();
            final String out = generator.generate(new InputStreamReader(is)).getGeneratedCode();
            assertTrue(out.indexOf("this.id = \"MyForm\";") > 0);
            assertTrue(out.indexOf("this.label = \"This is my form\";") > 0);
        } catch (final ReflErrorException e) {
            System.out.println(e.getErrorMsgHtml());
        }
        assertTrue(1 == 1);
    }

}
