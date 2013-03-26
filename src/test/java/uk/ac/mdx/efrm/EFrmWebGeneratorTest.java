package uk.ac.mdx.efrm;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Test;

public class EFrmWebGeneratorTest {

    private static final String TEST_FORM1 = "FORM testForm \"Test Form\"\n" +
        "FIELDS\n" +
        "fone : STRING[10]\n" +
        "ftwo : OPTION { one \"test1\", two \"test 2\" }\n" +
        "ftre : 1..10\n" +
        "END FORM";

    @Test
    public void test() throws Exception {
    	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("testForm.txt");
    	
        final EFrmWebGenerator generator = new EFrmWebGenerator();
        final String out = generator.generate(new InputStreamReader(is));
        System.out.println(out);
        assertTrue(1 == 1);
    }

}
