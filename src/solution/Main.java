package solution;

import solution.peer.Node;
import solution.peer.NodeInfo;

public class Main {

    public static void main(String[] args){

        String configPath = args[0];

        new Node(configPath);

        /**TODO
         * 1)Napravi da cim se cvor startuje kontaktira bootstrap
         * 2)Napravi jednostavan broadcast
         * 3)Napravi broadcast nakon leave
         *  3.1) Cvor koji izlazi posalje broadcast
         *  3.2) U message zahteva spakuje svog sledbenika
         *  3.3) Kad broadcast poruka dodje do poslednjeg u prstenu (do cvora kome je sledbenik cvor koji izlazi),
         *       setuje njegovog sledbenika da bude sledbenik cvora koji izlazi i javi cvoru koji izlazi da je svima uspesno
         *       javljeno da on napusta mrezu, te on onda bezbedno izlazi.
         * 4)Napravi thread? i algoritam za izracunavanje problema n kraljica i pocni work stealing.
         * ...
         */

    }

}
