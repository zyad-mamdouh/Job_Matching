package service;

import model.Applicant;
import model.Job;
import strategy.MatchingStrategy;

import java.util.List;
import java.util.Map;

public class MatchingEngine {

    private MatchingStrategy strategy;   // holds the strategy — just like ShoppingCart

    // Inject via constructor
    public MatchingEngine(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    // Swap at runtime — same as setPaymentStrategy()
    public void setStrategy(MatchingStrategy strategy) {
        this.strategy = strategy;
        System.out.println("Strategy → " + strategy.getClass().getSimpleName());
    }

    public void findBestMatch(List<Applicant> applicants, Job job) {
        System.out.println("\\nJob: " + job.getTitle());
        System.out.println("=".repeat(40));

        applicants.stream()
                .map(a -> Map.entry(a, strategy.calculate(a, job)))   // score each one
                .sorted((x, y) -> Double.compare(y.getValue(), x.getValue()))  // sort high→low
                .forEach(e -> System.out.printf(
                        "%-20s → Score: %.2f%n",
                        e.getKey().getName(), e.getValue()
                ));
    }
}
