package DifferentialPrivacy;

import java.io.*;
import java.util.*;

public class Exponential {
    private static String inputPath = "C:\\Users\\86184\\Desktop\\sum\\query_result.txt";
    private static double epsilon;
    private static double beta;
    private static double upperBound;
    private static double errorLevel = 1.0;
    private static double output;
    private static double queryResult;
    private static List<Double> possibleR = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        epsilon = Double.parseDouble(args[0]);
        beta = Double.parseDouble(args[1]);
        upperBound = Double.parseDouble(args[2]);
        inputPath = args[3];
        long startTime = System.currentTimeMillis();
        readInput();
        runAlgorithm();
        System.out.println(" Output: " + output + " QueryResult: " + queryResult);
        long endTime = System.currentTimeMillis();
        long runningTime = endTime - startTime;

        double relativeError = Math.abs((output - queryResult) / queryResult) * 100;
        System.out.println("Running Time: " + runningTime + "ms");
        System.out.println("Relative Error: " + relativeError + "%");

    }
    

    private static void readInput() throws IOException {
        File file = new File(inputPath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int i = 0;
        while ((line = br.readLine()) != null) {
            if (i == 1) queryResult = Double.parseDouble(line);
            possibleR.add(Math.ceil(Double.parseDouble(line) / errorLevel) * errorLevel);
            i++;
        }
        br.close();
    }

    private static void runAlgorithm() {
        int tau = (int) Math.ceil(2 / epsilon * Math.log((upperBound + 1.0 / 1.0) / beta));
        List<Double> left = possibleR.subList(0, tau);
        double mid = possibleR.get(tau + 1);
        List<Double> right = possibleR.subList(tau + 1, possibleR.size());

        List<Double> leftDifferences = getDifferences(left);
        List<Double> rightDifferences = getDifferences(right);

        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < leftDifferences.size(); i++)
            weights.add(Math.exp(epsilon / 2 * (tau + i - 1)) * leftDifferences.get(i));
        weights.add(Math.exp(0.0));
        for (int i = 0; i < rightDifferences.size(); i++)
            weights.add(Math.exp(epsilon / 2 * (tau - i)) * rightDifferences.get(i));

        int selectedIdx = selectIndex(weights);
        if (selectedIdx < tau) {
            output = left.get(selectedIdx);
        } else if (selectedIdx == tau) {
            output = mid;
        } else {
            output = right.get(selectedIdx);
        }
    }

    private static List<Double> getDifferences(List<Double> list) {
        List<Double> differences = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) differences.add(list.get(i) - list.get(i + 1));
        return differences;
    }

    private static int selectIndex(List<Double> weights) {
        double sum = weights.stream().mapToDouble(Double::doubleValue).sum();
        double r = Math.random() * sum;
        double count = 0;
        for (int i = 0; i < weights.size(); i++) {
            count += weights.get(i);
            if (count >= r) return i;
        }
        return weights.size() - 1;
    }
}
