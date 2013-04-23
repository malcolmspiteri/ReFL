package uk.ac.mdx.refl.workshop.db;

import java.sql.Connection;

public interface ExecuteWithConnectionCallbackWithReturn<T> {

    T process(Connection conn) throws Exception;
}
