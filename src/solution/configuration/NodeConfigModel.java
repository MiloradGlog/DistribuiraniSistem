package solution.configuration;

public class NodeConfigModel {

    private int nodeGUID;
    private int nodePort;
    private int nodeLimit;
    private String bootstrapAddress;
    private int bootstrapPort;

    public NodeConfigModel(int nodeGUID, int nodePort, int nodeLimit) {
        this.nodeGUID = nodeGUID;
        this.nodePort = nodePort;
        this.nodeLimit = nodeLimit;
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

    @Override
    public String toString() {
        return "NodeConfigModel{" +
                "nodeGUID=" + nodeGUID +
                ", nodePort=" + nodePort +
                ", nodeLimit=" + nodeLimit +
                '}';
    }
}
