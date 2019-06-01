package solution;

import solution.bootstrap.Bootstrap;
import solution.peer.Node;

public class Main {

    public static void main(String[] args){

        String configPath = args[0];
        Bootstrap.getInstance();

        new Node(configPath);

        /**TODO
         * 1)Zavrsi visibleNodes u Node
         * 2)Bootstrap da vraca neki random node nakon joina tako sto pretvori nodeinfo u json i spakuje u poruku koju vraca
         * 3)Napravi da cim se cvor startuje kontaktira bootstrap
         * ...
         */

    }

}
