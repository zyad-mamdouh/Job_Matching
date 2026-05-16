package service;

import model.Applicant;
import model.Skill;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResumeParser {

    public Applicant parse(Path resumePath) throws IOException {
        List<String> lines = Files.readAllLines(resumePath, StandardCharsets.UTF_8);

        String name = null;
        Integer yearsOfExperience = null;
        Integer educationLevel = null;
        Double expectedSalary = null;
        Applicant applicant = null;
        boolean readingSkills = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;
            }

            if (trimmedLine.equalsIgnoreCase("Skills:")) {
                readingSkills = true;
                continue;
            }

            if (readingSkills) {
                if (applicant == null) {
                    applicant = createApplicant(name, yearsOfExperience, educationLevel, expectedSalary);
                }
                applicant.addSkill(parseSkill(trimmedLine));
                continue;
            }

            String[] parts = trimmedLine.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid line: " + trimmedLine);
            }

            String key = parts[0].trim();
            String value = parts[1].trim();

            if (key.equalsIgnoreCase("Name")) {
                name = value;
            } else if (key.equalsIgnoreCase("YearsOfExperience")) {
                yearsOfExperience = parseInteger(value, "YearsOfExperience");
            } else if (key.equalsIgnoreCase("EducationLevel")) {
                educationLevel = parseInteger(value, "EducationLevel");
            } else if (key.equalsIgnoreCase("ExpectedSalary")) {
                expectedSalary = parseDouble(value, "ExpectedSalary");
            } else {
                throw new IllegalArgumentException("Unknown field: " + key);
            }
        }

        if (applicant == null) {
            applicant = createApplicant(name, yearsOfExperience, educationLevel, expectedSalary);
        }

        return applicant;
    }

    private Applicant createApplicant(
            String name,
            Integer yearsOfExperience,
            Integer educationLevel,
            Double expectedSalary
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (yearsOfExperience == null) {
            throw new IllegalArgumentException("YearsOfExperience is required.");
        }
        if (educationLevel == null) {
            throw new IllegalArgumentException("EducationLevel is required.");
        }
        if (expectedSalary == null) {
            throw new IllegalArgumentException("ExpectedSalary is required.");
        }
        if (educationLevel < 1 || educationLevel > 3) {
            throw new IllegalArgumentException("EducationLevel must be 1, 2, or 3.");
        }

        return new Applicant(name, yearsOfExperience, educationLevel, expectedSalary);
    }

    private Skill parseSkill(String line) {
        String[] parts = line.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid skill line: " + line);
        }

        String skillName = parts[0].trim();
        int skillLevel = parseInteger(parts[1].trim(), "skill level");

        if (skillName.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be empty.");
        }

        return new Skill(skillName, skillLevel);
    }

    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    private double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }
}
