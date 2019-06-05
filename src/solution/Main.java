package solution;

import solution.n_queens.NQueensSolver;
import solution.peer.Node;
import solution.peer.NodeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {

    /**TODO
     * 1)Osmisli i implementiraj kradju posla
     *  1.1)Proveri progress racuna
     *  1.2)Napravi f-ju koja jedan posao deli na dva jednaka dela i kao rezultat vraca novi posao i testiraj u main-u
     *  1.3)Napravi f-ju koja se okida nakon sto sam zavrsio posao, nadje random job koji nije zavrsen i napravi zahtev za kradju od njega - za pocetak printaj
     *  1.4)Napravi poruku STEAL_JOB koja ima origin i target node, kad se salje, pita za taj posao koji je najblizi cvor i njemu salje, to isto radi u handle sve
     *      dok ne naidje na target cvor. testiraj printanjem.
     *  1.5)Kad naidje 
     * 2)Resi greske
     * ...
     */

    public static void main(String[] args){

        String configPath = args[0];

        new Node(configPath);

    }

}
