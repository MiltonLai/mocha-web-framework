package com.rockbb.mocha.db;

import com.rockbb.jshadow.service.dto.UserDTO;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBHelper {
    private static Logger logger = LoggerFactory.getLogger(DBHelper.class);

    private static final DBHelper instance = new DBHelper();
    private DataSource dataSource;
    private BasicRowProcessor rowProcessor;

    private DBHelper() {
        // Replace the default BasicRowProcessor with GenerousBeanProcessor
        // to provide the mapping from column "user_id" to any of "user_id", "userId", "userid", etc.
        rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(DBConfig.getValue("jdbc.driver"));
        ds.setUrl(DBConfig.getValue("jdbc.url"));
        ds.setUsername(DBConfig.getValue("jdbc.username"));
        ds.setPassword(DBConfig.getValue("jdbc.password"));

        ds.setDefaultAutoCommit(DBConfig.getIntValue("dbcp.auto_commit") != 0);
        ds.setMaxIdle(DBConfig.getIntValue("dbcp.max_idle"));
        ds.setMinIdle(DBConfig.getIntValue("dbcp.min_idle"));
        ds.setMaxOpenPreparedStatements(DBConfig.getIntValue("dbcp.max_open"));
        dataSource = ds;
        logger.debug("DBHelper Initialized.");
    }

    public static DBHelper i() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static <T> List<T> list(Connection connection, String sql, Object[] params, Class<T> type) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).query(sql, new BeanListHandler<T>(type, instance.rowProcessor), params);
        } else {
            return new QueryRunner().query(connection, sql, new BeanListHandler<T>(type, instance.rowProcessor), params);
        }
    }

    public static long count(Connection connection, String sql, Object[] params) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).query(sql, new ScalarHandler<Long>(), params);
        } else {
            return new QueryRunner().query(connection, sql, new ScalarHandler<Long>(), params);
        }
    }

    public static <T> T select(Connection connection, String sql, Object[] params, Class<T> type) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).query(sql, new BeanHandler<T>(type, instance.rowProcessor), params);
        } else {
            return new QueryRunner().query(connection, sql, new BeanHandler<T>(type, instance.rowProcessor), params);
        }
    }

    /** Fetch the value of single field, it can be any of Integer, String, etc */
    public static <T> T field(Connection connection, String sql, Object[] params, Class<T> type) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).query(sql, new ScalarHandler<T>(), params);
        } else {
            return new QueryRunner().query(connection, sql, new ScalarHandler<T>(), params);
        }
    }

    /**
     * INSERT, UPDATE, DELETE
     */
    public static int update(Connection connection, String sql, Object[] params) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).update(sql, params);
        } else {
            return new QueryRunner().update(connection, sql, params);
        }
    }

    /** BATCH: INSERT, UPDATE, DELETE */
    public static int[] update(Connection connection, String sql, Object[][] params) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).batch(sql, params);
        } else {
            return new QueryRunner().batch(connection, sql, params);
        }
    }

    public static List<Map<String, Object>> listMap(Connection connection, String sql, Object[] params) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).query(sql, new MapListHandler(), params);
        } else {
            return new QueryRunner().query(connection, sql, new MapListHandler(), params);
        }
    }

    public static Map<String, Object> selectMap(Connection connection, String sql, Object[] params) throws SQLException {
        if (connection == null) {
            return new QueryRunner(instance.dataSource).query(sql, new MapHandler(), params);
        } else {
            return new QueryRunner().query(connection, sql, new MapHandler(), params);
        }
    }

    public static void start(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    public static void rollback(Connection conn) throws SQLException {
        conn.rollback();
    }

    public static void commit(Connection conn) throws SQLException {
        conn.commit();
    }
}
