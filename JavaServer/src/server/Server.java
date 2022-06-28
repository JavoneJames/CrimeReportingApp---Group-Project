package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.ServerNotActiveException;

public class Server {

    Database database;

    private Server() {
        database = new Database();
        startServer();
    }

    private void startServer(){
        //initialise ServerSocket and Socket objects are null
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            //create a new server socket object and pass through the port to listen on
            serverSocket = new ServerSocket(getPORT());
            if (serverSocket.isClosed())//by checking if the server socket is closed also checks that that port is available
                throw new ServerNotActiveException(getPORT() + " cannot be accessed!");
            System.out.println("Starting Server...");
            System.out.println("Waiting for Client Connection...");
            //infinite loops to accept multiple clients to the server
            while (true){
                socket = serverSocket.accept();//accepts socket connection from the client to the server
                System.out.println("Connection Established");
                if (socket.isClosed())//checks if connection is established between the client and server
                    throw new SocketException("Cannot Connect to Client");
                try {
                    MultiClient multiClient = new MultiClient(socket, this);
                    multiClient.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//end of while loop
        } catch (ServerNotActiveException | IOException e) {
            e.getMessage();//prints thrown exception messages
            try {//fail safe exception for any unexpected exceptions - closes ServerSocket and Socket
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (socket != null){
                    socket.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }//end of try catch
    }//end of listen method

    //port the server needs to listen on
    private int getPORT() {
        return 5536;
    }

    public static void main(String[] args) {

            Server server = new Server();
            server.startServer();

    }



}
