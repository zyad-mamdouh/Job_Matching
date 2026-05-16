package strategy;

import model.Applicant;
import model.Job;
import model.Skill;

import java.util.List;

public class AdvancedMatchingStrategy implements MatchingStrategy {

    private static final double SKILLS_WEIGHT     = 0.50;
    private static final double EXPERIENCE_WEIGHT = 0.25;
    private static final double EDUCATION_WEIGHT  = 0.15;
    private static final double SALARY_WEIGHT     = 0.10;

    @Override
    public double calculate(Applicant applicant, Job job) {
        double skills     = calculateSkillsScore(applicant, job);
        double experience = calculateExperienceScore(applicant, job);
        double education  = calculateEducationScore(applicant, job);
        double salary     = calculateSalaryScore(applicant, job);

        return (skills     * SKILLS_WEIGHT)
                + (experience * EXPERIENCE_WEIGHT)
                + (education  * EDUCATION_WEIGHT)
                + (salary     * SALARY_WEIGHT);
    }
    private double calculateSkillsScore(Applicant a, Job j) {
        List<Skill> required = j.getRequiredSkills();
        if (required.isEmpty()) return 100.0;

        long matched = a.getSkills().stream()
                .map(s -> s.getName().toLowerCase())
                .filter(name ->
                        required.stream()
                                .map(r -> r.getName().toLowerCase())
                                .anyMatch(rn -> rn.equals(name))
                )
                .count();

        return ((double) matched / required.size()) * 100.0;
    }
    private double calculateExperienceScore(Applicant a, Job j) {
        int applicantYears = a.getYearsOfExperience();
        int requiredYears  = j.getMinYearsRequired();

        if (requiredYears == 0) return 100.0;

        double ratio = (double) applicantYears / requiredYears;

        return Math.min(ratio, 1.0) * 100.0;
    }

    private double calculateEducationScore(Applicant a, Job j) {
        int applicantLevel = a.getEducationLevel(); // e.g. 1=HighSchool, 2=Bachelor, 3=Master
        int requiredLevel  = j.getEducationLevel();

        if (applicantLevel >= requiredLevel) return 100.0;

        return ((double) applicantLevel / requiredLevel) * 100.0;
    }

    private double calculateSalaryScore(Applicant a, Job j) {
        double expected = a.getExpectedSalary();
        double offered  = j.getOfferedSalary();

        if (expected <= offered) return 100.0;

        double ratio = offered / expected;

        return Math.max(0.0, ratio * 100.0);
    }
}
