package solution.n_queens;

import java.util.ArrayList;
import java.util.List;

public class Result {

    private List<int[][]> results;

    public Result(ArrayList<int[][]> results){
        this.results = results;
    }

    public Result() {
        this.results = new ArrayList<>();
    }

    public void addResult(int[][] result){
        results.add(result);
    }

    public List<int[][]> getResults() {
        return results;
    }

    public String getResultString(int n){
        StringBuilder sb = new StringBuilder();

        for (int[][] mat : results){
            for (int i = 0; i < n; i++){
                for (int j = 0; j < n; j++){
                    sb.append(mat[j][i]+" ");
                }
                sb.append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private void printMatrix(int[][] mat, int n){

    }

    @Override
    public String toString() {
        return "Result{" +
                "results=" + results +
                '}';
    }
}
