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
            System.out.println("Poruka od klijenta [" + p.getSenderGUID() + "]: "+ p.getMessage());

            socket.write(new CommPackage(thisNode.getNodeGUID(),"handler salje neku poruku", PackageType.MESSAGE));

            socket.close();
        } catch (IOException ioException){
            System.err.println("IOException in HandlerThread run method\nStackTrace:");
            ioException.printStackTrace();
        }
    }
}
