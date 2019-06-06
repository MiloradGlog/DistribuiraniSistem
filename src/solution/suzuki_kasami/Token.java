package solution.suzuki_kasami;

import solution.peer.NodeInfo;

import java.util.HashMap;
import java.util.Queue;

public class Token {

    private HashMap<NodeInfo, Integer> lnMap;
    private Queue<NodeInfo> queue;

    public Token(HashMap<NodeInfo, Integer> lnMap, Queue<NodeInfo> queue) {
        this.lnMap = lnMap;
        this.queue = queue;
    }

    public HashMap<NodeInfo, Integer> getLnMap() {
        return lnMap;
    }

    public Queue<NodeInfo> getQueue() {
        return queue;
    }
}
