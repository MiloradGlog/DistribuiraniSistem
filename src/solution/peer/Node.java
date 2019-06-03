package solution.peer;

import com.google.gson.Gson;
import solution.configuration.NodeConfigModel;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;
import solution.peer.threads.CLIThread;
import solution.peer.threads.CommunicatorThread;
import solution.peer.threads.MainServerThread;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {

    private String configFilePath;
    private Gson gson;
    private NodeInfo nodeInfo;
    private NodeConfigModel configModel;
    private List<NodeInfo> visibleNodes;
    private NodeInfo successorNode;

    public Node (String configFilePath){
        this.visibleNodes = Collections.synchronizedList(new ArrayList<NodeInfo>());
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

            joinBootstrap();

        } catch (IOException ioException) {
            System.err.println("Error when initializing Node: "+ nodeInfo.getNodeGUID() + "\nStackTrace:");
            ioException.printStackTrace();
        }

    }

    public void joinBootstrap(){
        CommPackage p = new CommPackage(getNodeInfo(), "", PackageType.BOOTSTRAP_JOIN, null);
        CommunicatorThread communicatorThread = new CommunicatorThread(this, p);
        communicatorThread.run();
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

    public void setSuccessorNode(NodeInfo nodeInfo){
        synchronized (visibleNodes){
            if (!hasVisibleNode(nodeInfo)) {
                addVisibleNode(nodeInfo);
                System.out.println("added successor guid:"+ nodeInfo.getNodeGUID() +" to visiblenodes");
            }
            if (successorNode != null){
                for (NodeInfo n : visibleNodes){
                    if (n.getNodeGUID() == successorNode.getNodeGUID()){
                        visibleNodes.remove(n);
                    }
                }
            }
        }
        successorNode = nodeInfo;
    }

    public boolean addVisibleNode(NodeInfo nodeInfo){
        if (hasVisibleNode(nodeInfo)){
            System.err.println("Already has that node in visiblenodes");
            return false;
        }
        visibleNodes.add(nodeInfo);
        System.out.println("Dodao cvor "+ nodeInfo.getNodeGUID() + " u visible nodes");
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

    public NodeInfo getLargestGUIDVisibleNode(){
        if (visibleNodes.isEmpty()){
            System.err.println("Error in getLargestGUIDVisibleNodeLesserThan, visibleNodes is empty, returning null...\n");
            return null;
        }
        NodeInfo max = visibleNodes.get(0);
        for (NodeInfo node : visibleNodes){
            if (node.getNodeGUID() > max.getNodeGUID()){
                max = node;
            }
        }
        return max;
    }

    public NodeInfo getLargestGUIDVisibleNodeLesserThan(int n){
        if (visibleNodes.isEmpty()){
            System.err.println("Error in getLargestGUIDVisibleNodeLesserThan, visibleNodes is empty, returning null...\n");
            return null;
        }
        NodeInfo max = visibleNodes.get(0);
        for (NodeInfo node : visibleNodes){
            if (node.getNodeGUID() > max.getNodeGUID()){
                if (node.getNodeGUID() < n){
                    max = node;
                }
            }
        }
        return max;
    }

    public NodeInfo getLargestGUIDVisibleNodeLesserThanIncludingThisNode(int n){
        if (visibleNodes.isEmpty()){
            System.err.println("Error in getLargestGUIDVisibleNodeLesserThanIncludingThisNode, visibleNodes is empty, returning null...\n");
            return null;
        }
        NodeInfo max = nodeInfo;
        for (NodeInfo node : visibleNodes){
            if (node.getNodeGUID() > max.getNodeGUID()){
                if (node.getNodeGUID() < n){
                    max = node;
                }
            }
        }
        return max;
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

    public NodeInfo getSuccessorNode() {
        return successorNode;
    }

    public List<NodeInfo> getVisibleNodes() {
        return visibleNodes;
    }

    private boolean hasVisibleNode(NodeInfo nodeInfo){
        for (NodeInfo n : visibleNodes){
            if (n.getNodeGUID() == nodeInfo.getNodeGUID()){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return nodeInfo.toString();
    }
}
