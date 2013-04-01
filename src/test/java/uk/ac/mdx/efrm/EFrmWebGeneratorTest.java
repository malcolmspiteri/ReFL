package uk.ac.mdx.efrm;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import uk.ac.mdx.efrm.error.eFrmErrorException;

public class EFrmWebGeneratorTest {

    @Test
    public void test() throws Exception {
        try {
            final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("testForm5.txt");

            final eFrmGenerator generator = new eFrmGenerator();
            final String out = generator.generate(new InputStreamReader(is));
            System.out.println(out);
        } catch (final eFrmErrorException e) {
            System.out.println(e.getErrorMsgHtml());
        }
        assertTrue(1 == 1);
    }

}
