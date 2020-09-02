import database.SQLiteDb;
import express.Express;
import express.middleware.Middleware;

import java.io.IOException;
import java.nio.file.Paths;

@SuppressWarnings("unchecked") // suppress type casting warnings
public class Main {

  public static void main(String[] args) {
    var db = new SQLiteDb("database.db");
    var app = new Express();

    new Rest(app, db);

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
