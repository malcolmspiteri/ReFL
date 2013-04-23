package uk.ac.mdx.refl.workshop;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.mdx.refl.workshop.db.DBUtils;

public class DBInitContextListener
    implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(DBInitContextListener.class);

    public void contextDestroyed(final ServletContextEvent sce) {
        DBUtils.shutdownDb();
        LOG.info("DB shutdown");
    }

    public void contextInitialized(final ServletContextEvent sce) {
        try {
            DBUtils.shutdownDb();
        } catch (final Exception e) {
        }

        DBUtils.createDb();
        LOG.info("DB initialised");
    }

}
