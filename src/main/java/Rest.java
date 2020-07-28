import database.SQLiteDb;
import entities.User;
import express.Express;
import express.utils.Status;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class Rest {
  private Express app;
  private SQLiteDb db;
  private String uploadsDir = "src/main/www/uploads/";

  public Rest(Express app, SQLiteDb db) {
    this.app = app;
    this.db = db;

    var uploads = new File(uploadsDir);
    if(!uploads.exists()) uploads.mkdirs();

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

    // to upload files the frontend need to append them
    // in FormData, and the server can retrieve this data
    // with either:
    // req.getFormData(), which returns Map<String, List<FileItem>>
    // req.getFormData(String name), which returns List<FileItem>

    //   Example:
    //   var fields = req.getFormData();
    //   String firstName = fields.get("firstname").get(0).getString();
    //   byte[] profilePic = fields.get("picture").get(0).get();
    app.post("/api/upload-files", (req, res) -> {
      var uploadDirs = new ArrayList<String>();
      List<FileItem> files = null;

      try {
        files = req.getFormData("files");
        System.out.println(req.getFormData("name").get(0).getString());
      } catch (IOException | FileUploadException e) {
        e.printStackTrace();
      }

      if(files == null) {
        res.sendStatus(Status._401);
        return;
      }

      for(var file : files) {
        var fileName = UUID.randomUUID().toString().
                concat(file.getName().replaceAll("^.*(\\.[\\w]{3,4})$", "$1"));

        uploadDirs.add("/uploads/" + fileName);

        try (var os = new FileOutputStream(uploadsDir + fileName)) {
          os.write(file.get());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      res.json(uploadDirs);
    });

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
