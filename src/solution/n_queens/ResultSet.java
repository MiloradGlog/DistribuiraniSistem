package solution.n_queens;

import java.util.HashSet;

public class ResultSet {

    private HashSet<Job> jobs;

    public ResultSet() {
        this.jobs = new HashSet<>();
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
            System.err.println("already have that job");
            return;
        }
        jobs.add(job);
    }

    public HashSet<Job> getJobs() {
        return jobs;
    }

    @Override
    public String toString() {
        return "ResultSet{" +
                "jobs=" + jobs +
                '}';
    }
}
