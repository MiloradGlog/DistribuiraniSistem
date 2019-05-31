package solution.peer.threads;

import com.google.gson.Gson;
import solution.peer.Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainServerThread extends Thread{

    private Node thisNode;
    private ServerSocket serverSocket;
    private Gson gson;

    private int[] table;

    public MainServerThread(Node node) throws IOException {
        this.thisNode = node;
        serverSocket = new ServerSocket(thisNode.getNodePort());
        serverSocket.setSoTimeout(100000);
        gson = new Gson();
    }

    public void run() {
        while(true) {
            try {
                Socket server = serverSocket.accept();
                new HandlerThread(server, thisNode).run();

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}