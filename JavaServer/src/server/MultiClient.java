package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class MultiClient extends Thread {

  private final Socket socket;
  private final Server server;

  MultiClient(Socket socket, Server server) {
    this.socket = socket;
    this.server = server;
  }

  @Override
  public void run() {
    clientHandler();
  }

  private void clientHandler() {
    ObjectInputStream objectInputStream = null;
    try {
      objectInputStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true) {
      String message;
      try {
        if (objectInputStream != null) {
          message = (String) objectInputStream.readObject();
          System.out.println(message);
          ReceiveReport(message);
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        break;
      }
    }
    try {
      objectInputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void ReceiveReport(String message) {
    System.out.println(message);
    Report report = ReportParser.parseReport(message);
    System.out.println(report.toString());
    server.database.AddToDatabase(report);
  }


}
