package solution.n_queens;

import solution.n_queens.Job;
import solution.peer.NodeInfo;

import java.util.*;

public class FingerTable {

    private HashMap<Job, NodeInfo> table;
    private NodeInfo thisNode;

    public FingerTable(NodeInfo thisNode){
        table = new HashMap<>();
        this.thisNode = thisNode;
    }

    public void addPair(Job key, NodeInfo value){
        if (table.containsKey(key)) {
            System.out.println("Replacing value in fingerTable!");
        }
        else {
            System.out.println("Added job to fingertable");
            table.put(key, value);
        }
    }

    public HashMap<Job, NodeInfo> getTable() {
        return table;

    }

    public void updateFingerTable(List<NodeInfo> visibleNodes, ResultSet resultSet){
        table = new HashMap<>();
        List<NodeInfo> nodes = new ArrayList<>();
        for (Job j : resultSet.getJobs()){
            if (!nodes.contains(j.getAssignedTo())){
                nodes.add(j.getAssignedTo());
            }
        }

        Collections.sort(nodes, Comparator.comparing(NodeInfo::getNodeGUID) );
        int myIndex = 0;
        while (nodes.get(myIndex).getNodeGUID() != thisNode.getNodeGUID()){
            myIndex++; //dobar
        }

        int listSize = nodes.size();
        NodeInfo closestNodeToResult = null;


        for (Job j : resultSet.getJobs()){
            for (int i = 0; i < listSize - 1 ; i++){
                NodeInfo node = nodes.get(++myIndex % listSize);
                for (NodeInfo n : visibleNodes){
                    if (n.getNodeGUID() == node.getNodeGUID()){
                        closestNodeToResult = node;
                    }
                }
                if (node.getNodeGUID() == j.getAssignedTo().getNodeGUID()){
                    table.put(j, closestNodeToResult);
                }
            }
        }
        System.out.println("Nova fingertabela je:");
        System.out.println(table);
    }

    public Job findKeyByJob(Job j){
        for (Job job : getKeySet()){
            if (job.equals(j)){
                return job;
            }
        }
        System.err.println("couldnt find that job in fingertable [findkeybyjob], returning null");
        return null;
    }

    public Set<Job> getKeySet(){
        return table.keySet();
    }
}
