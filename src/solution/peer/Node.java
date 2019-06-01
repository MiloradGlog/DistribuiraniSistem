package solution.peer;

import com.google.gson.Gson;
import solution.configuration.NodeConfigModel;
import solution.peer.threads.CLIThread;
import solution.peer.threads.MainServerThread;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Node {

    private String configFilePath;
    private Gson gson;
    private NodeInfo nodeInfo;
    private NodeConfigModel configModel;
    private ArrayList<NodeInfo> visibleNodes;

    public Node (String configFilePath){
        this.gson = new Gson();
        this.configModel = readConfig(configFilePath);
        this.configFilePath = configFilePath;
        this.nodeInfo = new NodeInfo(configModel.getNodeGUID(), configModel.getNodePort(), configModel.getNodeAddress());

        this.initialize();
    }
/*
    public Node(int nodeGUID, int nodePort, String nodeAddress, String configFilePath) {
        this.gson = new Gson();
        this.configFilePath = configFilePath;
        this.nodeGUID = nodeGUID;
        this.nodePort = nodePort;
        this.nodeAddress = nodeAddress;
        this.initialize();
    }
*/
    private void initialize(){
        try {
            System.out.println("Initializing node: "+ this.toString());

            CLIThread cliThread = new CLIThread(this);
            MainServerThread serverThread = new MainServerThread(this, false);

            cliThread.start();
            serverThread.start();

        } catch (IOException ioException) {
            System.err.println("Error when initializing Node: "+ nodeInfo.getNodeGUID() + "\nStackTrace:");
            ioException.printStackTrace();
        }

    }

    private NodeConfigModel readConfig(String path){
        StringBuilder sb = new StringBuilder();
        try {
            for (String s : Files.readAllLines(Paths.get(path))){
                sb.append(s);
            }
        } catch (IOException e){
            System.err.println("Error reading the configuration file\nStackTrace:");
            e.printStackTrace();
        }
        NodeConfigModel model = gson.fromJson(sb.toString(), NodeConfigModel.class);
        return model;
    }

    public void saveConfig(){
        try (PrintWriter out = new PrintWriter("files/config.json")) {
            out.println(gson.toJson(configModel));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean addVisibleNode(NodeInfo nodeInfo){
        for (NodeInfo n : visibleNodes){
            if (n.getNodeGUID() == nodeInfo.getNodeGUID()){
                return false;
            }
        }
        visibleNodes.add(nodeInfo);
        return true;
    }
    public boolean removeVisibleNode(NodeInfo nodeInfo){
        for (NodeInfo n : visibleNodes){
            if (n.getNodeGUID() == nodeInfo.getNodeGUID()){
                visibleNodes.remove(n);
                return true;
            }
        }
        return false;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public NodeConfigModel getConfigModel() {
        return configModel;
    }

    public ArrayList<NodeInfo> getVisibleNodes() {
        return visibleNodes;
    }

    @Override
    public String toString() {
        return nodeInfo.toString();
    }
}
