package network;

import model.Applicant;
import model.Skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Client {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private final String host;
    private final int port;

    // Constructors
    public Client() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Connects to the server, sends applicant data and job selection,
    // then reads and returns the matching result.
    /*
    CLIENT                      SERVER

   out.println()  ───────►  in.readLine()

    in.readLine()  ◄───────  out.println()
    */
    public String sendApplicant(Applicant applicant, int jobNumber, String strategy) throws IOException {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_JOBS");

            StringBuilder response = new StringBuilder();
            response.append("Available Jobs:\n");
            readJobs(in, response);

            out.println(jobNumber);
            out.println(buildApplicantLine(applicant));
            out.println(buildSkillsLine(applicant));
            out.println(strategy);

            response.append("\nResult:\n");
            readResult(in, response);
            return response.toString();
        } catch (ConnectException e) {
            throw new IOException("Cannot connect. Is the server running?", e);
        }
    }

    private void readJobs(BufferedReader in, StringBuilder response) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("END_JOBS")) {
                return;
            }
            response.append(line).append(System.lineSeparator()); // append 1 line + new line According to os or system
        }
    }

    private void readResult(BufferedReader in, StringBuilder response) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append(System.lineSeparator());
        }
    }

    private String buildApplicantLine(Applicant applicant) {
        return applicant.getName() + ","
                + applicant.getYearsOfExperience() + ","
                + applicant.getEducationLevel() + ","
                + applicant.getExpectedSalary();
    }

    private String buildSkillsLine(Applicant applicant) {
        if (applicant.getSkills().isEmpty()) {
            return "none";
        }

        StringBuilder skillsLine = new StringBuilder();
        for (int i = 0; i < applicant.getSkills().size(); i++) {
            Skill skill = applicant.getSkills().get(i);
            skillsLine.append(skill.getName()).append(":").append(skill.getLevel());
            if (i < applicant.getSkills().size() - 1) {
                skillsLine.append(",");
            }
        }
        return skillsLine.toString();
    }
}
