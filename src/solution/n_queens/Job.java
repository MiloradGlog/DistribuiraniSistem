package solution.n_queens;

public class Job {

    private int rangeStart;
    private int rangeEnd;
    private int matrixSize;
    private JobStatus status;
    private Result result;

    public Job(int rangeStart, int rangeEnd, int matrixSize) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.matrixSize = matrixSize;
        this.status = JobStatus.STARTED;
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

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Job{");
        sb.append("rangeStart=").append(rangeStart);
        sb.append(", rangeEnd=").append(rangeEnd);
        sb.append(", status=").append(status);
        sb.append("}\n");
        if (result != null){
            sb.append(result.getResultString(matrixSize));
        }
        return  sb.toString();
    }
}
