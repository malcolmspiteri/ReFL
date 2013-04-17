package uk.ac.mdx.efrm.demo.db;

import java.sql.Connection;

public interface ExecuteWithConnectionCallbackWithReturn<T> {

    T process(Connection conn) throws Exception;
}
