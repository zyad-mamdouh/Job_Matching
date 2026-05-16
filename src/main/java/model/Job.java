package model;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private final String title;
    private  int MinYearsRequired;
    private  int EducationLevel;
    private  double OfferedSalary;
    private final List<Skill> requiredSkills;

    public Job(String title, int MinYearsRequired,int EducationLevel , double OfferedSalary) {
        this.title = title;
        this.MinYearsRequired=MinYearsRequired;
        this.EducationLevel=EducationLevel;
        this.OfferedSalary=OfferedSalary;
        this.requiredSkills=new ArrayList<>();
    }
    public Job(String title) {
        this.title = title;
        this.requiredSkills=new ArrayList<>();
    }


    public void addRequiredSkill(Skill skill) {
        requiredSkills.add(skill);
    }

    public List<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public String getTitle() {
        return title;
    }

    public int getMinYearsRequired() {
return MinYearsRequired;
    }

    public int getEducationLevel() {
        return EducationLevel;
    }

    public double getOfferedSalary() {
        return OfferedSalary;
    }
}
