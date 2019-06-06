package solution;

import solution.n_queens.Job;
import solution.n_queens.NQueensSolver;
import solution.peer.Node;
import solution.peer.NodeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {

    /**TODO
     * 1)CS Token
     *  1.1)Kad cvor udje doda se u mapu
     *  1.5)Posalji svima updateovan rnmap
     *  1.6)Ako imam token dodaj me u queue, ako nemam, taj ko vidi da sam ja povecao me doda u queue
     *  1.7)Napravi popqueue funkciju koja ce da proveri koji je sledeci u queue, ako je to ovaj cvor popuje i krece izvrsavanje, ako nije prosledi token tome ko je na redu
     *
     * 2)Kradja posla
     *  1.5)Posalji nazad ukradeni posao i broadcastuj svima smanjeni posao.
     *  1.6)Koristi postojeci updatejob kako bi prvo iz onog gde si ukrao poslao svima da updateuju taj job, a onda kad se vrati iz lopova da updateuju (dodaju) novi job
     *  1.7)Implementiraj limit
     * 3)Resi greske
     * ...
     *
     * poznati bagovi : nekad nece fingertablea da se updateuje
     */

    public static void main(String[] args){

        String configPath = args[0];

        new Node(configPath);

//        testSteal();

    }

//    private static void testSteal(){
//        Job job = new Job(0, 5000, 6);
//        job.setProgress(70);
//        System.out.println("Delim posao: "+ job);
//        int jobEnd = job.getRangeEnd();
//        int range = jobEnd - job.getRangeStart();
//        int remainingRange = range/100*(100 - job.getProgress());
//        int split = jobEnd-remainingRange/2;
//        job.setRangeEnd(split);
//        Job job2 = new Job(split, jobEnd, job.getMatrixSize());
//        System.out.println("Nakon podele:");
//        System.out.println(job);
//        System.out.println(job2);
//
//    }

}
