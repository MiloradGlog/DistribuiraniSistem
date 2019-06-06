package solution.peer.threads;

import com.google.gson.Gson;
import solution.peer.NodeInfo;
import solution.peer.commPackage.Broadcast;
import solution.peer.commPackage.CommPackage;
import solution.peer.Node;
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
                sendStartBroadcastWalk();
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
            case WALK:{
                sendWalkBroadcast();
                break;
            }
            case LEAVE_WALK:{
                sendLeaveWalkBroadcast();
                break;
            }
            case COUNT_WALK:{
                sendCountWalkBroadcast();
                break;
            }
            case BROADCAST:{
                sendBroadcast();
                break;
            }
            case UPDATE_FINGER_TABLE_BROADCAST:{
                sendUpdateFingerTableBroadCast();
                break;
            }
            case COLLECT_ALL_NODES_WALK:{
                sendCollectAllNodesWalk();
                break;
            }
            case UPDATE_RESULT_BROADCAST:{
                sendUpdateResultWalkBroadcast();
                break;
            }
            case STEAL_JOB:{
                sendStealJobMessage();
                break;
            }
            case TAKE_JOB:{
                sendTakeJobMessage();
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

    private void sendBroadcast(){
        try {
            System.out.println("Sending broadcast...");

            for (NodeInfo node : thisNode.getVisibleNodes()){
                mySocket = new MySocket(node.getNodeAddress(), node.getNodePort());

                mySocket.write(commPackage);

                mySocket.close();

                System.out.println("Sent broadcast to: "+ node.getNodeGUID());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        thisNode.addToRecievedBroadcasts(gson.fromJson(commPackage.getMessage(), Broadcast.class));
    }

    private void sendCollectAllNodesWalk(){
        try {
            System.out.println("Sending collect nodes walk broadcast...");

            mySocket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUpdateFingerTableBroadCast(){
        try {
            System.out.println("Sending updatefingertable broadcast with resultSet:");
            System.out.println(commPackage.getMessage());
            NodeInfo node = thisNode.getSuccessorNode();
//            for (NodeInfo node : thisNode.getVisibleNodes()){
                mySocket = new MySocket(node.getNodeAddress(), node.getNodePort());

                mySocket.write(commPackage);

                mySocket.close();

                System.out.println("Sent broadcast to: "+ node.getNodeGUID());
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        thisNode.addToRecievedBroadcasts(gson.fromJson(commPackage.getMessage(), Broadcast.class));
    }

    private void sendStartBroadcastWalk(){
        try {
            System.out.println("Sending start broadcast...");

            mySocket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void sendWalkBroadcast(){
        try {
            System.out.println("Sending walk broadcast...");

            mySocket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLeaveWalkBroadcast(){
        try {
            System.out.println("Sending leave walk broadcast...");

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

    private void sendCountWalkBroadcast(){
        try {
            System.out.println("Sending count walk broadcast...");


            mySocket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUpdateResultWalkBroadcast(){
        try {
            System.out.println("Sending updateresult walk broadcast...");


            mySocket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendStealJobMessage(){
        try {
            System.out.println("Sending steal job message...");

            NodeInfo target = commPackage.getTargetNode();

            mySocket = new MySocket(target.getNodeAddress(), target.getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTakeJobMessage(){
        try {
            System.out.println("Sending take job message...");

            NodeInfo target = commPackage.getTargetNode();

            mySocket = new MySocket(target.getNodeAddress(), target.getNodePort());

            mySocket.write(commPackage);

            mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
