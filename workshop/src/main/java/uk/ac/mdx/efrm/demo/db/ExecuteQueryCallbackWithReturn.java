package uk.ac.mdx.efrm.demo.db;

import java.sql.ResultSet;

public interface ExecuteQueryCallbackWithReturn<T> {

    T process(ResultSet rs) throws Exception;

}
