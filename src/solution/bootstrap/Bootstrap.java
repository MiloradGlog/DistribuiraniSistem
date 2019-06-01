package solution.bootstrap;

import solution.peer.NodeInfo;
import solution.peer.threads.MainServerThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Bootstrap {

    private String boostrapAddress;
    private int bootstrapPort;
    private ArrayList<NodeInfo> activeNodes;

    private static Bootstrap instance;

    private Bootstrap() {
        this.boostrapAddress = "127.0.0.1";
        this.bootstrapPort = 8080;
        this.activeNodes = new ArrayList<>();
        //ne sme init ovde zbog singletona
    }

    public static Bootstrap getInstance(){
        if (instance == null){
            instance = new Bootstrap();
        }
        return instance;
    }

    private void initialize(){
        try {
            System.out.println("Initializing bootstrap...");

            MainServerThread serverThread = new MainServerThread(null, true);

            serverThread.start();

        } catch (IOException ioException) {
            System.err.println("Error when initializing bootstrap\nStackTrace:");
            ioException.printStackTrace();
        }
    }

    public boolean addNode(NodeInfo nodeInfo){
        for (NodeInfo n : activeNodes){
            if (n.getNodeGUID() == nodeInfo.getNodeGUID()){
                return false;
            }
        }
        activeNodes.add(nodeInfo);
        return true;
    }

    public boolean removeNode(NodeInfo nodeInfo){
        for (NodeInfo n : activeNodes){
            if (n.getNodeGUID() == nodeInfo.getNodeGUID()){
                activeNodes.remove(n);
                return true;
            }
        }
        return false;
    }

    public NodeInfo getRandomNode(){
        if (activeNodes.isEmpty())
            return null;
        int rand =  (int)(Math.random()*activeNodes.size());
        return activeNodes.get(rand);
    }

    public String getBoostrapAddress() {
        return boostrapAddress;
    }

    public int getBootstrapPort() {
        return bootstrapPort;
    }

    public ArrayList<NodeInfo> getActiveNodes() {
        return activeNodes;
    }

    public void printStatus(){
        System.out.println(activeNodes);
    }

    public static void main(String[] args){
        getInstance().initialize();
    }

}
