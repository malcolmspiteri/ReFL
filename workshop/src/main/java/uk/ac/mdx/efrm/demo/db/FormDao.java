package uk.ac.mdx.efrm.demo.db;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.mdx.efrm.demo.model.eForm;

public class FormDao {

    private static final int COL_ID = 1;
    private static final int COL_NAME = 2;
    private static final int COL_LABEL = 3;
    private static final int COL_VERSION = 4;
    private static final int COL_DEF = 5;
    private static final int COL_COMPILED = 6;
    private static final int COL_CREATED = 7;

    private static final int BUFFER_SIZE = 1024;

    private static final Logger LOG = LoggerFactory.getLogger(FormDao.class);

    public eForm create(final eForm form) {
        form.setId(DBUtils.generateId("SQ_FORM"));

        DBUtils.executeWithConnection(new ExecuteWithConnectionCallback() {

            public void process(final Connection conn) throws Exception {
                final String qry = "INSERT INTO EFRM.TB_FORM VALUES (?, ?, ?, ?, ?, ?, ?)";
                final PreparedStatement ps = conn.prepareStatement(qry);
                ps.setInt(COL_ID, form.getId());
                ps.setString(COL_NAME, form.getName());
                ps.setString(COL_LABEL, form.getLabel());
                ps.setInt(COL_VERSION, form.getVersion());
                ps.setClob(COL_DEF, new StringReader(form.getDefinition()));
                if ((form.getCompiled() != null) && (form.getCompiled().length() > 0)) {
                    ps.setClob(COL_COMPILED, new StringReader(form.getCompiled()));
                } else {
                    ps.setNull(COL_COMPILED, Types.CLOB);
                }
                ps.setDate(COL_CREATED, new java.sql.Date(form.getCreated().getTime()));
                try {
                    ps.executeUpdate();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (ps != null) {
                        try {
                            if (!ps.isClosed()) {
                                ps.close();
                            }
                        } catch (final SQLException e) {
                            // Ignore
                        }
                    }
                }
            }
        });
        return form;

    }

    public eForm getById(final int id) {
        final String qry = "SELECT ID, NAME, LABEL, VERSION, DEFINITION, COMPILED, CREATED FROM EFRM.TB_FORM f " +
            "WHERE ID = " + id;
        final eForm ret = DBUtils.executeQuery(qry, new ExecuteQueryCallbackWithReturn<eForm>() {

            public eForm process(final ResultSet rs) throws Exception {
                if (rs.next()) {
                    return mapRow(rs);
                } else {
                    throw new IllegalStateException("eForm " + id + " not found");
                }
            }

        });
        return ret;
    }

    public List<eForm> getAllForms() {
        final String qry = "SELECT ID, NAME, LABEL, VERSION, DEFINITION, COMPILED, CREATED FROM EFRM.TB_FORM f " +
            "WHERE VERSION = (SELECT MAX(VERSION) FROM EFRM.TB_FORM f2 WHERE f2.NAME = f.NAME)";
        final List<eForm> ret = new ArrayList<eForm>();
        DBUtils.executeQuery(qry, new ExecuteQueryCallback() {

            public void process(final ResultSet rs) throws Exception {
                while (rs.next()) {
                    ret.add(mapRow(rs));
                }
            }

        });
        return ret;
    }

    private eForm mapRow(final ResultSet rs) throws Exception {
        final eForm f = new eForm();
        f.setId(rs.getInt(COL_ID));
        f.setName(rs.getString(COL_NAME));
        f.setLabel(rs.getString(COL_LABEL));
        f.setVersion(rs.getInt(COL_VERSION));

        Reader r = rs.getClob(COL_DEF).getCharacterStream();
        StringBuilder sb = new StringBuilder();
        final char[] buffer = new char[BUFFER_SIZE];
        int i = 0;
        // Definition
        while ((i = r.read(buffer)) != -1) {
            sb.append(buffer, 0, i);
        }
        f.setDefinition(sb.toString());

        // Compiled
        Clob clob = rs.getClob(COL_COMPILED);
        if (clob != null) {
	        r = clob.getCharacterStream();
	        sb = new StringBuilder();
	        while ((i = r.read(buffer)) != -1) {
	            sb.append(buffer, 0, i);
	        }
	        f.setCompiled(sb.toString());
        }
        
        f.setCreated(rs.getDate(COL_CREATED));

        return f;
    }

}
