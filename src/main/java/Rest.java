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
      res.json(allUsers);
    });

    app.get("/rest/users/:id", (req, res) -> {
      var id = Long.parseLong(req.getParam("id"));
      var user = (List<User>) db.get(User.class,
              "SELECT * FROM users WHERE id = ?",
              stmt -> stmt.setLong(1, id));
      res.json(user.get(0));
    });
  }

  private void post() {
    // db.update returns auto incremented id
    app.post("/rest/users", (req, res) -> {
      var user = (User) req.getBody(User.class);
      var id = db.update("INSERT INTO users VALUES(null, ?, ?, ?)", stmt -> {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getUsername());
        stmt.setString(3, user.getPassword());
      });

      user.setId(id);
      res.json(user);
    });
  }
}
