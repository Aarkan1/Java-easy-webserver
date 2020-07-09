package database;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SqlModel {

  public List<?> find() {
    var query = "SELECT * FROM " + getClass().getSimpleName().toLowerCase() + "s";
    return SQLiteDb.db.get(getClass(), query);
  }

  public List<?> find(Map<String, Object> params) {
    var query = "SELECT * FROM " + getClass().getSimpleName().toLowerCase() + "s WHERE ";
    var paramString = String.join(" = ? AND ", params.keySet());
    paramString += " = ?";
    query += paramString;
    System.out.println(query);

    return SQLiteDb.db.get(getClass(), query, List.of(params.values().toArray()));
  }

  public Object findOne(Map<String, Object> params) {
    return find(params).get(0);
  }

  public void save() {
    var fields = getClass().getDeclaredFields();
    Field idField = null;
    var paramsArray = new String[fields.length];
    Arrays.fill(paramsArray, "?");
    var params = String.join(",", paramsArray);
    var table = getClass().getSimpleName().toLowerCase() + "s";
    boolean createEntity = true;

    for(var field : fields) {
      field.setAccessible(true);
      if(field.getName().equals("id")) {
        idField = field;
      }
    }

    try {
      createEntity = idField.getLong(this) == 0;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    var fieldNames = Arrays.stream(fields)
            .filter(f -> !f.getName().equals("id"))
            .map(Field::getName)
            .collect(Collectors.toList());
    var fieldValues = Arrays.stream(fields)
            .filter(f -> !f.getName().equals("id"))
            .map(f -> {
              try {
                return f.get(this);
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
              return null;
            })
            .collect(Collectors.toList());

    // create
    if(createEntity) {
      params = params.substring(2);
      var query = "INSERT INTO " + table +
              "(" +
              String.join(",", fieldNames)  +
              ") VALUES(" +
              params +
              ")";

      var dbId = SQLiteDb.db.update(query, fieldValues);
      try {
        idField.set(this, dbId);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    // update
    else {
      var query = "UPDATE " + table +
              " SET " +
              String.join(" = ?, ", fieldNames) +
              " = ? WHERE id = ?";

      try {
        fieldValues.add(idField.getLong(this));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      SQLiteDb.db.update(query, fieldValues);
    }
  }

  public void delete() {
    var table = getClass().getSimpleName().toLowerCase() + "s";
    var fields = getClass().getDeclaredFields();
    long id = 0;

    for(var field : fields) {
      field.setAccessible(true);
      if(field.getName().equals("id")) {
        try {
          id = field.getLong(this);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    var query = "DELETE FROM " + table + " WHERE id = ?";
    SQLiteDb.db.update(query, List.of(id));
  }
}
