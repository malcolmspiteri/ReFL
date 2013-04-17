package uk.ac.mdx.efrm.demo.db;

import java.sql.ResultSet;

public interface ExecuteQueryCallback {

    void process(ResultSet rs) throws Exception;

}
