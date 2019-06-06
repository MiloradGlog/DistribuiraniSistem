package solution.peer.threads;

import com.google.gson.Gson;
import solution.n_queens.Job;
import solution.n_queens.ResultSet;
import solution.peer.Node;
import solution.peer.NodeInfo;
import solution.peer.commPackage.Broadcast;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;
import solution.suzuki_kasami.TokenRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CLIThread extends Thread {

    private Scanner in;
    private Node thisNode;
    private Gson gson;

    public CLIThread(Node node){
        this.thisNode = node;
        gson = new Gson();
        in = new Scanner(System.in);
    }

    @Override
    public void run() {
        while(true){
            String inputString = in.nextLine();
            interpret(inputString);
            try {
                sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void interpret(String str){
        StringTokenizer tokenizer = new StringTokenizer(str);
        String commandString = tokenizer.nextToken();
        String parameterString = getParameterFromTokenizer(tokenizer);
        switch (commandString){
            case ("start"): {
                int n = Integer.parseInt(parameterString);
                System.out.println("starting for X = "+ n);
                //proveri dal vec ima rezultat za zadato X
                if (thisNode.getResults().hasResultsFor(n)){
                    showResultForX(n);
                    break;
                }

                //sendRequestToken();

                //prebaci u tren kad se popuje ovaj cvor iz queuea;
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), "1", PackageType.COUNT_WALK, new NodeInfo(n, 0, "")));//nodeinfo nosi X
                break;

            }
            case ("result"): {
                int n = Integer.parseInt(parameterString);
                showResultForX(n);
                break;
            }
            case ("join"): {
                System.out.println("joining...");
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), "", PackageType.BOOTSTRAP_JOIN, null));
                break;
            }
            case ("leave"): {
                System.out.println("leaving...");
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), "", PackageType.BOOTSTRAP_LEAVE, null));
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), gson.toJson(thisNode.getSuccessorNode()), PackageType.LEAVE_WALK, null));
                break;
            }case ("broadcast"): {
                System.out.println("Attempting broadcast...");
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), gson.toJson(new Broadcast(thisNode.getNodeInfo().getNodeGUID(), parameterString)), PackageType.BROADCAST, null));
                break;
            }
            case ("status"): {
                System.out.println("Cvor: "+ thisNode);
                try {
                    System.out.println("Naslednik mu je: "+ thisNode.getSuccessorNode().getNodeGUID());
                } catch (NullPointerException e){
                    System.out.println("Jedini cvor u mrezi.");
                }
                System.out.println("Vidi cvorove: \n"+ thisNode.getVisibleNodes());
                System.out.println("Results:"+ thisNode.getResults());
                break;
            }
            case ("updatevisible"):{
                System.out.println("Attempting to update visible nodes for all...");
                List<NodeInfo> nodes = new ArrayList<>();
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), gson.toJson(nodes), PackageType.COLLECT_ALL_NODES_WALK, null));
                break;
            }
            case ("progress"):{
                System.out.println("Trenutni napredak: "+ thisNode.getCurrentSolverProgress());
                break;
            }
            case ("teststeal"):{
                System.out.println("Testiram steal");
                int n = Integer.parseInt(parameterString);

                ResultSet resultSet = thisNode.getResults().getResultSet(n);
                Job job = resultSet.getRandomUnfinishedJob(thisNode.getNodeInfo());

                System.out.println("Hocu da pokradem posao:"+ job);

                job = resultSet.getFingerTable().findKeyByJob(job);
                NodeInfo targetNode = resultSet.getFingerTable().getTable().get(job);
                if (targetNode == null){
                    System.err.println("targetNode in teststeal is null, setting to successor");
                    targetNode = thisNode.getSuccessorNode();
                }
                System.out.println("hocu da pokradem cvor "+ targetNode);

                CommPackage p = new CommPackage(thisNode.getNodeInfo(), gson.toJson(job), PackageType.STEAL_JOB, targetNode);

                sendCommPackageViaCommThread(p);

                break;
            }
            default: {
                System.out.println("Nepoznata komanda");
                break;
            }

        }
    }

    private void showResultForX(int n){
        System.out.println("showing results for "+ n);
        if (thisNode.getResults().hasResultsFor(n)){
            System.out.println(thisNode.getResults().getResultSet(n));
        }else {
            System.out.println("Nemam rezultate za to n");
        }

    }

    private void sendRequestToken(){

        System.out.println("map before increment:\n"+ thisNode.getRnMap());
        thisNode.getRnMap().put(thisNode.getNodeInfo(), thisNode.getRnMap().get(thisNode.getNodeInfo()));
        System.out.println("map after increment:\n"+ thisNode.getRnMap());
        TokenRequest request = new TokenRequest(thisNode.getNodeInfo(), thisNode.getRnMap().get(thisNode.getNodeInfo()));
        sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), gson.toJson(request), PackageType.REQUEST_TOKEN, null));

    }

    private void sendCommPackageViaCommThread(CommPackage p){
        CommunicatorThread communicatorThread = new CommunicatorThread(thisNode, p);
        communicatorThread.start();
    }

    private String getParameterFromTokenizer(StringTokenizer tokenizer){
        if (tokenizer.hasMoreElements())
            return tokenizer.nextToken();
        return null;
    }
}
