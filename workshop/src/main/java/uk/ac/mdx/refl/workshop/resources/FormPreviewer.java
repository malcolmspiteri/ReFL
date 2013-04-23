package uk.ac.mdx.refl.workshop.resources;

import java.io.StringReader;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import uk.ac.mdx.refl.ReflGenerator;
import uk.ac.mdx.reflerror.ReflErrorException;

public class FormPreviewer extends Restlet {

    @Override
    public void handle(final Request req, final Response res) {
        try {
            final ReflGenerator g = new ReflGenerator();
            final String resHtml = g.generate(new StringReader(req.getEntityAsText())).getGeneratedCode();
            res.setStatus(Status.SUCCESS_OK);
            res.setEntity(resHtml, MediaType.TEXT_HTML);

        } catch (final ReflErrorException e) {
            res.setEntity(e.getErrorMsgHtml(), MediaType.TEXT_HTML);
            res.setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
        } catch (final Exception e) {
            e.printStackTrace();
            res.setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }

}
