package solution.peer.commPackage;

import solution.peer.NodeInfo;

public class CommPackage {

    private NodeInfo senderNode;
    private String message;
    private PackageType type;
    private NodeInfo targetNode;

    public CommPackage(NodeInfo senderNode, String message, PackageType type, NodeInfo targetNode){
        this.message = message;
        this.senderNode = senderNode;
        this.type = type;
        this.targetNode = targetNode;
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

    public NodeInfo getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(NodeInfo targetNode) {
        this.targetNode = targetNode;
    }
}
