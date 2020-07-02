package database;

import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class SQLiteDb {
  private Connection conn;

  public SQLiteDb() {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:" + Paths.get("database.db").toString());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public SQLiteDb(String databaseName) {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:" + Paths.get(databaseName).toString());
    } catch (SQLException e) {
      e.printStackTrace();
    }
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

  private List<?> convertToList(ResultSet rs, Class klass) {
    ObjectMapper<?> objectMapper = new ObjectMapper<>(klass);
    return objectMapper.map(rs);
  }
}
