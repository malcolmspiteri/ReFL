package uk.ac.mdx.refl.workshop.resources;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.restlet.resource.ServerResource;

import uk.ac.mdx.refl.workshop.model.ReflForm;

public abstract class FormsSupport extends ServerResource {

    public FormsSupport() {
        super();
    }

    protected JSONObject formToJsonObj(final ReflForm f) {
        return new JSONObject(formToMap(f));
    }

    protected Map<String, Object> formToMap(final ReflForm f) {
        final Map<String, Object> fm = new HashMap<String, Object>();
        fm.put("id", f.getId());
        fm.put("name", f.getName());
        fm.put("label", f.getLabel());
        fm.put("ver", f.getVersion());
        fm.put("def", f.getDefinition());
        fm.put("created", DateFormat.getDateInstance().format(f.getCreated()));
        return fm;
    }

}