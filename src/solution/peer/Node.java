package solution.peer;

import com.google.gson.Gson;
import solution.configuration.NodeConfigModel;
import solution.n_queens.*;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;
import solution.peer.threads.CLIThread;
import solution.peer.threads.CommunicatorThread;
import solution.peer.threads.MainServerThread;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {

    private String configFilePath;
    private Gson gson;
    private NodeInfo nodeInfo;
    private NodeConfigModel configModel;
    private List<NodeInfo> visibleNodes;
    private NodeInfo successorNode;
    private FingerTable fingerTable;
    private Results results;

    public Node (String configFilePath){
        this.visibleNodes = Collections.synchronizedList(new ArrayList<NodeInfo>());
        this.gson = new Gson();
        this.configModel = readConfig(configFilePath);
        this.configFilePath = configFilePath;
        this.nodeInfo = new NodeInfo(configModel.getNodeGUID(), configModel.getNodePort(), configModel.getNodeAddress());
        this.fingerTable = new FingerTable();
        this.results = new Results();
        this.initialize();
    }
/*
    public Node(int nodeGUID, int nodePort, String nodeAddress, String configFilePath) {
        this.gson = new Gson();
        this.configFilePath = configFilePath;
        this.nodeGUID = nodeGUID;
        this.nodePort = nodePort;
        this.nodeAddress = nodeAddress;
        this.initialize();
    }
*/
    private void initialize(){
        try {
            System.out.println("Initializing node: "+ this.toString());

            CLIThread cliThread = new CLIThread(this);
            MainServerThread serverThread = new MainServerThread(this, false);

            cliThread.start();
            serverThread.start();

            joinBootstrap();

        } catch (IOException ioException) {
            System.err.println("Error when initializing Node: "+ nodeInfo.getNodeGUID() + "\nStackTrace:");
            ioException.printStackTrace();
        }

    }

    public void initiateJobs(int count, int n){
        //podelim poslove;
        ArrayList<Job> jobs = generateJobs(count, n);


        //saljem broadcast sa poslovima, svako 'uzme' po jedan posao i updateuje message broadcasta (json)
        Job myJob = jobs.get(0);
        jobs.remove(myJob);
        new CommunicatorThread(this, new CommPackage(this.getNodeInfo(), gson.toJson(jobs), PackageType.START, null)).run();
        beginJob(myJob);

        //updatefingertable
    }

    public void beginJob(Job job){
        NQueensSolver solver = new NQueensSolver();
        solver.solve(job.getRangeStart(), job.getRangeEnd(), job.getMatrixSize());
        System.out.println("Solver zavrsio? " + solver.isFinished());
        Result result = solver.getResult();

        job.setResult(result);
        job.setStatus(JobStatus.COMPLETED);

        if (results.hasResultsFor(job.getMatrixSize())){
            results.getResultSet(job.getMatrixSize()).addJob(job);
        } else {
            results.addResultSet(job.getMatrixSize(), new ResultSet());
            results.getResultSet(job.getMatrixSize()).addJob(job);
        }
        System.out.println("Zavrsio racunanje");
    }

    private ArrayList<Job> generateJobs(int count, int n){
        ArrayList<Job> jobs = new ArrayList<>();
        int start = 1;
        int range = (int)Math.pow(n, n)/count;
        for (int i = 1; i < count; i++){
            jobs.add(new Job(start, i * range - 1, n));
            start += range+1;
        }
        jobs.add(new Job(start, (int)Math.pow(n,n), n));
        return jobs;
    }

    public void joinBootstrap(){
        CommPackage p = new CommPackage(getNodeInfo(), "", PackageType.BOOTSTRAP_JOIN, null);
        CommunicatorThread communicatorThread = new CommunicatorThread(this, p);
        communicatorThread.run();
    }

    private NodeConfigModel readConfig(String path){
        StringBuilder sb = new StringBuilder();
        try {
            for (String s : Files.readAllLines(Paths.get(path))){
                sb.append(s);
            }
        } catch (IOException e){
            System.err.println("Error reading the configuration file\nStackTrace:");
            e.printStackTrace();
        }
        NodeConfigModel model = gson.fromJson(sb.toString(), NodeConfigModel.class);
        return model;
    }

    public void saveConfig(){
        try (PrintWriter out = new PrintWriter("files/config.json")) {
            out.println(gson.toJson(configModel));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setSuccessorNode(NodeInfo nodeInfo){
        synchronized (visibleNodes){
            if (!hasVisibleNode(nodeInfo)) {
                addVisibleNode(nodeInfo);
            }
            if (successorNode != null){
                for (NodeInfo n : visibleNodes){
                    if (n.getNodeGUID() == successorNode.getNodeGUID()){
                        visibleNodes.remove(n);
                    }
                }
            }
        }
        successorNode = nodeInfo;
    }

    public boolean addVisibleNode(NodeInfo nodeInfo){
        if (hasVisibleNode(nodeInfo)){
            System.err.println("Already has that node in visiblenodes");
            return false;
        }
        visibleNodes.add(nodeInfo);
        System.out.println("Dodao cvor "+ nodeInfo.getNodeGUID() + " u visible nodes.");
        return true;
    }

    public boolean removeVisibleNode(NodeInfo nodeInfo){
        for (NodeInfo n : visibleNodes){
            if (n.getNodeGUID() == nodeInfo.getNodeGUID()){
                visibleNodes.remove(n);
                return true;
            }
        }
        return false;
    }

    public NodeInfo getLargestGUIDVisibleNode(){
        if (visibleNodes.isEmpty()){
            System.err.println("Error in getLargestGUIDVisibleNodeLesserThan, visibleNodes is empty, returning null...\n");
            return null;
        }
        NodeInfo max = visibleNodes.get(0);
        for (NodeInfo node : visibleNodes){
            if (node.getNodeGUID() > max.getNodeGUID()){
                max = node;
            }
        }
        return max;
    }

    public NodeInfo getLargestGUIDVisibleNodeLesserThan(int n){
        if (visibleNodes.isEmpty()){
            System.err.println("Error in getLargestGUIDVisibleNodeLesserThan, visibleNodes is empty, returning null...\n");
            return null;
        }
        NodeInfo max = visibleNodes.get(0);
        for (NodeInfo node : visibleNodes){
            if (node.getNodeGUID() > max.getNodeGUID()){
                if (node.getNodeGUID() < n){
                    max = node;
                }
            }
        }
        return max;
    }

    public NodeInfo getLargestGUIDVisibleNodeLesserThanIncludingThisNode(int n){
        if (visibleNodes.isEmpty()){
            System.err.println("Error in getLargestGUIDVisibleNodeLesserThanIncludingThisNode, visibleNodes is empty, returning null...\n");
            return null;
        }
        NodeInfo max = nodeInfo;
        for (NodeInfo node : visibleNodes){
            if (node.getNodeGUID() > max.getNodeGUID()){
                if (node.getNodeGUID() < n){
                    max = node;
                }
            }
        }
        return max;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public NodeConfigModel getConfigModel() {
        return configModel;
    }

    public NodeInfo getSuccessorNode() {
        return successorNode;
    }

    public List<NodeInfo> getVisibleNodes() {
        return visibleNodes;
    }

    private boolean hasVisibleNode(NodeInfo nodeInfo){
        for (NodeInfo n : visibleNodes){
            if (n.getNodeGUID() == nodeInfo.getNodeGUID()){
                return true;
            }
        }
        return false;
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }

    public Results getResults() {
        return results;
    }

    @Override
    public String toString() {
        return nodeInfo.toString();
    }
}
