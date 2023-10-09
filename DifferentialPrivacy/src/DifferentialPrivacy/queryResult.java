package DifferentialPrivacy;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class queryResult {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java queryResult <databaseName> <outputFilePath> <query>");
            System.exit(1);
        }

        String dbName = args[0];
        String outputPath = args[1];
        String sqlQuery = args[2];

        try {
            retrieveData(dbName, outputPath, sqlQuery);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void retrieveData(String dbName, String outputPath, String sqlQuery) throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + dbName, "root", "root");// connect to the database
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sqlQuery); // execute query

        HashMap<Integer, Integer> idMapping = new HashMap<>();
        int identifier = 0;

        FileWriter fileWriter = new FileWriter(outputPath);

        while (resultSet.next()) {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(resultSet.getFloat(1)).append(" ");
            
            int userID = resultSet.getInt(2); 

            if (idMapping.containsKey(userID)) {
                strBuilder.append(idMapping.get(userID)).append(" ");
            } else {
                idMapping.put(userID, identifier);
                strBuilder.append(identifier).append(" ");
                identifier++;
            }

            fileWriter.write(strBuilder.toString() + "\n");
        }

        fileWriter.close();
        connection.close();
    }
}
