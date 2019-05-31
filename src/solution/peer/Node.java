package solution.peer;

import solution.peer.threads.CLIThread;
import solution.peer.threads.MainServerThread;

import java.io.IOException;

public class Node {

    private int nodeGUID;
    private int nodePort;
    private String nodeAddress;

    public Node(int nodeGUID, int nodePort, String nodeAddress) {
        this.nodeGUID = nodeGUID;
        this.nodePort = nodePort;
        this.nodeAddress = nodeAddress;
        this.initialize();
    }

    private void initialize(){
        try {
            CLIThread cliThread = new CLIThread(this);
            MainServerThread serverThread = new MainServerThread(this);

            cliThread.start();
            serverThread.start();

        } catch (IOException ioException) {
            System.err.println("Error when initializing Node: "+ nodeGUID + "\nStackTrace:");
            ioException.printStackTrace();
        }

    }

    public int getNodeGUID() {
        return nodeGUID;
    }

    public void setNodeGUID(int nodeGUID) {
        this.nodeGUID = nodeGUID;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }
}
