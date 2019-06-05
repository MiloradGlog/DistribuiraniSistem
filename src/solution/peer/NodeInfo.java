package solution.peer;

public class NodeInfo {

    private int nodeGUID;
    private int nodePort;
    private String nodeAddress;

    public NodeInfo(int nodeGUID, int nodePort, String nodeAddress) {
        this.nodeGUID = nodeGUID;
        this.nodePort = nodePort;
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

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }


    @Override
    public String toString() {
        return "NodeInfo{" +
                "nodeGUID=" + nodeGUID +
                ", nodePort=" + nodePort +
                ", nodeAddress='" + nodeAddress + '\'' +
                '}';
    }
}
