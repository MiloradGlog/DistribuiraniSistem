package solution.peer.threads;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import solution.n_queens.Job;
import solution.n_queens.NQueensSolver;
import solution.peer.NewNodeReorganizationHandler;
import solution.peer.NodeInfo;
import solution.peer.commPackage.CommPackage;
import solution.peer.Node;
import solution.peer.commPackage.PackageType;
import solution.socket.MySocket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class HandlerThread extends Thread {

    private Gson gson;
    private Node thisNode;
    private MySocket socket;

    public HandlerThread(Socket s, Node thisNode) {
        gson = new Gson();
        try {
            this.thisNode = thisNode;
            this.socket = new MySocket(s);
        } catch (IOException ioexception){
            System.err.println("HandlerThread IOException when creating socket\nStackTrace:");
            ioexception.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            CommPackage p = socket.read();
            socket.close();
            determinePackageType(p);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void determinePackageType(CommPackage p){
        switch (p.getType()) {
            case START:{
                handleStartBroadcast(p);
                break;
            }
            case PING:{
                handlePing(p);
                break;
            }
            case PING_RESPONSE:{
                System.out.println("Recieved pong from "+ p.getSenderNode());
                break;
            }
            case BOOTSTRAP_JOIN:{
                handleBootstrapJoin(p);
                break;
            }
            case BOOTSTRAP_LEAVE:{
                handleBootstrapLeave(p);
                break;
            }
            case NEW_NODE_REORGANIZATION_REQUEST:{
                handleNewNodeReorganizationRequest(p);
                break;
            }
            case SET_SUCCESSOR:{
                handleSetSuccessor(p);
                break;
            }
            case BROADCAST:{
                handleBroadcast(p);
                break;
            }
            case BROADCAST_LEAVE:{
                handleLeaveBroadcast(p);
                break;
            }
            case COUNT:{
                handleCountBroadcast(p);
                break;
            }
            default:{
                System.err.println("default in handlerthread");
            }
        }
    }

    private void handleStartBroadcast(CommPackage p){
        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            System.out.println("Svi zapoceli poslove!");
        }
        else {
            ArrayList<Job> jobs = gson.fromJson(p.getMessage(),  new TypeToken<ArrayList<Job>>(){}.getType());
            Job myJob = jobs.get(0);
            jobs.remove(myJob);
            p.setMessage(gson.toJson(jobs));
            System.out.println("Uzeo sam posao "+ myJob);

            try {
                System.out.println("Prosledjujem start broadcast...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            thisNode.beginJob(myJob);
        }
    }

    private void handlePing(CommPackage p){
        try {
            System.out.println("Ping od serventa [" + p.getSenderNode() + "]: "+ p.getMessage());
            socket = new MySocket(p.getSenderNode().getNodeAddress(), p.getSenderNode().getNodePort());
            try {
                sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            socket.write(new CommPackage(thisNode.getNodeInfo(),"primio ping!", PackageType.PING_RESPONSE, p.getSenderNode()));

            socket.close();
        } catch (IOException ioException){
            System.err.println("IOException in HandlerThread run method\nStackTrace:");
            ioException.printStackTrace();
        }

    }

    private void handleBootstrapJoin(CommPackage p){

        NodeInfo recievedNode = gson.fromJson(p.getMessage(), NodeInfo.class);

        if (recievedNode == null){
            System.err.println("Primljen cvoj je null");
        } else {
            System.out.println("Zapocinjem reorganizaciju...");

            CommPackage commPackage = new CommPackage(
                    thisNode.getNodeInfo(),
                    "zahtevam reogranizaciju",
                    PackageType.NEW_NODE_REORGANIZATION_REQUEST,
                    recievedNode);

            new CommunicatorThread(thisNode, commPackage).run();
        }

    }

    private void handleBootstrapLeave(CommPackage p){

        System.out.println("Bootstrap odgovara: "+ p.getMessage());

    }

    private void handleNewNodeReorganizationRequest(CommPackage p){
        new NewNodeReorganizationHandler(socket, thisNode).handle(p);
    }

    private void handleBroadcast(CommPackage p){
        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            System.out.println("Broadcast uspesno obavljen!");
        }
        else {
            System.out.println("Cvor "+ p.getSenderNode().getNodeGUID() +" salje broadcast, poruka: "+ p.getMessage());
            try {
                System.out.println("Prosledjujem broadcast...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void handleLeaveBroadcast(CommPackage p){
        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            //broadcast dosao do onog ko ga salje
            System.out.println("Uspesno obavestio cvorove o izlasku!");
            System.exit(0);
        }
        else if (p.getSenderNode().getNodeGUID() == thisNode.getSuccessorNode().getNodeGUID()){
            //broadcast je na prethodniku onog ko ga salje
            try {
                System.out.println("Prosledjujem leave broadcast clanu koji izlazi...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            NodeInfo newSuccessor = gson.fromJson(p.getMessage(), NodeInfo.class);
            if (newSuccessor == null){
                System.err.println("New succesor je null u leavebroadcast!");
            } else {
                thisNode.setSuccessorNode(newSuccessor);
            }
        }
        else {
            //broadcast se prosledjuje
            System.out.println("Cvor "+ p.getSenderNode().getNodeGUID() +" napusta sistem...");
            try {
                System.out.println("Prosledjujem broadcast...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Postavlja dobijeni cvor kao sledeceg u nizu (svog "naslednika")
     * @param p (U paketu u message se sadrzi json sa NodeInfo objektom naslednika)
     */
    private void handleSetSuccessor(CommPackage p){
        NodeInfo successorNode = gson.fromJson(p.getMessage(), NodeInfo.class);
        System.out.println("Menjam naslednika u node "+ successorNode.getNodeGUID());
        thisNode.setSuccessorNode(successorNode);
    }

    private void handleCountBroadcast(CommPackage p){
        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            System.out.println("Count broadcast uspesno obavljen!");
            int count = Integer.parseInt(p.getMessage());
            System.out.println("Prebrojao sam da ih ima: "+ count);
            thisNode.initiateJobs(count, p.getTargetNode().getNodeGUID());// node guid je n
        }
        else {
            System.out.println("Cvor "+ p.getSenderNode().getNodeGUID() +" salje count broadcast, broj: "+ p.getMessage());
            try {
                int count = Integer.parseInt(p.getMessage());
                p.setMessage(Integer.toString(++count));
                System.out.println("Prosledjujem count broadcast...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
