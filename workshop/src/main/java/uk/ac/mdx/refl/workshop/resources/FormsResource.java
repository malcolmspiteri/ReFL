package uk.ac.mdx.refl.workshop.resources;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.mdx.refl.ReflGenerationResult;
import uk.ac.mdx.refl.ReflGenerator;
import uk.ac.mdx.refl.workshop.db.FormDao;
import uk.ac.mdx.refl.workshop.model.ReflForm;
import uk.ac.mdx.reflerror.ReflErrorException;

public class FormsResource extends FormsSupport {

    private static final FormDao FORMS_DB = new FormDao();

    @Get
    public Representation represent() {
        final JSONArray retArr = new JSONArray();
        final List<ReflForm> forms = FORMS_DB.getAllForms();
        for (final ReflForm f : forms) {
            retArr.put(formToJsonObj(f));
        }
        return new JsonRepresentation(retArr);
    }

    @Post()
    public Representation createNewForm(final Representation entity) {
        try {
            final Form form = new Form(entity);

            final ReflForm frm = new ReflForm();
            frm.setDefinition(form.getFirstValue("formDef"));

            final ReflGenerator g = new ReflGenerator();
            final ReflGenerationResult gr = g.generate(new StringReader(form.getFirstValue("formDef")));
            frm.setCompiled(gr.getGeneratedCode());
            frm.setName(gr.getName());
            frm.setLabel(gr.getLabel());

            frm.setVersion(1);
            frm.setCreated(new Date(System.currentTimeMillis()));

            final ReflForm ret = FORMS_DB.create(frm);

            setStatus(Status.SUCCESS_CREATED);
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
