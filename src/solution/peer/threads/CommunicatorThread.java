package solution.peer.threads;

import solution.peer.NodeInfo;
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
            case NEW_NODE_REORGANIZATION_REQUEST:{
                sendNewNodeReorganizationRequest();
                break;
            }
            case BROADCAST:{
                sendBroadcast();
                break;
            }
            case BROADCAST_LEAVE:{
                sendLeaveBroadcast();
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
        System.out.println("Pinging: " + serverName + " on port " + commPackage.getTargetNode());
        try {
            mySocket = new MySocket(serverName, commPackage.getTargetNode().getNodePort());

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

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcast(){
        try {
            System.out.println("Sending broadcast...");

            mySocket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLeaveBroadcast(){
        try {
            System.out.println("Sending leave broadcast...");

            mySocket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNewNodeReorganizationRequest(){
        try {
            MySocket socket = new MySocket(commPackage.getTargetNode().getNodeAddress(), commPackage.getTargetNode().getNodePort());
            socket.write(commPackage);

            socket.close();
        } catch (IOException e){
            System.err.println("IOException u handlebootstrapjoin\nStackTrace:");
            e.printStackTrace();
        }
    }

}
