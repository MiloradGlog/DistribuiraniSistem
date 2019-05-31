package solution.configuration;

import java.util.ArrayList;

public class ConfigModel {

    private ArrayList<NodeConfigModel> nodes;
    private String bootstrapAddress;
    private int bootstrapPort;

    public ConfigModel(String bootstrapAddress, int bootstrapPort) {
        this.nodes = new ArrayList<>();
        this.bootstrapAddress = bootstrapAddress;
        this.bootstrapPort = bootstrapPort;
    }

    public ArrayList<NodeConfigModel> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<NodeConfigModel> nodes) {
        this.nodes = nodes;
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
        return "ConfigModel{" +
                "nodes=" + nodes +
                ", bootstrapAddress='" + bootstrapAddress + '\'' +
                ", bootstrapPort=" + bootstrapPort +
                '}';
    }
}
