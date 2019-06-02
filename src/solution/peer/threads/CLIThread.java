package solution.peer.threads;

import solution.peer.Node;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;

import java.util.Scanner;
import java.util.StringTokenizer;

public class CLIThread extends Thread {

    private Scanner in;
    private Node thisNode;

    public CLIThread(Node node){
        this.thisNode = node;
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
                System.out.println("Komanda je join");
                CommPackage p = new CommPackage(thisNode.getNodeInfo(), "", PackageType.BOOTSTRAP_JOIN, null);
                CommunicatorThread communicatorThread = new CommunicatorThread(thisNode, p);
                communicatorThread.run();
                break;
            }
            case ("leave"): {
                System.out.println("Komanda je leave");
                CommPackage p = new CommPackage(thisNode.getNodeInfo(), "", PackageType.BOOTSTRAP_LEAVE, null);
                CommunicatorThread communicatorThread = new CommunicatorThread(thisNode, p);
                communicatorThread.run();
                break;
            }/*
            case ("ping"): {
                System.out.println("Komanda je ping");
                int targetPort = Integer.parseInt(parameterString);
                CommPackage p = new CommPackage(thisNode.getNodeInfo(), "testporuka", PackageType.PING, targetPort);
                CommunicatorThread communicatorThread = new CommunicatorThread(thisNode, p);
                communicatorThread.run();

                break;
            }*/
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

    private String getParameterFromTokenizer(StringTokenizer tokenizer){
        if (tokenizer.hasMoreElements())
            return tokenizer.nextToken();
        return null;
    }
}
