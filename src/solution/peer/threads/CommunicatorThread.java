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
    private CommPackage commPackage;


    public CommunicatorThread(Node thisNode, CommPackage p) {
        this.thisNode = thisNode;
        this.commPackage = p;
    }

    public void run() {

        switch (commPackage.getType()){
            case START:{
                System.out.println("Created CommThread with START message type");
                break;
            }
            case PING:{
                ping();
                break;
            }
            case BOOTSTRAP_JOIN:{
                joinBootstrap();
                break;
            }
            case BOOTSTRAP_LEAVE:{
                leaveBootstrap();
                break;
            }
            default:{
                System.err.println("no comm package type in commthread constructor");
                break;
            }
        }
    }
/*
    public void connect() {

        try {
            System.out.println("Connecting to " + serverName + " on port " + testTarget);

            mySocket = new MySocket(serverName, testTarget);

            mySocket.write(new CommPackage(thisNode.getNodeGUID(),"Neka poruka", PackageType.MESSAGE));

            CommPackage p = mySocket.read();

            System.out.println("Stigla poruka serventa ["+ p.getSenderNode() +"]: "+ p.getMessage());;

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
*/
    private void ping(){
        System.out.println("Pinging: " + serverName + " on port " + commPackage.getTargetTempPort());
        try {
            mySocket = new MySocket(serverName, commPackage.getTargetTempPort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void joinBootstrap(){
        try {
            System.out.println("Connecting to bootstrap...");

            mySocket = new MySocket(thisNode.getConfigModel().getBootstrapAddress(), thisNode.getConfigModel().getBootstrapPort());

            mySocket.write(commPackage);

            CommPackage p = mySocket.read();

            System.out.println("Bootstrap odgovara: "+ p.getMessage());

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void leaveBootstrap(){
        try {
            System.out.println("Attempting to leave bootstrap...");

            mySocket = new MySocket(thisNode.getConfigModel().getBootstrapAddress(), thisNode.getConfigModel().getBootstrapPort());

            mySocket.write(commPackage);

            CommPackage p = mySocket.read();

            System.out.println("Bootstrap odgovara: "+ p.getMessage());

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
