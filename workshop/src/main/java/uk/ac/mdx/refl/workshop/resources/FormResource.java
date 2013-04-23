package uk.ac.mdx.refl.workshop.resources;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import uk.ac.mdx.refl.ReflGenerationResult;
import uk.ac.mdx.refl.ReflGenerator;
import uk.ac.mdx.refl.workshop.db.FormDao;
import uk.ac.mdx.refl.workshop.model.ReflForm;
import uk.ac.mdx.reflerror.ReflErrorException;

public class FormResource extends FormsSupport {

    private static final FormDao FORMS_DB = new FormDao();

    @Override
    @Delete
    public Representation delete() {
        final int id = Integer.parseInt((String) getRequest().getAttributes().get("id"));
        final ReflForm f = FORMS_DB.getById(id);
        FORMS_DB.delete(id);
        return new JsonRepresentation(formToJsonObj(f));
    }

    @Get
    public Representation represent() {
        final int id = Integer.parseInt((String) getRequest().getAttributes().get("id"));
        final ReflForm f = FORMS_DB.getById(id);
        return new JsonRepresentation(formToJsonObj(f));
    }

    @Put
    public Representation updateForm(final Representation entity) {
        try {

            final Form form = new Form(entity);

            final ReflForm frm = new ReflForm();
            int version = Integer.parseInt(form.getFirstValue("formVersion"));
            frm.setDefinition(form.getFirstValue("formDef"));
            frm.setVersion(++version);
            frm.setCreated(new Date(System.currentTimeMillis()));

            final ReflGenerator g = new ReflGenerator();
            final ReflGenerationResult gr = g.generate(new StringReader(form.getFirstValue("formDef")));
            frm.setCompiled(gr.getGeneratedCode());
            frm.setName(gr.getName());
            frm.setLabel(gr.getLabel());

            final ReflForm ret = FORMS_DB.create(frm);

            setStatus(Status.SUCCESS_OK);
            return new JsonRepresentation(formToJsonObj(ret));

        } catch (final ReflErrorException e) {
            getResponse().setEntity(e.getErrorMsgHtml(), MediaType.TEXT_HTML);
            getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
        } catch (final Exception e) {
            e.printStackTrace();
            final StringWriter s = new StringWriter();
            final PrintWriter w = new PrintWriter(s);
            e.printStackTrace(w);
            getResponse().setEntity(s.getBuffer().toString(), MediaType.TEXT_PLAIN);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return null;
    }

}
