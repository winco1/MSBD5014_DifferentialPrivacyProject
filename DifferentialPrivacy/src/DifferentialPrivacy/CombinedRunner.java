package DifferentialPrivacy;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CombinedRunner {

    public static void main(String[] args) {
        double[] epsilons = {0.125, 0.25, 0.5, 1.0};
        double beta = 0.1;
        double upperBound = 100000000;
        
        String databaseName = "tpch_4g";
        String outputFilePath = "C:\\Users\\86184\\Desktop\\info\\output_4g.txt";
        String query = "select 1, o_orderkey from orders, lineitem where o_orderkey = l_orderkey";
        String outputPathTemplate = "C:\\Users\\86184\\Desktop\\sum\\4g\\sum_epsilon_%.3f.txt";
        String finalOutputPath = "C:\\Users\\86184\\Desktop\\result\\4g\\all_results.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(finalOutputPath, true))) {
            for (double epsilon : epsilons) {
                long startTime = System.currentTimeMillis();

                runJavaProgram("DifferentialPrivacy.queryResult", databaseName, outputFilePath, query);

                String outputPath = String.format(outputPathTemplate, epsilon);
                executeCommandWithClasspath("DifferentialPrivacy.AggregateData", epsilon, beta, upperBound, outputFilePath, outputPath);

                String exponentialResult = executeCommandWithClasspathAndCaptureOutput("DifferentialPrivacy.Exponential", epsilon, beta, upperBound, outputPath);

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;

                writer.println("Epsilon: " + epsilon + ", Total Time: " + totalTime + " ms, Exponential Result: " + exponentialResult);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runJavaProgram(String className, Object... args) {
        List<String> command = buildCommandList(className, args);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void executeCommandWithClasspath(String className, Object... args) {
        List<String> command = buildCommandList(className, args);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String executeCommandWithClasspathAndCaptureOutput(String className, Object... args) {
        List<String> command = buildCommandList(className, args);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        StringBuilder output = new StringBuilder();

        try {
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString().trim();
    }

    private static List<String> buildCommandList(String className, Object... args) {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-cp");
        command.add("C:\\Users\\86184\\eclipse-workspace\\DifferentialPrivacy\\bin");
        command.add(className);
        for (Object arg : args) {
            command.add(arg.toString());
        }
        return command;
    }
}
