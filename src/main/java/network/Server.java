package network;

import model.Applicant;
import model.Job;
import model.Skill;
import strategy.AdvancedMatchingStrategy;
import strategy.BasicMatchingStrategy;
import strategy.MatchingStrategy;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 5000;


    private static final List<Job> JOBS = new ArrayList<>();

    static {
        Job j1 = new Job("Software Engineer", 2, 2, 8000.0);
        j1.addRequiredSkill(new Skill("Java", 3));
        j1.addRequiredSkill(new Skill("Python", 2));
        j1.addRequiredSkill(new Skill("SQL", 1));
        JOBS.add(j1);

        Job j2 = new Job("Data Analyst", 1, 2, 6000.0);
        j2.addRequiredSkill(new Skill("Python", 2));
        j2.addRequiredSkill(new Skill("SQL", 2));
        j2.addRequiredSkill(new Skill("Excel", 1));
        JOBS.add(j2);

        Job j3 = new Job("Android Developer", 3, 2, 9000.0);
        j3.addRequiredSkill(new Skill("Java", 3));
        j3.addRequiredSkill(new Skill("Kotlin", 2));
        j3.addRequiredSkill(new Skill("XML", 1));
        JOBS.add(j3);

        Job j4 = new Job("Database Admin", 4, 3, 10000.0);
        j4.addRequiredSkill(new Skill("SQL", 3));
        j4.addRequiredSkill(new Skill("Oracle", 2));
        j4.addRequiredSkill(new Skill("Python", 1));
        JOBS.add(j4);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(" Server is running on port " + PORT);
            System.out.println(" Available jobs: " + JOBS.size());
            System.out.println(" Waiting for clients...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 Client connected: "
                        + clientSocket.getInetAddress().getHostAddress());

                new Thread(new ClientHandler(clientSocket, JOBS)).start();
            }

        } catch (IOException e) {
            System.out.println(" Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}

