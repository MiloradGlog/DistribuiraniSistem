package solution.peer;

import com.google.gson.Gson;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;
import solution.peer.threads.CommunicatorThread;
import solution.socket.MySocket;

import java.io.IOException;

public class NewNodeReorganizationHandler {

    private Node thisNode;
    private MySocket socket;
    private Gson gson;

    public NewNodeReorganizationHandler(MySocket socket, Node thisNode) {
        this.gson = new Gson();
        this.thisNode = thisNode;
        this.socket = socket;
    }


    public void handle(CommPackage p){
        System.out.println("dobio zahtev za reorganizaciju od cvora: "+ p.getSenderNode());
        NodeInfo newNode = p.getSenderNode();

        if (thisNode.getVisibleNodes().isEmpty()){
            //Ako cvor ne vidi nikoga, dakle sam je, salje novom cvoru sebe kao njegovog "naslednka" i sebi setuje njega kao svog naslednika (samo njih dvojica su u mrezi)
            sendSetSuccessorRequest(p, thisNode.getNodeInfo());
            thisNode.setSuccessorNode(newNode);
        }
        else {
            //Ako cvor vidi druge cvorove, trazi odgovarajuci da vrati novom cvoru kao "naslednika"
            if (thisNode.getNodeInfo().getNodeGUID() < newNode.getNodeGUID()) {
                //Ako je trenutni manji od novog cvora
                System.out.println("Ja sam manji od novog cvora");
                NodeInfo n = thisNode.getLargestGUIDVisibleNodeLesserThanIncludingThisNode(newNode.getNodeGUID());

                if (n.getNodeGUID() == thisNode.getNodeInfo().getNodeGUID()){
                    System.out.println("Ja sam taj cvor");
                    insertNewNodeAsMySuccessor(newNode, p);
                }
                else {
                    System.out.println("Ja nisam taj cvor");
                    //prosledi zahtev cvoru n
                    p.setTargetNode(n);
                    new CommunicatorThread(thisNode, p).run();
                }

            }
            else {
                //Ako je trenutni veci od novog cvora
                System.out.println("Ja sam veci od novog cvora");

                if (this.thisNode.getSuccessorNode().getNodeGUID() < this.thisNode.getNodeInfo().getNodeGUID()){
                    //Moj naslednik je manji od mene, dakle ja sam na kraju prstena
                    if (this.thisNode.getSuccessorNode().getNodeGUID() > newNode.getNodeGUID()){
                        //sledeci je veci od novog
                        insertNewNodeAsMySuccessor(newNode, p);
                    }
                    else {
                        //sledeci je manji od novog
                        //prosledi zahtev mom sledbeniku
                        p.setTargetNode(this.thisNode.getSuccessorNode());
                        new CommunicatorThread(thisNode, p).run();
                    }
                }
                else {
                    //Moj naslednik je veci od mene, dakle nisam na kraju prstena
                    //prosledi zahtev najvecem koji vidim
                    NodeInfo n = thisNode.getLargestGUIDVisibleNode();
                    p.setTargetNode(n);
                    new CommunicatorThread(thisNode, p).run();
                }
            }
        }

    }

    //Kada pronadje naslednika za novi cvor, salje mu njegovog naslednika
    private void sendSetSuccessorRequest(CommPackage p, NodeInfo successorNode){
        try {
            socket = new MySocket(p.getSenderNode().getNodeAddress(), p.getSenderNode().getNodePort());
            socket.write(new CommPackage(
                    thisNode.getNodeInfo(), //ovaj cvor salje
                    gson.toJson(successorNode), //u poruci spakujemo naslednika u json
                    PackageType.SET_SUCCESSOR, //saljemo setsuccessor request novom cvoru
                    p.getSenderNode())); //novi cvor kome saljemo naslednika

            socket.close();
        } catch (IOException e){
            System.err.println("IOException u handlebootstrapjoin\nStackTrace:");
            e.printStackTrace();
        }
    }

    private void insertNewNodeAsMySuccessor(NodeInfo newNode, CommPackage p){
        sendSetSuccessorRequest(p, thisNode.getSuccessorNode());
        thisNode.setSuccessorNode(newNode);
    }

}
