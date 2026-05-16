package model;

public class Application {

    private final Applicant applicant;
    private final Job job;
    private double score;


    public Application(Applicant applicant, Job job) {
        this.applicant = applicant;
        this.job = job;
        this.score = 0.0;
    }


    public void setScore(double score) {
        this.score = score;
    }


    public double getScore() {
        return score;
    }


    public Applicant getApplicant() {
        return applicant;
    }

    public Job getJob() {
        return job;
    }
}