package uk.ac.mdx.refl.workshop.db;

import java.sql.Connection;

public interface ExecuteWithConnectionCallback {

    void process(Connection conn) throws Exception;
}
