package uk.ac.mdx.efrm.demo.resources;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.restlet.resource.ServerResource;

import uk.ac.mdx.efrm.demo.model.eForm;

public abstract class FormsSupport extends ServerResource {

    public FormsSupport() {
        super();
    }

    protected JSONObject formToJsonObj(final eForm f) {
        return new JSONObject(formToMap(f));
    }

    protected Map<String, Object> formToMap(final eForm f) {
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