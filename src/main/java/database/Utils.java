package database;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

  public static Object convertBodyToObject(InputStream is, Class klass) {
    String body = convertBodyToJson(is);
    return new Gson().fromJson(body, klass);
  }

  public static String convertBodyToJson(InputStream is) {
    try {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = is.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      return result.toString("UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
