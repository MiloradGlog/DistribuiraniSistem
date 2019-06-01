package solution.bootstrap;

import solution.peer.Node;
import solution.peer.NodeInfo;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;
import solution.socket.MySocket;

import java.io.IOException;
import java.net.Socket;

public class BootstrapHandlerThread extends Thread {

    private MySocket socket;

    public BootstrapHandlerThread(Socket s) {
        try {
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
            switch (p.getType()) {
                case BOOTSTRAP_JOIN:{
                    System.out.println("Handling JOIN message type");
                    handleJoin(p, socket);
                    break;
                }
                case BOOTSTRAP_LEAVE:{
                    System.out.println("Handle leave");
                    handleLeave(p, socket);
                    break;
                }
                default: {
                    System.err.println("Bootstrap recieved package with wrong type");
                    break;
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleJoin(CommPackage p, MySocket socket){
        try {
            System.out.println("Servent ID:[" + p.getSenderNode() + "] bi da bude dodat");

            if (addNodeToList(p.getSenderNode())){
                p.setMessage("Uspesno dodao cvor u bootstrap");
            } else {
                p.setMessage("Dodavanje nije uspelo");
            }

            socket.write(p);

            socket.close();
        } catch (IOException ioException){
            System.err.println("IOException in HandlerThread run method\nStackTrace:");
            ioException.printStackTrace();
        }
        printStatus();
    }

    private void handleLeave(CommPackage p, MySocket socket){
        try {
            System.out.println("Servent ID:[" + p.getSenderNode() + "] bi da izadje");

            if (removeNodeFromList(p.getSenderNode())){
                p.setMessage("Uspesno obrisan sa bootstrapa");
            } else {
                p.setMessage("Brisanje nije uspelo");
            }

            socket.write(p);

            socket.close();
        } catch (IOException ioException){
            System.err.println("IOException in HandlerThread run method\nStackTrace:");
            ioException.printStackTrace();
        }
        printStatus();
    }

    private boolean addNodeToList(NodeInfo nodeInfo){
        return Bootstrap.getInstance().addNode(nodeInfo);
    }

    private boolean removeNodeFromList(NodeInfo nodeInfo){
        return Bootstrap.getInstance().removeNode(nodeInfo);
    }

    private void printStatus(){
        System.out.println("New status is:");
        Bootstrap.getInstance().printStatus();
    }
}