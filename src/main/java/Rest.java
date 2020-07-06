import database.SQLiteDb;
import entities.User;
import express.Express;

import java.util.List;

@SuppressWarnings("unchecked")
public class Rest {
  private Express app;
  private SQLiteDb db;

  public Rest(Express app, SQLiteDb db) {
    this.app = app;
    this.db = db;

    get();
    post();
  }

  private void get() {
    app.get("/rest/users", (req, res) -> {
      // db.get return a list of object, and we need to
      // explicitly cast the list to the type we want - see (List<entities.User>)
      // We also need to pass which class we're going to
      // populate the list with - see entities.User.class
      var allUsers = (List<User>) db.get(User.class, "SELECT * FROM users");
      allUsers.forEach(u -> u.setPassword(null)); // always sanitize passwords

      res.json(allUsers);
    });

    app.get("/rest/users/:id", (req, res) -> {
      var id = Long.parseLong(req.getParam("id"));
      var user = (List<User>) db.get(User.class,"SELECT * FROM users WHERE id = ?", List.of(id));
      user.get(0).setPassword(null);

      res.json(user.get(0)); // always sanitize passwords
    });
  }

  private void post() {
    // To set query parameters we can either add a list of values
    // (this will automatically set right param and prevent SQL injection)
    // Note: it's important that right variable type is passed in the list
    // to properly set right parameter
    app.post("/rest/users", (req, res) -> {
      // to get an instance of a specific class we must
      // provide which class the body should convert to
      var user = (User) req.getBody(User.class);

      // db.update returns auto incremented id after insertion.
      var id = db.update("INSERT INTO users(name, username, password) VALUES(?, ?, ?)",
              List.of(user.getName(), user.getUsername(), user.getPassword()));
      user.setId(id);

      res.json(user);
    });

    // or use a lambda to manually set the PreparedStatement parameters, like:
    /*
      var id = db.update("INSERT INTO users(name, username, password) VALUES(?, ?, ?)", statement -> {
        statement.setString(1, user.getName());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
      });
     */
  }
}
