package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class Client {

    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getByName("104.248.165.64");
            Socket socket = new Socket(ip, 5536);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
