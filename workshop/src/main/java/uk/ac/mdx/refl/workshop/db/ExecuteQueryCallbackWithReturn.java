package uk.ac.mdx.refl.workshop.db;

import java.sql.ResultSet;

public interface ExecuteQueryCallbackWithReturn<T> {

    T process(ResultSet rs) throws Exception;

}
