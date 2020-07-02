# Java express-like webserver
## Frontend
Place all frontend files in the **/www** directory, with index.html at the root of that folder.

## Backend
See https://github.com/Simonwep/java-express for details on the WebServer API.

## Database
Connecting to SQLite database. Creates new database file if it doesn't exist.
```java
SQLiteDb db = new SQLiteDb("databaseName.db"); // defaults to "database.SQLiteDb"
```

Entities used in the database must have EXACT same field names as column names in table.

Query to database. The PreparedStatement gets automatically executed when lambda has been called.
The lambda gets the statement as an argument to set parameters to the query.
ResultSet from query gets auto mapped to target class.
```java
// db.get returns list of objects, auto mapped to provided class 
(List<class>) db.get(class, query, statement lambda); // lambda is optional if prepared statement isn't needed

// returns a list of all users
(List<User>) db.get(User.class, "SELECT * FROM users");
 
// returns a list of all users mathing statement
(List<User>) db.get(User.class, "SELECT * FROM users WHERE username = ?", statement -> {
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
https://github.com/Simonwep/java-express

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
