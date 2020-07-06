import at.favre.lib.crypto.bcrypt.BCrypt;
import database.SQLiteDb;
import dtos.LoginDTO;
import entities.User;
import express.Express;
import express.http.SessionCookie;
import express.utils.Status;

import java.util.List;

@SuppressWarnings("unchecked")
public class Authentication {
  private Express app;
  private SQLiteDb db;

  public Authentication(Express app, SQLiteDb db) {
    this.app = app;
    this.db = db;

    get();
    post();
  }

  private void get() {
    app.get("/api/login", (req, res) -> {
      var sessionCookie = (SessionCookie) req.getMiddlewareContent("sessioncookie");
      if(sessionCookie.getData() == null) {
        res.send("Not logged in");
        return;
      }

      var user = (User) sessionCookie.getData();
      user.setPassword(null); // sanitize password
      res.json(user);
    });

    app.get("/api/logout", (req, res) -> {
      var sessionCookie = (SessionCookie) req.getMiddlewareContent("sessioncookie");
      sessionCookie.setData(null);
      res.send("Successfully logged out");
    });
  }

  private void post() {
    app.post("/api/login", (req, res) -> {
      var sessionCookie = (SessionCookie) req.getMiddlewareContent("sessioncookie");
      if(sessionCookie.getData() != null) {
        res.send("Already logged in");
        return;
      }

      var loginDto = (LoginDTO) req.getBody(LoginDTO.class);
      var userFromDb = (List<User>) db.get(User.class, "SELECT * FROM users WHERE username = ?", List.of(loginDto.username));

      if(userFromDb.size() < 1) {
        res.setStatus(Status._401);
        res.send("Bad credentials!");
        return;
      }
      var user = userFromDb.get(0);

      var result = BCrypt.verifyer().verify(loginDto.password.toCharArray(), user.getPassword().toCharArray());
      if(!result.verified) {
        res.setStatus(Status._401);
        res.send("Bad credentials!");
        return;
      }

      sessionCookie.setData(user);
      user.setPassword(null); // sanitize password

      res.json(user);
    });

    app.post("/api/register", (req, res) -> {
      var sessionCookie = (SessionCookie) req.getMiddlewareContent("sessioncookie");
      if(sessionCookie.getData() != null) {
        res.send("Already logged in");
        return;
      }
      var user = (User) req.getBody(User.class);
      var userInDB = (List<User>) db.get(User.class, "SELECT * FROM users WHERE username = ?", List.of(user.getUsername()));

      if(userInDB.size() > 0) {
        res.setStatus(Status._400);
        res.send("entities.User already exists!");
        return;
      }

      String hashedPassword = BCrypt.withDefaults().hashToString(10, user.getPassword().toCharArray());
      user.setPassword(hashedPassword);

      var id = db.update("INSERT INTO users(name, username, password) VALUES(?, ?, ?)",
              List.of(user.getName(), user.getUsername(), user.getPassword()));

      user.setId(id); // update with incremented id
      sessionCookie.setData(user); // log in user with session
      user.setPassword(null); // sanitize password

      res.json(user);
    });
  }

}
