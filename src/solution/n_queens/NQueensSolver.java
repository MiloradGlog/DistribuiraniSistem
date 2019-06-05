package solution.n_queens;

import java.util.ArrayList;
import java.util.Arrays;

public class NQueensSolver {
    // ovde ces da pamtis rezultate po k,v
    private Result result;
    private boolean finished;
    private int n;
    private int progress;

    public NQueensSolver(){
        finished = false;
        result = new Result();
    }

    public void solve(int start, int end, int n){
        finished = false;
        progress = 0;
        int range = end - start;
        this.n = n;
        int[][] curr;
        System.out.println("iterations:");
        for (int i = start; i <= end; i++){
            curr = calculatePositionsBasedOnNumber(i,n);
            if (isSolution(curr, n)){
                result.addResult(curr);
            }
            progress = ((i - start + 1) / range) * 100;
        }
        //printResults();
        finished = true;
        progress = 100;
    }


    public int[][] calculatePositionsBasedOnNumber(int number, int n){
        //napravimo i popunimo matricu nulama
        int[][] mat = new int[n][n];
        for (int[] row : mat){
            Arrays.fill(row, 0);
        }
        //smanjim broj jer u racunu krece od 0 dok logicki krece od 1
        number--;

        int row = 0;

        for (int i = 1; i < n; i++){
            try {
                row = number / (int)Math.pow(n, n-i);
                mat[i-1][row] = 1;
                number = number - (row * (int)Math.pow(n, n-i));
            } catch (ArrayIndexOutOfBoundsException e){
                System.err.println("ArrayIndexOutOfBounds za broj: "+ number +" u calculatePositionsBasedOnNumber.\nStackTrace:");
                e.printStackTrace();
            }
        }
        mat[n-1][number] = 1;

        return mat;
    }

    private void printMatrix(int[][] mat, int n){
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                System.out.print(mat[j][i]+" ");
            }
            System.out.println();
        }
    }

    public void printResults(){

        for (int[][] mat : result.getResults()){
            printMatrix(mat, n);
            System.out.println();
        }
    }

    private boolean isSolution(int[][] mat, int n){
        int kolona = 0, red = 0, i = 0, j = 0;
        for (kolona = 0; kolona < n; kolona++){
            //saltam kolone
            red = 0;
            while(mat[kolona][red] != 1)
                red++;
            //nasao red kraljice (j) u koloni (i)
            for (int a = 0; a < n; a++){
                //proveri red za pronadjenu kraljicu
                if (mat[a][red] == 1 && a != kolona){
                    return false;
                }
            }
            //proveri dole levo (red + [limit n-1], kolona - [limit 0]
            i = kolona;
            j = red;
            while (i >= 0 && j < n){
                i--;
                j++;
                if (i < 0 || j < 0 || i >= n || j >= n)
                    continue;
                if (mat[i][j] == 1){
                    return false;
                }
            }
            //proveri gore levo (red -, kol -)
            i = kolona;
            j = red;
            while (i >= 0 && j >= 0){
                i--;
                j--;
                if (i < 0 || j < 0 || i >= n || j >= n)
                    continue;
                if (mat[i][j] == 1){
                    return false;
                }
            }
            //proveri dole desno (red++, kol++)
            i = kolona;
            j = red;
            while (i < n && j < n){
                i++;
                j++;
                if (i < 0 || j < 0 || i >= n || j >= n)
                    continue;
                if (mat[i][j] == 1){
                    return false;
                }
            }
            //proveri gore desno
            i = kolona;
            j = red;
            while (i < n && j >= 0){
                i++;
                j--;
                if (i < 0 || j < 0 || i >= n || j >= n)
                    continue;
                if (mat[i][j] == 1){
                    return false;
                }
            }
        }
        return true;
    }

    public int getProgress() {
        return progress;
    }

    public Result getResult() {
        return result;
    }

    public boolean isFinished() {
        return finished;
    }
}
