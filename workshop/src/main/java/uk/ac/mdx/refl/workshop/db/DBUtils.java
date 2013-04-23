package uk.ac.mdx.refl.workshop.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;

import com.googlecode.flyway.core.Flyway;

public abstract class DBUtils {

    private static final String DB_CONN_STRING = "jdbc:hsqldb:mem:refldb";

    private static final Object MUTEX = new Object();

    private static DataSource dataSource;

    public static DataSource getDataSource() {
        synchronized (MUTEX) {
            if (dataSource == null) {
                dataSource = new JDBCDataSource();
                ((JDBCDataSource) dataSource).setDatabase(DB_CONN_STRING);
                ((JDBCDataSource) dataSource).setUser("sa");
                ((JDBCDataSource) dataSource).setPassword("");
            }
            return dataSource;

        }

    }

    public static void createDb() {
        synchronized (MUTEX) {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
                final Connection conn = DriverManager.getConnection(
                    DB_CONN_STRING, "sa", "");
                final String[] initStmts = new String[] { "CREATE SCHEMA REFL AUTHORIZATION DBA" };

                for (final String e : initStmts) {
                    conn.createStatement().execute(e);
                }
                conn.close();

                // Create database
                final Flyway flyway = new Flyway();
                flyway.setDataSource(getDataSource());
                flyway.setSchemas("REFL");
                flyway.init();
                flyway.migrate();

            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void shutdownDb() {

        synchronized (MUTEX) {
            Connection conn;
            try {
                conn = getDataSource().getConnection();
                conn.createStatement().execute("SHUTDOWN");
                conn.close();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void executeWithConnection(
        final ExecuteWithConnectionCallback callback) {
        executeWithConnection(new ExecuteWithConnectionCallbackWithReturn<Void>() {

            public Void process(final Connection conn) throws Exception {
                callback.process(conn);
                return null;
            }
        });
    }

    public static <T> T executeWithConnection(
        final ExecuteWithConnectionCallbackWithReturn<T> callback) {
        Connection conn = null;
        try {
            conn = getDataSource().getConnection();
            return callback.process(conn);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                } catch (final SQLException e) {
                    // Ignore
                }
            }
        }
    }

    public static void executeQuery(final String query,
        final ExecuteQueryCallback callback) {
        executeQuery(query, new ExecuteQueryCallbackWithReturn<Void>() {

            public Void process(final ResultSet rs) throws Exception {
                callback.process(rs);
                return null;
            }
        });
    }

    public static <T> T executeQuery(final String query,
        final ExecuteQueryCallbackWithReturn<T> callback) {
        return executeWithConnection(new ExecuteWithConnectionCallbackWithReturn<T>() {

            public T process(final Connection conn) {

                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = conn.prepareStatement(query);
                    rs = ps.executeQuery();
                    return callback.process(rs);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (rs != null) {
                        try {
                            if (!rs.isClosed()) {
                                rs.close();
                            }
                        } catch (final SQLException e) {
                            // Ignore
                        }
                    }
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

    public static int executeUpdateStatement(final PreparedStatement ps) {
        return executeWithConnection(new ExecuteWithConnectionCallbackWithReturn<Integer>() {

            public Integer process(final Connection conn) {

                try {
                    return ps.executeUpdate();
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

    public static int generateId(final String seqName) {
        final String sql = String
            .format("select next value for REFL.%s from INFORMATION_SCHEMA.SYSTEM_TABLES",
                seqName);
        return executeQuery(sql, new ExecuteQueryCallbackWithReturn<Integer>() {

            public Integer process(final ResultSet rs) throws Exception {
                rs.next();
                return rs.getInt(1);
            }

        });
    }

}
