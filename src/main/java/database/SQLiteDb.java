package database;

import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class SQLiteDb {
  private Connection conn;
  private boolean useColumnTags = false;
  private String databaseName = "database.db";
  static SQLiteDb db;

  public SQLiteDb() {
    connectToDb();
  }

  public SQLiteDb(boolean useColumnTags) {
    this.useColumnTags = useColumnTags;
    connectToDb();
  }

  public SQLiteDb(String databaseName) {
    this.databaseName = databaseName;
    connectToDb();
  }

  public SQLiteDb(String databaseName, boolean useColumnTags) {
    this.databaseName = databaseName;
    this.useColumnTags = useColumnTags;
    connectToDb();
  }

  private void connectToDb() {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:" + Paths.get(databaseName).toString());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    db = this;
  }

  public Connection getConn() {
    return conn;
  }

  public List<?> get(Class klass, String query, List params) {
    return get(klass, query, stmt -> {
      for(int i = 0; i < params.size(); i++) {
        setParam(stmt, i, params.get(i));
      }
    });
  }

  public long update(String query, List params) {
    return update(query, stmt -> {
      for(int i = 0; i < params.size(); i++) {
        setParam(stmt, i, params.get(i));
      }
    });
  }

  public List<?> get(Class klass, String query, StatementHandler statementHandler) {
    try {
      PreparedStatement stmt = conn.prepareStatement(query);
      statementHandler.handler(stmt);
      return convertToList(stmt.executeQuery(), klass);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<?> get(Class klass, String query) {
    try {
      PreparedStatement stmt = conn.prepareStatement(query);
      return convertToList(stmt.executeQuery(), klass);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public long update(String query, StatementHandler statementHandler) {
    try {
      PreparedStatement stmt = conn.prepareStatement(query);
      statementHandler.handler(stmt);
      stmt.executeUpdate();

      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        return rs.getLong(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public void query(ConnectionHandler connectionHandler) {
    connectionHandler.handler(conn);
  }

  private List<?> convertToList(ResultSet rs, Class klass) {
    ObjectMapper<?> objectMapper = new ObjectMapper<>(klass, useColumnTags);
    return objectMapper.map(rs);
  }

  private void setParam(PreparedStatement stmt, int index, Object param) {
    index++; // set statement parameters start on index 1
    try {
      if(param == null) {
        stmt.setNull(index, 0);
        return;
      }

      Class klass = param.getClass();
      if(klass == String.class) {
        stmt.setString(index, (String) param);
      } else if(klass == Integer.class) {
        stmt.setInt(index, (Integer) param);
      } else if(klass == Double.class) {
        stmt.setDouble(index, (Double) param);
      } else if(klass == Float.class) {
        stmt.setFloat(index, (Float) param);
      } else if(klass == Long.class) {
        stmt.setLong(index, (Long) param);
      } else if(klass == Boolean.class) {
        stmt.setBoolean(index, (Boolean) param);
      } else if(klass == Timestamp.class) {
        stmt.setTimestamp(index, (Timestamp) param);
      } else if(klass == Time.class) {
        stmt.setTime(index, (Time) param);
      } else if(klass == Date.class) {
        stmt.setDate(index, (Date) param);
      } else if(klass == Short.class) {
        stmt.setShort(index, (Short) param);
      } else if(klass == Byte.class) {
        stmt.setByte(index, (Byte) param);
      } else if(klass == Blob.class) {
        stmt.setBlob(index, (Blob) param);
      } else {
        throw new SQLException("Variable type doesn't match any methods");
      }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
