package uk.ac.mdx.refl.workshop.db;

import java.sql.ResultSet;

public interface ExecuteQueryCallback {

    void process(ResultSet rs) throws Exception;

}
