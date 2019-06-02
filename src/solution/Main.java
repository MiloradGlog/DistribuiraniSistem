package solution;

import solution.peer.Node;
import solution.peer.NodeInfo;

public class Main {

    public static void main(String[] args){

        String configPath = args[0];

        new Node(configPath);

        /**TODO
         * 1)Kad cvor dobije nazad radnom cvor, desava se reorganizacija sistema
         * 1.1)U iz handlerthread dodatog cvora prosledi zahtev za 'razmestanje' cvoru koji si dobio od bootstrapa
         * 1.2)Implementirati algoritam za 'razmestanje"
         * 2)Napravi da cim se cvor startuje kontaktira bootstrap
         * ...
         */

    }

}
