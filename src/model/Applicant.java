package model;

import java.util.ArrayList;
import java.util.List;

public class Applicant {
    private final String name;
    private final List<Skill> skills;
    private  int YearsOfExperience;
    private  int EducationLevel;
    private  double ExpectedSalary;

    public Applicant(String name, int YearsOfExperience ,int EducationLevel,double ExpectedSalary) {
        this.name = name;
        this.YearsOfExperience=YearsOfExperience;
        this.EducationLevel=EducationLevel;
        this.ExpectedSalary=ExpectedSalary;
        this.skills=new ArrayList<>();
    }
    public Applicant(String name) {
        this.name = name;
        this.skills=new ArrayList<>();
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public String getName() {
        return name;
    }

    public int getYearsOfExperience() {
        return YearsOfExperience;
    }

    public int getEducationLevel() {
        return EducationLevel;
    }

    public double getExpectedSalary() {
        return ExpectedSalary;
    }
}
