package solution.n_queens;

import solution.peer.NodeInfo;

import java.util.HashSet;

public class ResultSet {

    private HashSet<Job> jobs;
    private FingerTable fingerTable;
    private int n;

    public ResultSet(ResultSet resultSet, NodeInfo thisNode){
        this.jobs = resultSet.getJobs();
        this.n = resultSet.getN();
        fingerTable = new FingerTable(thisNode);
    }

    public ResultSet(NodeInfo thisNode, int n) {
        this.jobs = new HashSet<>();
        this.n = n;
        fingerTable = new FingerTable(thisNode);
    }

    public boolean resultsCompleted(){
        for (Job job : jobs){
            if (job.getStatus() != JobStatus.COMPLETED)
                return false;
        }
        return true;
    }

    public void addJob(Job job){
        if (jobs.contains(job)){
            System.err.println("already have that job, updating instead [addjob, resultset]");
            updateJob(job);
            return;
        }
        jobs.add(job);
    }

    public Job getRandomUnfinishedJob(NodeInfo thisNode){
        for (Job j : jobs){
            if (j.isTaken() && j.getStatus() == JobStatus.STARTED && j.getAssignedTo().getNodeGUID() != thisNode.getNodeGUID()){
                return j;
            }
        }
        System.err.println("nema nezavrsenih cvorova");
        return null;
    }

    public void updateResultSet(ResultSet resultSet){
        for (Job j : resultSet.getJobs()){
            updateJob(j);
        }
    }

    public void updateJob(Job j){
        for (Job job : jobs){
            if (job.equals(j)){
                System.out.println("updated job "+ j);
                job.setStatus(j.getStatus());
                job.setRangeStart(j.getRangeStart());
                job.setRangeEnd(j.getRangeEnd());
                job.setTaken(j.isTaken());
                job.setAssignedTo(j.getAssignedTo());
                job.setResult(j.getResult());
            }
        }
    }

    public Job getJob(Job j){
        for (Job job : jobs){
            if (j.equals(job)){
                return job;
            }
        }
        System.err.println("Nisam nasao taj job u getJob");
        return null;
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }

    public boolean hasFreeJobs(){
        for (Job j : jobs){
            if (!j.isTaken()){
                return true;
            }
        }
        return false;
    }

    public Job getFreeJob(){
        for (Job j : jobs){
            if (!j.isTaken()){
                return j;
            }
        }
        System.err.println("Nisam nasao prazan job, vracam null");
        return null;
    }

    public void takeJob(Job j, NodeInfo nodeInfo){
        for (Job job : jobs){
            if (j.equals(job)){
                j.setTaken(true);
                j.setAssignedTo(nodeInfo);
                System.out.println("Sucessfuly taken job "+ j);
                return;
            }
        }
        System.err.println("Failed to take job");
    }

    public HashSet<Job> getJobs() {
        return jobs;
    }

    public int getN() {
        return n;
    }

    @Override
    public String toString() {
        return "ResultSet{" +
                "jobs=" + jobs +
                '}';
    }
}
