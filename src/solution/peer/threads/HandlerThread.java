package solution.peer.threads;

import solution.peer.commPackage.CommPackage;
import solution.peer.Node;
import solution.peer.commPackage.PackageType;
import solution.socket.MySocket;

import java.io.IOException;
import java.net.Socket;

public class HandlerThread extends Thread {

    private Node thisNode;
    private MySocket socket;

    public HandlerThread(Socket s, Node thisNode) {
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
                    System.out.println("Recieved ping response from "+ p.getSenderNode());
                    break;
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
            socket.write(new CommPackage(thisNode.getNodeInfo(),"primio ping!", PackageType.PING_RESPONSE, p.getSenderNode().getNodePort()));

            socket.close();
        } catch (IOException ioException){
            System.err.println("IOException in HandlerThread run method\nStackTrace:");
            ioException.printStackTrace();
        }

    }
}
