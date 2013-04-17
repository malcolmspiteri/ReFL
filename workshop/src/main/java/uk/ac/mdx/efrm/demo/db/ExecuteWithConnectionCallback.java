package uk.ac.mdx.efrm.demo.db;

import java.sql.Connection;

public interface ExecuteWithConnectionCallback {

    void process(Connection conn) throws Exception;
}
