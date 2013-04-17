package uk.ac.mdx.efrm.demo.db;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class FormDataDao {

    private static final int COL_FORM_KEY_VALUE = 3;
	private static final int COL_FORM_KEY_KEY = 2;
	private static final int COL_ID = 1;
    private static final int COL_FORM_ID = 2;
    private static final int COL_FORM_DATA = 3;
    private static final int COL_CREATED = 4;

	public long saveFormData(final int formId, final Reader formData, final Map<String, String> key) {
    	final int id = DBUtils.generateId("SQ_FORM_DATA");
    	
        DBUtils.executeWithConnection(new ExecuteWithConnectionCallback() {

			public void process(Connection conn) throws Exception {
		        final String qry = "INSERT INTO EFRM.TB_FORM_DATA VALUES (?, ?, ?, ?)";
		        PreparedStatement ps = conn.prepareStatement(qry);
		        ps.setInt(COL_ID, id);
		        ps.setInt(COL_FORM_ID, formId);
		        ps.setClob(COL_FORM_DATA, formData);
		        ps.setDate(COL_CREATED, new java.sql.Date(System.currentTimeMillis()));
		        
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
        
        for (final String k : key.keySet()) {
        	final String val = key.get(k);
	        DBUtils.executeWithConnection(new ExecuteWithConnectionCallback() {
	
				public void process(Connection conn) throws Exception {
			        final String qry = "INSERT INTO EFRM.TB_FORM_KEYS VALUES (?, ?, ?)";
			        PreparedStatement ps = conn.prepareStatement(qry);
			        ps.setInt(COL_ID, id);
			        ps.setString(COL_FORM_KEY_KEY, k);
			        ps.setString(COL_FORM_KEY_VALUE, val);
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
        }
        
        return id;
		
	}
	
}
