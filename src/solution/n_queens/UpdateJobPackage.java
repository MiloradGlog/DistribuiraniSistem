package solution.n_queens;

public class UpdateJobPackage {

    private int n;
    private Job job;

    public UpdateJobPackage(int n, Job job){
        this.n = n;
        this.job = job;
    }

    public int getN() {
        return n;
    }

    public Job getJob() {
        return job;
    }

    @Override
    public String toString() {
        return "UpdateJobPackage{" +
                "n=" + n +
                ", job=" + job +
                '}';
    }
}
