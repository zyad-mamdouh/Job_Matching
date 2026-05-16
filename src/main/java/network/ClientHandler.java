package network;

import model.Applicant;
import model.Application;
import model.Job;
import model.Skill;
import strategy.AdvancedMatchingStrategy;
import strategy.BasicMatchingStrategy;
import strategy.MatchingStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ClientHandler implements Runnable {

    private final Socket    socket;
    private final List<Job> jobs;

    public ClientHandler(Socket socket, List<Job> jobs) {
        this.socket = socket;
        this.jobs   = jobs;
    }

    @Override
    public void run() {
        try (
                BufferedReader in  = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter    out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {
            // Step 1: request jobs
            String request = in.readLine();
            if (!"GET_JOBS".equals(request)) {
                out.println("ERROR: Expected GET_JOBS");
                return;
            }

            // Step 2: send  jobs menu
            StringBuilder jobsList = new StringBuilder(); // same string but when we need edit it didnot create new obj
            for (int i = 0; i < jobs.size(); i++) {
                Job j = jobs.get(i);
                jobsList.append(i + 1).append(". ")
                        .append(j.getTitle())
                        .append(" | MinExp: ").append(j.getMinYearsRequired()).append("yrs")
                        .append(" | Edu: ").append(j.getEducationLevel())
                        .append(" | Salary: ").append(j.getOfferedSalary())
                        .append(" | Skills: ").append(getSkillNames(j))
                        .append("\n");
            }
            jobsList.append("END_JOBS");
            out.println(jobsList.toString());

            // Step 3: Receive client selection
            String choiceLine = in.readLine();
            int choice;
            try {
                choice = Integer.parseInt(choiceLine.trim());
            } catch (NumberFormatException e) {
                out.println("ERROR: Invalid job choice");
                return;
            }

            if (choice < 1 || choice > jobs.size()) {
                out.println("ERROR: Job number out of range");
                return;
            }
            Job selectedJob = jobs.get(choice - 1);

            // Step 4: Receive Applicant
            String applicantLine = in.readLine(); // NAME,YEARS,EDU,SALARY
            String skillsLine    = in.readLine(); // Java:3,Python:2
            String strategyLine  = in.readLine(); // basic or advanced

            if (applicantLine == null || skillsLine == null || strategyLine == null) {
                out.println("ERROR: Incomplete applicant data");
                return;
            }

            System.out.println("Job      : " + selectedJob.getTitle());
            System.out.println("Applicant: " + applicantLine);
            System.out.println("Skills   : " + skillsLine);
            System.out.println("Strategy : " + strategyLine);

            // Step 5: Parse
            Applicant applicant = parseApplicant(applicantLine, skillsLine);
            if (applicant == null) {
                out.println("ERROR: Invalid applicant data format");
                return;
            }

            // Step 6: Strategy
            MatchingStrategy strategy = strategyLine.equalsIgnoreCase("advanced")
                    ? new AdvancedMatchingStrategy()
                    : new BasicMatchingStrategy();

            // Step 7: Score using Application object
            Application application = new Application(applicant, selectedJob);
            application.setScore(strategy.calculate(applicant, selectedJob));
            double score = application.getScore();

            // Step 8: result
            out.println(buildResult(applicant, selectedJob, score, strategyLine));
            System.out.println("Response sent | Score: "
                    + String.format("%.2f", score) + "%\n");

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private String buildResult(Applicant a, Job j, double score, String strategy) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("Job      : ").append(j.getTitle()).append("\n");
        sb.append("Applicant: ").append(a.getName()).append("\n");
        sb.append("Strategy : ").append(strategy).append("\n");
        sb.append("Score    : ").append(String.format("%.2f%%", score)).append("\n");
        sb.append("------------------------------------\n");
        if      (score >= 80) sb.append("Status   :  HIGHLY RECOMMENDED\n");
        else if (score >= 50) sb.append("Status   :  POSSIBLE MATCH\n");
        else                  sb.append("Status   :  NOT RECOMMENDED\n");
        sb.append("====================================");
        return sb.toString();
    }

    private Applicant parseApplicant(String applicantLine, String skillsLine) {
        try {
            String[] parts = applicantLine.split(",");
            if (parts.length < 4) return null;

            Applicant applicant = new Applicant(
                    parts[0].trim(),
                    Integer.parseInt(parts[1].trim()),
                    Integer.parseInt(parts[2].trim()),
                    Double.parseDouble(parts[3].trim())
            );

            if (!skillsLine.equalsIgnoreCase("none")) {
                for (String sp : skillsLine.split(",")) {
                    String[] kv = sp.trim().split(":");
                    if (kv.length == 2)
                        applicant.addSkill(new Skill(
                                kv[0].trim(), Integer.parseInt(kv[1].trim())));
                }
            }
            return applicant;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getSkillNames(Job j) {
        StringBuilder sb = new StringBuilder();
        List<Skill> skills = j.getRequiredSkills();
        for (int i = 0; i < skills.size(); i++) {
            sb.append(skills.get(i).getName());
            if (i < skills.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}