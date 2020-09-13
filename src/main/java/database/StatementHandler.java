package database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementHandler {
  void handler(PreparedStatement statement) throws SQLException;
}
