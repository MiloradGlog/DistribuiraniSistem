package solution.peer;

import solution.n_queens.Job;

import java.util.HashMap;
import java.util.Set;

public class FingerTable {

    private HashMap<Job, NodeInfo> table;

    public FingerTable(){
        table = new HashMap<>();
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

    public Set<Job> getKeySet(){
        return table.keySet();
    }


}
