package strategy;

import model.Applicant;
import model.Job;

public interface MatchingStrategy {

    double calculate(Applicant applicant, Job job);
}
