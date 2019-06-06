package solution.peer;

import com.google.gson.Gson;
import solution.configuration.NodeConfigModel;
import solution.n_queens.*;
import solution.peer.commPackage.Broadcast;
import solution.peer.commPackage.CommPackage;
import solution.peer.commPackage.PackageType;
import solution.peer.threads.CLIThread;
import solution.peer.threads.CommunicatorThread;
import solution.peer.threads.MainServerThread;
import solution.suzuki_kasami.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Node {

    private String configFilePath;
    private Gson gson;
    private NodeInfo nodeInfo;
    private NodeConfigModel configModel;
    private List<NodeInfo> visibleNodes;
    private NodeInfo successorNode;
    private Results results;
    private ArrayList<Broadcast> recievedBroadcasts;
    private NQueensSolver currentSolver;

    //suzukikasami
    private HashMap<NodeInfo, Integer> rnMap;
    private Token token;
    private boolean hasToken;

    public Node (String configFilePath){
        this.visibleNodes = Collections.synchronizedList(new ArrayList<NodeInfo>());
        this.gson = new Gson();
        this.configModel = readConfig(configFilePath);
        this.configFilePath = configFilePath;
        this.nodeInfo = new NodeInfo(configModel.getNodeGUID(), configModel.getNodePort(), configModel.getNodeAddress());
        this.results = new Results();
        this.recievedBroadcasts = new ArrayList<>();

        //suzuki
        rnMap = new HashMap<>();
        rnMap.put(this.getNodeInfo(), 0);
        token = null;
        hasToken = false;
        this.initialize();
    }

    public void giveToken(Token t){
        this.token = t;
        this.hasToken = true;
    }

    public Token takeToken(){
        if (!hasToken){
            System.err.println("I cant give the token since I dont have it");
            return null;
        }
        Token t = this.token;
        this.token = null;
        this.hasToken = false;
        return t;
    }


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

//    public void updateVisibleNodesBasedOnResultSet(ResultSet resultSet){
//        List<NodeInfo> list = Collections.synchronizedList(new ArrayList<NodeInfo>());
//        for (Job j : resultSet.getFingerTable().getKeySet()){
//            if (!list.contains(resultSet.getFingerTable().getTable().get(j))){
//                list.add(resultSet.getFingerTable().getTable().get(j));
//            }
//        }
//        System.out.println("Izracunao da mi je novi visiblenodes list:\n"+ list);
//    }

    public void updateVisibleNodes(ArrayList<NodeInfo> nodes){
        List<NodeInfo> newVisibleNodes = Collections.synchronizedList(new ArrayList<NodeInfo>());

        int count = 1;
        int x = 0;

        Collections.sort(nodes, Comparator.comparing(NodeInfo::getNodeGUID) );
        int myIndex = 0;
        while (nodes.get(myIndex).getNodeGUID() != getNodeInfo().getNodeGUID()){
            myIndex++;
        }
        //nasao svoj index
        int listSize = nodes.size();
        //ovaj algoritam treba da bude u updatevisiblenodes
        for (int i = 0; i < listSize - 1 ; i++){
            NodeInfo node = nodes.get(++myIndex % listSize);
            if (count++ == Math.pow(2, x)){
                System.out.println("Node "+ node.getNodeGUID() +" je od mene udaljen 2 na "+ ++x);
                newVisibleNodes.add(node);
            }
        }
        visibleNodes = newVisibleNodes;
    }

    public void updateFingerTableForResultSet(ResultSet resultSet){
        System.err.println("Updating fingertable for resultset");

        int n = resultSet.getN();

        if (!results.hasResultsFor(n)){
            System.err.println("nemam rezultate za to n");
            //da li?
            results.addResultSet(n, resultSet);
        }

        results.getResultSet(n).getFingerTable().updateFingerTable(visibleNodes, resultSet);
//        updateVisibleNodesBasedOnResultSet(results.getResultSet(n));
    }

    public void sendUpdateJobPackage(Job job){
        UpdateJobPackage jobPackage = new UpdateJobPackage(job.getMatrixSize(), job);
        new CommunicatorThread(this, new CommPackage(getNodeInfo(), gson.toJson(jobPackage), PackageType.UPDATE_RESULT_BROADCAST, null)).start();
    }

    public void initiateJobs(int count, int n){
        if (results.hasResultsFor(n)){
            System.err.println("Already have results for n, returning...");
            return;
        }
        //podelim poslove;

        //dodam poslove u resultset ako ih vec nema, ako ima obavestim
        ResultSet resultSet = generateJobs(count, n);
        Job myJob = resultSet.getFreeJob();
        resultSet.takeJob(myJob, this.getNodeInfo());

        if (results.hasResultsFor(n)){
            System.err.println("Vec imam rezultat za to n");
        } else {
            results.addResultSet(n, resultSet);
        }
        //zovem beginjob sto uzme jedan slobodan cvor iz rezultseta za dato n

        //saljem broadcast sa resultset-om, svako 'uzme' po jedan posao i updateuje message broadcasta (json)
        new CommunicatorThread(this, new CommPackage(this.getNodeInfo(), gson.toJson(resultSet), PackageType.START, null)).start();
        beginJob(myJob);

        //updatefingertable
    }

//    private Job getFreeJob(){}
    public Job stealJob(Job j){
        Job job = results.getResultSet(j.getMatrixSize()).getJob(j);

        if (job.getStatus() == JobStatus.COMPLETED){
            System.err.println("Cannot steal job, its completed");
        }
        if (job.getStatus() == JobStatus.LOCKED){
            System.err.println("Cannot steal job, its locked");
        }

        System.out.println("Delim posao: "+ job +"\nTrenutni progres = "+ job.getProgress());
        int jobEnd = job.getRangeEnd();
        int range = jobEnd - job.getRangeStart();
        int remainingRange = range/100*(100 - job.getProgress());
        int split = jobEnd-remainingRange/2;
        job.setRangeEnd(split);
        Job job2 = new Job(split, jobEnd, job.getMatrixSize());
        System.out.println("Nakon podele:");
        System.out.println(job);
        System.out.println(job2);

        return job2;
    }

    public void beginJob(Job job){

        NQueensSolver solver = new NQueensSolver();
        currentSolver = solver;
        solver.solve(job);
        System.out.println("Solver zavrsio? " + solver.isFinished());

        Result result = solver.getResult();
        //dodaj odma rezultat a ne kad je zavrseno racunanje
        job.setResult(result);
        job.setStatus(JobStatus.COMPLETED);
        //broadcastuj rezultat svima
        sendUpdateJobPackage(job);

        if (results.hasResultsFor(job.getMatrixSize())){
            results.getResultSet(job.getMatrixSize()).addJob(job);
        } else {
            System.err.println("Nemam taj resultset u koji da smestim rezultat koji sam izracunao [beginjob]");
        }
        System.out.println("Zavrsio racunanje");
    }

    private ResultSet generateJobs(int count, int n){
        ResultSet jobs = new ResultSet(this.getNodeInfo(), n);
        int start = 1;
        int range = (int)Math.pow(n, n)/count;
        for (int i = 1; i < count; i++){
            jobs.addJob(new Job(start, i * range - 1, n));
            start += range+1;
        }
        jobs.addJob(new Job(start, (int)Math.pow(n,n), n));
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

    public boolean recievedBroadcast(Broadcast b){
        if (recievedBroadcasts.contains(b)){
            return true;
        } else {
            return false;
        }
    }

    public void addToRecievedBroadcasts(Broadcast b){
        if (recievedBroadcasts.contains(b)){
            System.err.println("Already have that broadcast");
            return;
        }
        recievedBroadcasts.add(b);
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
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

    public int getCurrentSolverProgress(){
        return currentSolver.getProgress();
    }

    public HashMap<NodeInfo, Integer> getRnMap() {
        return rnMap;
    }

    public boolean hasToken() {
        return hasToken;
    }

    public Results getResults() {
        return results;
    }

    public void updateRNMap(HashMap<NodeInfo, Integer> newMap){
        System.out.println("RNMap before update "+ rnMap);
        System.out.println(newMap.keySet().iterator().next().getClass());
        for (NodeInfo nodeInfo : newMap.keySet()){
            rnMap.put(nodeInfo, newMap.get(nodeInfo));
        }
        System.out.println("RNMap after update "+ rnMap);
    }

    @Override
    public String toString() {
        return nodeInfo.toString();
    }
}
