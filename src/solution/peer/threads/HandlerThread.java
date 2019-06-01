package solution.peer.threads;

import com.google.gson.Gson;
import solution.peer.NodeInfo;
import solution.peer.commPackage.CommPackage;
import solution.peer.Node;
import solution.peer.commPackage.PackageType;
import solution.socket.MySocket;

import java.io.IOException;
import java.net.Socket;

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
            switch (p.getType()) {
                case START:{
                    System.out.println("Handling START message type");
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
                    System.out.println("Handling join");
                    handleBootstrapJoin(p);
                    break;
                }
                case BOOTSTRAP_LEAVE:{
                    handleBootstrapLeave(p);
                    break;
                }
                default:{
                    System.err.println("default in handlerthread");
                }
            }

        } catch (IOException e){
            e.printStackTrace();
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

            System.out.println("Primljeni cvor je "+ recievedNode);

        }

    }

    private void handleBootstrapLeave(CommPackage p){

        System.out.println("Bootstrap odgovara: "+ p.getMessage());

    }
}
