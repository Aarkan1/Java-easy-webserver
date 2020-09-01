import com.google.gson.Gson;
import entities.User;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServer extends WebSocketServer {
  ConcurrentHashMap<String, List<WebSocket>> rooms = new ConcurrentHashMap<>();
  Gson gson = new Gson();

  public SocketServer(String host, int port) {
    super(new InetSocketAddress(host, port));
  }

  public SocketServer(int port) {
    super(new InetSocketAddress(port));
  }

  // should init rooms from database
  private void initRooms() {
    rooms.put("public", new ArrayList<>());
  }

  @Override
  public void onStart() {
    initRooms();
    System.out.println("WebSocket server started successfully");
  }

  @Override
  public void onOpen(WebSocket socket, ClientHandshake handshake) {
    rooms.get("public").add(socket);

    var user = new User("Sven", "svenman", "supersven");
    var message = Map.of("action", "message", "payload", user);

    socket.send(gson.toJson(message)); //This method sends a message to the new client
    broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
    System.out.println("new connection to " + socket.getRemoteSocketAddress());
  }

  @Override
  public void onMessage(WebSocket socket, String message) {
    System.out.println("received message from "	+ socket.getRemoteSocketAddress() + ": " + message);
    broadcast(message, rooms.get("public"));
  }

  @Override
  public void onClose(WebSocket socket, int code, String reason, boolean remote) {
    rooms.get("public").remove(socket);
    System.out.println("closed " + socket.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
  }

  @Override
  public void onError(WebSocket socket, Exception e) {
    System.err.println("an error occurred on connection " + socket.getRemoteSocketAddress()  + ":" + e);
  }

}
