package solution.peer.commPackage;

import solution.peer.Node;
import solution.peer.NodeInfo;

public class CommPackage {

    private NodeInfo senderNode;
    private String message;
    private PackageType type;
    private int targetTempPort;

    public CommPackage(NodeInfo senderNode, String message, PackageType type, int targetTempPort){
        this.message = message;
        this.senderNode = senderNode;
        this.type = type;
        this.targetTempPort = targetTempPort;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NodeInfo getSenderNode() {
        return senderNode;
    }

    public void setSenderNode(NodeInfo senderNode) {
        this.senderNode = senderNode;
    }

    public PackageType getType() {
        return type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public int getTargetTempPort() {
        return targetTempPort;
    }

    public void setTargetTempPort(int targetTempPort) {
        this.targetTempPort = targetTempPort;
    }
}
