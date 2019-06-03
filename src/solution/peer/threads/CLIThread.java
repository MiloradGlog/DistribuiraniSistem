package solution.peer.threads;

import com.google.gson.Gson;
import solution.peer.Node;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;

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
            case ("join"): {
                System.out.println("joining...");
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), "", PackageType.BOOTSTRAP_JOIN, null));
                break;
            }
            case ("leave"): {
                System.out.println("leaving...");
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), "", PackageType.BOOTSTRAP_LEAVE, null));
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), gson.toJson(thisNode.getSuccessorNode()), PackageType.BROADCAST_LEAVE, null));
                break;
            }case ("broadcast"): {
                System.out.println("Attempting broadcast...");
                sendCommPackageViaCommThread(new CommPackage(thisNode.getNodeInfo(), parameterString, PackageType.BROADCAST, null));
                break;
            }
            case ("status"): {
                System.out.println("Cvor: "+ thisNode.getNodeInfo().getNodeGUID());
                System.out.println("Naslednik mu je: "+ thisNode.getSuccessorNode().getNodeGUID());
                System.out.println("Vidi cvorove: \n"+ thisNode.getVisibleNodes());
                break;
            }
            default: {
                System.out.println("Nepoznata komanda");
                break;
            }

        }
    }

    private void sendCommPackageViaCommThread(CommPackage p){
        CommunicatorThread communicatorThread = new CommunicatorThread(thisNode, p);
        communicatorThread.run();
    }

    private String getParameterFromTokenizer(StringTokenizer tokenizer){
        if (tokenizer.hasMoreElements())
            return tokenizer.nextToken();
        return null;
    }
}
