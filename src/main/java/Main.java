import database.SQLiteDb;
import express.Express;
import express.http.SessionCookie;
import express.middleware.Middleware;

import java.io.IOException;
import java.nio.file.Paths;

@SuppressWarnings("unchecked") // suppress type casting warnings
public class Main {

  public static void main(String[] args) {
    var db = new SQLiteDb("database.db");
    var app = new Express();
    new Authentication(app, db);
    new Rest(app, db);

    app.use(Middleware.cookieSession("f3v4", 60 * 60 * 24 * 7));

    // authentication middleware
    app.use((req, res) -> {
      // get session cookie id to store in database for persisting sessions
//      var cookie = req.getCookie("f3v4");
//      System.out.println(cookie.getValue());
      var sessionCookie = (SessionCookie) req.getMiddlewareContent("sessioncookie");
      if(sessionCookie.getData() != null) {
        // user is logged in
//        var user = (entities.User) sessionCookie.getData();
//        System.out.println("Session user: " + user.getUsername());
      }
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
