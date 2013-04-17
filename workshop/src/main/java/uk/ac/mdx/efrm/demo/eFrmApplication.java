package uk.ac.mdx.efrm.demo;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import uk.ac.mdx.efrm.demo.resources.FormResource;
import uk.ac.mdx.efrm.demo.resources.FormPreviewer;
import uk.ac.mdx.efrm.demo.resources.FormsResource;

public class eFrmApplication extends Application {

    public eFrmApplication(final Context context) {
        super(context);

    }

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance of HelloWorldResource.
        final Router router = new Router(getContext());

        // Defines only one route
        router.attach("/forms", FormsResource.class);
        router.attach("/forms/previewer", new FormPreviewer());
        router.attach("/forms/{id}", FormResource.class);

        return router;
    }

}
