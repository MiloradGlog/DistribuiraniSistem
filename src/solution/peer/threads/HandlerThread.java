package solution.peer.threads;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import solution.n_queens.Job;
import solution.n_queens.ResultSet;
import solution.n_queens.UpdateJobPackage;
import solution.peer.NewNodeReorganizationHandler;
import solution.peer.NodeInfo;
import solution.peer.commPackage.Broadcast;
import solution.peer.commPackage.CommPackage;
import solution.peer.Node;
import solution.peer.commPackage.PackageType;
import solution.socket.MySocket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
                handleStartWalkBroadcast(p);
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
            case WALK:{
                handleWalkBroadcast(p);
                break;
            }
            case LEAVE_WALK:{
                handleLeaveWalkBroadcast(p);
                break;
            }
            case COUNT_WALK:{
                handleCountWalkBroadcast(p);
                break;
            }
            case BROADCAST:{
                handleBroadcast(p);
                break;
            }
            case UPDATE_FINGER_TABLE_BROADCAST:{
                handleUpdateFingerTableBroadcast(p);
                break;
            }
            case UPDATE_VISIBLENODES_BROADCAST:{
                handleUpdateVisibleNodesBroadcast(p);
                break;
            }
            case COLLECT_ALL_NODES_WALK:{
                handleCollectAllNodesWalk(p);
                break;
            }
            case UPDATE_RESULT_BROADCAST:{
                handleUpdateResultWalkBroadcast(p);
                break;
            }
            default:{
                System.err.println("default in handlerthread");
            }
        }
    }

    private Broadcast handleBroadcast(CommPackage p){
        Broadcast b = gson.fromJson(p.getMessage(), Broadcast.class);
        if (thisNode.recievedBroadcast(b)){
            System.out.println("Already recieved broadcast "+ b);
            return b;
        }
        thisNode.addToRecievedBroadcasts(b);
        System.out.println("Recieved broadcast: "+ b);
        MySocket mySocket;
        try {
            for (NodeInfo node : thisNode.getVisibleNodes()){
                mySocket = new MySocket(node.getNodeAddress(), node.getNodePort());

                mySocket.write(p);

                mySocket.close();

                System.out.println("Redirected broadcast to: "+ node.getNodeGUID());
            }
        } catch (Exception e){
            System.err.println("Exception in handleBroadcast\nStackTrace:");
            e.printStackTrace();
            return b;
        }

        return b;
    }

    private void handleUpdateFingerTableBroadcast(CommPackage p){
//        Broadcast b = gson.fromJson(p.getMessage(), Broadcast.class);
//        if (thisNode.recievedBroadcast(b)){
//            System.out.println("Already recieved broadcast "+ b);
//            return;
//        }
//        System.out.println("Recieved broadcast: "+ b);
//        MySocket mySocket;
//        try {
//            for (NodeInfo node : thisNode.getVisibleNodes()){
//                mySocket = new MySocket(node.getNodeAddress(), node.getNodePort());
//
//                mySocket.write(p);
//
//                mySocket.close();
//
//                System.out.println("Redirected broadcast to: "+ node.getNodeGUID());
//            }
//        } catch (Exception e){
//            System.err.println("Exception in handleBroadcast\nStackTrace:");
//            e.printStackTrace();
//            return;
//        }
//        thisNode.addToRecievedBroadcasts(b);
//
//        //System.out.println("DOBIO SAM UPDATEFINGERTABLE BROADCAST SA PORUKOM: " + b.getMessage());

        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            System.out.println("updatefingertable broadcast uspesno obavljen!");
        }
        else {
            System.out.println("Cvor "+ p.getSenderNode().getNodeGUID() +" salje updatefingertable broadcast, poruka: "+ p.getMessage());
            try {
                System.out.println("Prosledjujem updatefingertable broadcast...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        Broadcast b = gson.fromJson(p.getMessage(), Broadcast.class);
        ResultSet resultSet = gson.fromJson(b.getMessage(), ResultSet.class);
        thisNode.updateFingerTableForResultSet(resultSet);

    }

    private void handleStartWalkBroadcast(CommPackage p){
        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            System.out.println("Svi zapoceli poslove!");
            //ovde krecem updatefingertable broadcast
            ResultSet resultSet = new ResultSet(gson.fromJson(p.getMessage(),  ResultSet.class), thisNode.getNodeInfo());
            Broadcast broadcast = new Broadcast(thisNode.getNodeInfo().getNodeGUID(), gson.toJson(resultSet));
            //pravim commthread koji salje svima info o poslovima i gde su
            new CommunicatorThread(thisNode, new CommPackage(thisNode.getNodeInfo(), gson.toJson(broadcast), PackageType.UPDATE_FINGER_TABLE_BROADCAST, null)).start();
        }
        else {
            ResultSet resultSet = new ResultSet(gson.fromJson(p.getMessage(),  ResultSet.class), thisNode.getNodeInfo());
            Job myJob = resultSet.getFreeJob();
            resultSet.takeJob(myJob, thisNode.getNodeInfo());
            thisNode.getResults().addResultSet(resultSet.getN(), resultSet);
            p.setMessage(gson.toJson(resultSet));
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

    private void handleCollectAllNodesWalk(CommPackage p){
        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            System.out.println("collect nodes uspesno obavljen!");
            List<NodeInfo> nodes = gson.fromJson(p.getMessage(), new TypeToken<ArrayList<NodeInfo>>() {}.getType());
            if (!nodes.contains(thisNode.getNodeInfo())){
                nodes.add(thisNode.getNodeInfo());
            } else {
                System.err.println("Already have that node");
            }
            try {
                System.out.println("Saljem updateVisibleNodes broadcast...");
                Broadcast b = new Broadcast(thisNode.getNodeInfo().getNodeGUID(), gson.toJson(nodes));
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(new CommPackage(thisNode.getNodeInfo(), gson.toJson(b), PackageType.UPDATE_VISIBLENODES_BROADCAST, null));
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Cvor "+ p.getSenderNode().getNodeGUID() +" salje update visible walk, poruka: "+ p.getMessage());
            List<NodeInfo> nodes = gson.fromJson(p.getMessage(), new TypeToken<ArrayList<NodeInfo>>() {}.getType());
            nodes.add(thisNode.getNodeInfo());
            p.setMessage(gson.toJson(nodes));
            try {
                System.out.println("Prosledjujem collect nodes broadcast...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void handleUpdateVisibleNodesBroadcast(CommPackage p){
        Broadcast b = handleBroadcast(p);
        ArrayList<NodeInfo> nodes = gson.fromJson(b.getMessage(), new TypeToken<ArrayList<NodeInfo>>() {}.getType());
        thisNode.updateVisibleNodes(nodes);
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

    private void handleWalkBroadcast(CommPackage p){
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

    private void handleLeaveWalkBroadcast(CommPackage p){
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

    private void handleCountWalkBroadcast(CommPackage p){
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

    private void handleUpdateResultWalkBroadcast(CommPackage p){
        if (p.getSenderNode().getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
            System.out.println("Updateresult broadcast uspesno obavljen!");
        }
        else {
            UpdateJobPackage jobPackage = gson.fromJson(p.getMessage(), UpdateJobPackage.class);
            System.out.println("Stigao updatepackage, salje ga: "+ p.getSenderNode().getNodeGUID());
            thisNode.getResults().getResultSet(jobPackage.getN()).addJob(jobPackage.getJob());
            try {
                System.out.println("Prosledjujem updatejob broadcast...");
                socket = new MySocket(thisNode.getSuccessorNode().getNodeAddress(), thisNode.getSuccessorNode().getNodePort());
                socket.write(p);
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
