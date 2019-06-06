package solution.n_queens;

import solution.peer.NodeInfo;

public class Job {

    private String jobID;
    private int rangeStart;
    private int rangeEnd;
    private int matrixSize;
    private JobStatus status;
    private Result result;
    private NodeInfo assignedTo;
    private boolean taken;
    private int progress;

    public Job(int rangeStart, int rangeEnd, int matrixSize) {
        jobID = Integer.toString(rangeStart) + System.currentTimeMillis() + rangeEnd;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.matrixSize = matrixSize;
        this.status = JobStatus.STARTED;
        this.assignedTo = null;
        this.taken = false;
        this.progress = 0;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(int rangeStart) {
        this.rangeStart = rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getMatrixSize() {
        return matrixSize;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public NodeInfo getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(NodeInfo assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getJobID() {
        return jobID;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Job){
            if (((Job)obj).getJobID().equals(jobID)){
                return true;
            }
            else{
                return false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Job{id=").append(jobID);
        sb.append(", rangeStart=").append(rangeStart);
        sb.append(", rangeEnd=").append(rangeEnd);
        sb.append(", status=").append(status);
        sb.append(", assignedTo= ").append(assignedTo);
        sb.append("}\n");
        if (result != null){
            sb.append(result.getResultString(matrixSize));
        }
        return  sb.toString();
    }
}
