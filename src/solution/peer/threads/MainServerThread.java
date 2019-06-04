package solution.peer.threads;

import com.google.gson.Gson;
import solution.bootstrap.Bootstrap;
import solution.bootstrap.BootstrapHandlerThread;
import solution.peer.Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainServerThread extends Thread{

    private Node thisNode;
    private ServerSocket serverSocket;
    private Gson gson;
    private boolean bootstrap;

    private int[] table;

    public MainServerThread(Node node, boolean isBootstrap) throws IOException {
        this.bootstrap = isBootstrap;
        if (!bootstrap) {
            //Ako je Node
            this.thisNode = node;
            serverSocket = new ServerSocket(thisNode.getNodeInfo().getNodePort());
            serverSocket.setSoTimeout(1000000);
            gson = new Gson();
        } else {
            //Ako je Bootstrap
            serverSocket = new ServerSocket(Bootstrap.getInstance().getBootstrapPort());
            serverSocket.setSoTimeout(10000000);
            gson = new Gson();
        }
    }

    public void run() {
        while(true) {
            if (!bootstrap) {
                //Ako je Node
                try {
                    Socket server = serverSocket.accept();
                    new HandlerThread(server, thisNode).start();

                } catch (SocketTimeoutException s) {
                    System.out.println("Socket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            } else {
                //Ako je Bootstrap
                try {
                    Socket server = serverSocket.accept();
                    new BootstrapHandlerThread(server).start();

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
}