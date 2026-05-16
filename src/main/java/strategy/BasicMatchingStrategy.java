package strategy;

import model.Applicant;
import model.Job;
import model.Skill;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicMatchingStrategy implements MatchingStrategy{

    @Override
    public double calculate(Applicant applicant, Job job) {

        List<Skill> required = job.getRequiredSkills();
        if (required.isEmpty()) return 100.0;

        Set<String> requiredNames =required.stream().map(r-> r.getName().toLowerCase()).collect(Collectors.toSet());
        long matched =applicant.getSkills().stream().map(r-> r.getName().toLowerCase()).filter(requiredNames::contains).count();

        return ((double) matched / required.size()) * 100.0;
    }
}
