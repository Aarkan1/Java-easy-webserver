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

Query to database. The PreparedStatement gets automatically executed when lambda has been called.
The lambda gets the statement as an argument to set parameters to the query.
ResultSet from query gets auto mapped to target class.
```java
// db.get returns list of objects, auto mapped to provided class 
(List<class>) db.get(class, query, statement lambda); // lambda is optional if prepared statement isn't needed

// returns a list of all users
(List<entities.User>) db.get(entities.User.class, "SELECT * FROM users");
 
// returns a list of all users mathing statement
(List<entities.User>) db.get(entities.User.class, "SELECT * FROM users WHERE username = ?", statement -> {
  statement.setString(1, "superman");
});

// db.update is used to query writes to the database, like INSERT, UPDATE and DELETE
// and returns the auto incremented long number when INSERT
long id = db.update(query, statement lambda);

long id = db.update("INSERT INTO users(username, age) VALUES(?,?)", statement -> {
  statement.setString(1, "superman");
  statement.setInt(2, 33);
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
