package solution.peer.threads;

import solution.peer.commPackage.CommPackage;
import solution.peer.Node;
import solution.peer.commPackage.PackageType;
import solution.socket.MySocket;

import java.io.*;

public class CommunicatorThread extends Thread{

    private String serverName = "localhost";
    private Node thisNode;
    private MySocket mySocket;

    public CommunicatorThread(Node thisNode) {
        this.thisNode = thisNode;
    }

    public void run() {
        connect();
    }

    public void connect() {

        try {
            System.out.println("Connecting to " + serverName + " on port " + thisNode.getNodePort());

            mySocket = new MySocket(serverName, thisNode.getNodePort());

            mySocket.write(new CommPackage(thisNode.getNodeGUID(),"Neka poruka", PackageType.MESSAGE));

            CommPackage p = mySocket.read();

            System.out.println("Stigao comm paket sa servera ciji je id ["+ p.getSenderGUID() +"]: "+ p.getMessage());;

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
