package uk.ac.mdx.efrm.demo.resources;

import java.io.StringReader;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import uk.ac.mdx.efrm.eFrmGenerator;
import uk.ac.mdx.efrm.error.eFrmErrorException;

public class FormPreviewer extends Restlet {

    @Override
    public void handle(final Request req, final Response res) {
        try {
            final eFrmGenerator g = new eFrmGenerator();
            final String resHtml = g.generate(new StringReader(req.getEntityAsText())).getGeneratedCode();
            res.setStatus(Status.SUCCESS_OK);
            res.setEntity(resHtml, MediaType.TEXT_HTML);

        } catch (final eFrmErrorException e) {
            res.setEntity(e.getErrorMsgHtml(), MediaType.TEXT_HTML);
            res.setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
        } catch (final Exception e) {
            e.printStackTrace();
            res.setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }

}
