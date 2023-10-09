package DifferentialPrivacy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateData {
    
    static double epsilon;
    static double beta; 
    static double upperBound;
    static double queryResult = 0.0; 
    static String inputPath ;
    static String outputPath;

    public static void main(String[] args) {
        inputPath = args[3];
        outputPath = args[4];
        Map<Integer, Double> userAggregateMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputPath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                double count = Double.parseDouble(parts[0]);
                int userID = Integer.parseInt(parts[1]);
                userAggregateMap.put(userID, userAggregateMap.getOrDefault(userID, 0.0) + count);
                queryResult += count;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        epsilon = Double.parseDouble(args[0]);
        beta = Double.parseDouble(args[1]);
        upperBound = Double.parseDouble(args[2]);
        getValue(userAggregateMap);
    }

    public static void getValue(Map<Integer, Double> userAggregateMap) {
        int tau = (int) Math.ceil(2 / epsilon * Math.log((upperBound + 1.0 / 1.0) / beta)); 

        List<Double> aggregationValues = new ArrayList<>(userAggregateMap.values());
        Collections.sort(aggregationValues);

        try (PrintWriter outputWriter = new PrintWriter(new FileWriter(outputPath))) {
            outputWriter.println(upperBound);
            outputWriter.println(queryResult);
            
            for (int j = 1; j <= 2 * tau; j++) {
                double value = 0;
                for (int k = 1; k <= j; k++) {
                    value += aggregationValues.get(aggregationValues.size() - k);
                }
                outputWriter.println(queryResult - value);
            }
            outputWriter.println("0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

