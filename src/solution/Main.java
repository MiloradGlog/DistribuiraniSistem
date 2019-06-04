package solution;

import solution.n_queens.NQueensSolver;
import solution.peer.Node;
import solution.peer.NodeInfo;

public class Main {

    /**TODO
     * 1)Napravi thread? i algoritam za izracunavanje problema n kraljica.
     *
     * 2)Osmisli i implementiraj deljenje posla
     * 3)Osmisli i implementiraj kradju posla
     * 4)Resi greske
     * ...
     */

    public static void main(String[] args){

        String configPath = args[0];

        new Node(configPath);

//        int n = 8;
//        new NQueensSolver().solve(1, (int)Math.pow(n,n), n);


    }

}
