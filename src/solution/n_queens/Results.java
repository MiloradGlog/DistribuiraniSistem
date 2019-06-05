package solution.n_queens;

import java.util.HashMap;

public class Results {

    private HashMap<Integer, ResultSet> results;

    public Results() {
        this.results = new HashMap<>();
    }

    public boolean hasResultsFor(int n){
        if (results.containsKey(n))
            return true;
        return false;
    }

    public ResultSet getResultSet(int n){
        if (!results.containsKey(n)){
            System.err.println("Nemam taj resultset");
            return null;
        }
        return results.get(n);
    }

    public void addResultSet(int n, ResultSet resultSet){
        if (results.containsKey(n)){
            System.err.println("Already added!");
            return;
        }
        results.put(n, resultSet);
    }

    public HashMap<Integer, ResultSet> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "Results{" +
                "results=" + results +
                '}';
    }
}
