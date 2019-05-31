package solution.peer.threads;

import solution.peer.Node;

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
            case ("connect"): {
                System.out.println("Komanda je start");
                CommunicatorThread communicatorThread = new CommunicatorThread(thisNode);
                communicatorThread.run();

                break;
            }
            case ("status"): {
                System.out.println("Komanda je stop");
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
