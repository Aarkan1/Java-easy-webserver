package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionHandler {
  void handler(Connection connection);
}
