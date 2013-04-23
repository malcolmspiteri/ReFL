package uk.ac.mdx.refl.workshop;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import uk.ac.mdx.refl.workshop.resources.FormPreviewer;
import uk.ac.mdx.refl.workshop.resources.FormResource;
import uk.ac.mdx.refl.workshop.resources.FormsResource;

public class ReflApplication extends Application {

    public ReflApplication(final Context context) {
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
