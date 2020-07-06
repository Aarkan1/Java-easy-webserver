# Java express-like webserver
## Frontend
Place all frontend files in the **/www** directory, with index.html at the root of that folder.

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
  var user = (List<User>) db.get(User.class,
    "SELECT * FROM users WHERE id = ?",
    statement -> statement.setLong(1, id));
  res.json(user.get(0));
});
```

Post new user
```java
app.post("/rest/users", (req, res) -> {
  var user = (User) req.getBody(User.class);
  var id = db.update("INSERT INTO users(name, age) VALUES(?,?)", statement -> {
    statement.setString(1, user.getName());
    statement.setInt(2, user.getAge());
  });

  user.setId(id);
  res.json(user);
});
```

## Database
Connecting to SQLite database. Creates new database file if it doesn't exist.
```java
SQLiteDb db = new SQLiteDb("databaseName.db"); // defaults to "database.db"
```

Entities used in the database must have EXACT same field names as column names in table.

Query to database. 

The lambda gets the statement as an argument to set parameters to the query.
ResultSet from query gets auto mapped to target class.
```java
// db.get returns list of objects, auto mapped to provided class 
(List<class>) db.get(class, query, statement lambda); // lambda is optional if prepared statement isn't needed

// returns a list of all users
(List<entities.User>) db.get(entities.User.class, "SELECT * FROM users");
 
// returns a list of all users matching statement
(List<entities.User>) db.get(entities.User.class, "SELECT * FROM users WHERE username = ?", List.of("superman"));

// db.update is used to query writes to the database, like INSERT, UPDATE and DELETE
// and returns the auto incremented long number when INSERT
long id = db.update(query, list of values); 
long id = db.update(query, statement lambda);

// There's two ways to set query parameters:

// (Recommended) Provide a list of values that will replace the "?" in the query at the same
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
  } catch (SQLException e) {
    e.printStackTrace();
  }
});
```

## Libraries used

Express library:
https://github.com/Aarkan1/express-java

SQLite Java: 
https://www.sqlitetutorial.net/sqlite-java/

SQL ORM: 
https://java18.lms.nodehill.se/article/java-orm-object-relational-mapping

Gson:
https://github.com/google/gson

BCrypt: 
https://github.com/patrickfav/bcrypt

## Possible enhancements
WebSocket: 
https://github.com/TooTallNate/Java-WebSocket
