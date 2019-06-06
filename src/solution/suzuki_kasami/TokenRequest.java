package solution.suzuki_kasami;

import solution.peer.NodeInfo;

public class TokenRequest {

    private NodeInfo node;
    private int count;

    public TokenRequest(NodeInfo node, int count) {
        this.node = node;
        this.count = count;
    }

    public NodeInfo getNode() {
        return node;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "TokenRequest{" +
                "node=" + node +
                ", count=" + count +
                '}';
    }
}
