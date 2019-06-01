package solution.configuration;

import com.google.gson.Gson;

import java.io.PrintWriter;

public class NodeConfigModel {

    private int nodeGUID;
    private int nodePort;
    private int nodeLimit;
    private String nodeAddress;
    private String bootstrapAddress;
    private int bootstrapPort;

    public NodeConfigModel(int nodeGUID, int nodePort, int nodeLimit, String nodeAddress) {
        this.nodeGUID = nodeGUID;
        this.nodePort = nodePort;
        this.nodeLimit = nodeLimit;
        this.nodeAddress = nodeAddress;
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

    public int getNodeLimit() {
        return nodeLimit;
    }

    public void setNodeLimit(int nodeLimit) {
        this.nodeLimit = nodeLimit;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getBootstrapAddress() {
        return bootstrapAddress;
    }

    public void setBootstrapAddress(String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }

    public int getBootstrapPort() {
        return bootstrapPort;
    }

    public void setBootstrapPort(int bootstrapPort) {
        this.bootstrapPort = bootstrapPort;
    }

    @Override
    public String toString() {
        return "NodeConfigModel{" +
                "nodeGUID=" + nodeGUID +
                ", nodePort=" + nodePort +
                ", nodeLimit=" + nodeLimit +
                '}';
    }
}
