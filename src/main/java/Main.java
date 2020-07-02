import com.google.gson.Gson;
import database.SQLiteDb;
import database.Utils;
import express.Express;
import express.http.SessionCookie;
import express.middleware.Middleware;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@SuppressWarnings("unchecked") // suppress type casting warnings
public class Main {

  public static void main(String[] args) {
    var db = new SQLiteDb("database.db");
    var app = new Express();
    new Authentication(app, db);

    app.use(Middleware.cookieSession("f3v4", 60 * 60 * 24 * 7));

    // authentication middleware
    app.use((req, res) -> {
      var sessionCookie = (SessionCookie) req.getMiddlewareContent("sessioncookie");
      if(sessionCookie.getData() != null) {
        // user is logged in
//        var user = (User) sessionCookie.getData();
//        System.out.println("Session user: " + user.getUsername());
      }
    });

    app.get("/rest/users", (req, res) -> {
      // db.get return a list of object, and we need to
      // explicitly cast the list to the type we want - see (List<User>)
      // We also need to pass which class we're going to populate
      // the list with - see User.class
      var allUsers = (List<User>) db.get(User.class, "SELECT * FROM users");
      res.send(new Gson().toJson(allUsers));
    });

    app.get("/rest/users/:id", (req, res) -> {
      var id = Long.parseLong(req.getParam("id"));
      var user = (List<User>) db.get(User.class,
              "SELECT * FROM users WHERE id = ?",
              stmt -> stmt.setLong(1, id));
      res.send(new Gson().toJson(user.get(0)));
    });

    // db.update returns auto incremented id
    app.post("/rest/users", (req, res) -> {
      var user = (User) Utils.convertBodyToObject(req.getBody(), User.class);
      var id = db.update("INSERT INTO users VALUES(null, ?, ?, ?)", stmt -> {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getUsername());
        stmt.setString(3, user.getPassword());
      });

      user.setId(id);
      res.send(new Gson().toJson(user));
    });

    try {
      app.use(Middleware.statics(Paths.get("src/main/www").toString()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // handles SPA - if no asset match url send index.html
    app.get("*", (req, res) -> {
      res.send(Paths.get("src/main/www/index.html"));
    });

    app.listen(4000);
    System.out.println("Server started on port 4000");
  }
}
