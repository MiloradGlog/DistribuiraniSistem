package solution.n_queens;

import java.util.ArrayList;
import java.util.Arrays;

public class NQueensSolver {
    // ovde ces da pamtis rezultate po k,v
    private ArrayList<int[][]> solutions;

    public NQueensSolver(){
        solutions = new ArrayList<>();
    }

    public void solve(int start, int end, int n){
        int[][] curr;
        System.out.println("iterations:");
        for (int i = start; i <= end; i++){
            curr = calculatePositionsBasedOnNumber(i,n);
            if (isSolution(curr, n)){
                solutions.add(curr);
            }
        }

        for (int[][] mat : solutions){
            printMatrix(mat, n);
            System.out.println();
        }
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
            row = number / (int)Math.pow(n, n-i);
            mat[i-1][row] = 1;
            number = number - (row * (int)Math.pow(n, n-i));
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
}
