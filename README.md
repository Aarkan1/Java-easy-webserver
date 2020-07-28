# Java express-like webserver
## Frontend
Place all frontend files in the **/www** directory, with index.html at the root of that folder.
Express-Java server supports frontend frameworks like React and Vue.

## Backend
See https://github.com/Aarkan1/express-java for details on the WebServer API.

Get all users
```java
app.get("/rest/users", (req, res) -> {
  var users = (List<User>) db.get(User.class, "SELECT * FROM users");

  res.json(users);
});
```

Get one user on id
```java
app.get("/rest/users/:id", (req, res) -> {
  var id = Long.parseLong(req.getParam("id"));
  var user = (List<User>) db.get(User.class, "SELECT * FROM users WHERE id = ?", List.of(id));

  res.json(user.get(0));
});
```

Post new user
```java
app.post("/rest/users", (req, res) -> {
  var user = (User) req.getBody(User.class);
  var id = db.update("INSERT INTO users(name, age) VALUES(?, ?)", List.of(user.getName(), user.getAge()));
  user.setId(id);

  res.json(user);
});
```

## Database
Connecting to SQLite database. Creates new database file if it doesn't exist.
```java
SQLiteDb db = new SQLiteDb("databaseName.db"); // defaults to "database.db"

// we can pass a boolean toggling the use of @Column annotations 
// in our entities to map property fields
SQLiteDb db = new SQLiteDb("databaseName.db", true); // defaults to false
```

SQLiteDb uses an ORM, ObjectMapper, to convert the entity from the database to a class you created.

If we don't toggle the use of @Column to true entities used in the database must have EXACT same field names as column names in table.
A variable not matching a column in the table will be null when the entity is fetched from the database.
```java
class User {
  String username;
  int age;

  String[] hobbies;
}
```

If you use @Column your entities fields will be tagged.
If the variable name doesn't match the column in the table you can pass the columns name to @Column.
If no value is passed the variable name MUST match the column in the table.
Variables not tagged will be ignored when getting entities from the database.
```java
class User {
  @Column("user_name")
  String username;

  @Column
  int age;

  String[] hobbies;
}
```

Query to database. 

The lambda gets the statement as an argument to set parameters to the query.
ResultSet from query gets auto mapped to target class.
```java
// db.get returns list of objects, auto mapped to provided class 
(List<class>) db.get(class, query, statement lambda); // lambda is optional if prepared statement isn't needed

// returns a list of all users
(List<User>) db.get(User.class, "SELECT * FROM users");
 
// returns a list of all users matching statement
(List<User>) db.get(User.class, "SELECT * FROM users WHERE username = ?", List.of("superman"));

// db.update is used to query writes to the database, like INSERT, UPDATE and DELETE
// and returns the auto incremented long number when INSERT
long id = db.update(query, list of values); 
long id = db.update(query, statement lambda);

// There's two ways to set query parameters:

// (Recommended) 
// Provide a list of values that will replace the "?" in the query at the same
// index as the value in the list. (first value will replace the first "?", second will replace the second "?" etc..)
// (this automatically prevents SQL injection)
// NOTE: It's important that the value type matches the type of the column in database table!
// see the first value is a String and second is an Integer
long id = db.update("INSERT INTO users(username, age) VALUES(?, ?)", List.of("superman", 33));

// The other way is to pass a lambda where you manually set each PreparedStatement parameter. 
// The PreparedStatement gets automatically executed when lambda has been called.
long id = db.update("INSERT INTO users(username, age) VALUES(?, ?)", statement -> {
  statement.setString(1, "superman");
  statement.setInt(2, 33);
});


// If we want full control of our database transactions we can pass a lambda to db.query()
// and manually set a PreparedStatement, query parameters and execute statement.
db.query(connection -> {
  String query = "INSERT INTO users(username, age) VALUES(?, ?)";

  try {
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, "superman");
    statement.setInt(2, 33);

    statement.executeUpdate();
  } catch (SQLException e) {
    e.printStackTrace();
  }
});

// If db.query is used the value from database will not be auto converted to a class instance.
// The default value from a SELECT is a ResultSet
db.query(connection -> {
  String query = "SELECT * FROM users WHERE username = ?";

  try {
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, "superman");

    ResultSet resultSet = statement.executeQuery();

    while(resultSet.next()) {
      System.out.println(resultSet.getString("username"));
    }
  } catch (SQLException e) {
    e.printStackTrace();
  }
});
```

## File upload
Over `req.getFormData(NAME)` you receive a list of FileItems from the posted FormData.
`req.getFormData()` without param returns a Map, where key is the field name and value the list of FileItems.
Example JavaScript:
```js
let files = document.querySelector('input[type=file]').files;
let formData = new FormData();

for(let file of files) {
   formData.append('files', file, file.name);
}
   
formData.append('greeting', 'Hello, awesome server!');

fetch('/api/file-upload', {
   method: 'POST',
   body: formData
});
```

Form data gets stored in lists. If a file is appended we get the byte[] array from the fileItem.get(). You can use this byte[] array to save the file to an uploads-folder. 
```java
app.post("/api/file-upload", (req, res) -> {
  List<FileItem> files = req.getFormData("files");
  String greeting = req.getFormData("greeting").get(0).getString();

  String filename = files.get(0).getName();
  byte[] profilePic = files.get(0).get();

  // Process data, save files to disk
  try (var os = new FileOutputStream("path/to/uploads/" + filename)) {
    os.write(profilePic);
  } catch (Exception e) {
    e.printStackTrace();
  }

  // Prints "Greeting: Hello, awesome server!, Filename: profile-picture.png"
  res.send("Greeting: " + greeting + ", Filename: " + filename);
});
```

## Libraries used

Express Java library:
https://github.com/Aarkan1/express-java

SQLite Java: 
https://www.sqlitetutorial.net/sqlite-java/

SQL ORM: 
https://java18.lms.nodehill.se/article/java-orm-object-relational-mapping

JSON parser:
https://jsoniter.com/

BCrypt: 
https://github.com/patrickfav/bcrypt

## Possible enhancements
WebSocket: 
https://github.com/TooTallNate/Java-WebSocket

Store sessions in a database.