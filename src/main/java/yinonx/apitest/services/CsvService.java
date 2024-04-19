package yinonx.apitest.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.apache.commons.lang3.tuple.Pair;
import yinonx.apitest.classes.Game;

@Service
public class CsvService {
    private double[][] matrix;
    // private String csvFilePath = "src\\main\\java\\yinonx\\apitest\\CSV's\\movie
    // _ratings.csv";
    private String csvFilePath = "src\\main\\java\\yinonx\\apitest\\CSV's\\Modified.csv";
    private int numRowsToProcess = 500;
    private int numColsToProcess = 500; // Default value, -1 means process all columns
    private String filePath = "src\\main\\java\\yinonx\\apitest\\CSV's\\predictedMatrix.csv";
    private String predictionCsvFilePath = "src\\main\\java\\yinonx\\apitest\\CSV's\\predictedMatrix.csv";
    public CsvService() {
    }

    public CsvService(int numRowsToProcess, int numColsToProcess) {
        this.numRowsToProcess = numRowsToProcess;
        this.numColsToProcess = numColsToProcess;
    }

    private void buildMatrix(List<String[]> data) {
        int numRows = Math.min(data.size() - 1, numRowsToProcess); // Ignore the first row

        // Ensure there is at least one row of data
        if (numRows == 0) {
            throw new IllegalArgumentException("Empty data list");
        }

        int numCols = (numColsToProcess == -1) ? data.get(0).length - 1
                : Math.min(data.get(0).length - 1, numColsToProcess);

        matrix = new double[numRows][numCols];

        for (int i = 1; i <= numRows; i++) { // Start from index 1 to ignore the first row
            for (int j = 1; j <= numCols; j++) { // Start from index 1 to ignore the first column
                String value = data.get(i)[j].trim();

                try {
                    matrix[i - 1][j - 1] = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    // Handle non-numeric values (you might want to log or take appropriate action)
                    matrix[i - 1][j - 1] = 0.0; // Default to 0.0 for non-numeric values
                }
            }
        }
    }

    public void writeArrayToCSV(double[][] newData) throws IOException {
        String tempFile = "temp.csv";
        String filePath = "src\\main\\java\\yinonx\\apitest\\CSV's\\predictedMatrix.csv";
        // Read existing CSV file
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Create temporary file to store modified data
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        // Write existing data and keep the first row
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
        }
        reader.close();

        // Write new data, keeping the first column
        for (int i = 0; i < newData.length; i++) {
            writer.write(String.valueOf(newData[i][0])); // Write first column
            for (int j = 1; j < newData[i].length; j++) {
                writer.write("," + newData[i][j]);
            }
            writer.newLine();
        }

        // Close resources
        writer.close();

        // Rename the temporary file to the original file
        renameFile(tempFile, filePath);
    }

    private static void renameFile(String oldName, String newName) {
        // Rename the temporary file to the original file
        java.io.File oldFile = new java.io.File(oldName);
        java.io.File newFile = new java.io.File(newName);
        if (oldFile.renameTo(newFile)) {
            System.out.println("File renamed successfully");
        } else {
            System.out.println("Failed to rename file");
        }
    }

    public void printRealMatrix(RealMatrix matrix) {
        System.out.println("RealMatrix:");
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                double value = matrix.getEntry(i, j);
                System.out.printf("%f ,", value); // Adjust the format as needed
            }
            System.out.println(); // Move to the next row
        }
    }

    private List<String[]> readCSV(String filePath) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            return reader.readAll();
        }
    }

    public void printMatrix(double[][] matrix) {
        System.out.println("Matrix:");
        for (double[] row : matrix) {
            for (double value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public void printOnlyNonZeroMatrix(double[][] matrix) {
        System.out.println("Matrix:");
        for (double[] row : matrix) {
            for (double value : row) {
                // Add a condition to exclude printing 0 values
                if (value != 0.0) {
                    System.out.print(value + " ");
                }
            }
            System.out.println();
        }
    }

    public void printOnlyNonZeroRealMatrix(RealMatrix matrix) {
        System.out.println("Matrix (Non-Zero Values Only):");
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                double value = matrix.getEntry(i, j);
                // Check if the value is not equal to 0.0 (non-zero)
                if (value != 0.0) {
                    System.out.printf("%.2f ", value); // Adjust the format as needed
                }
            }
            System.out.println(); // Move to the next row
        }
    }

    public double[][] normalizeMatrix(double[][] matrix) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        // Find the minimum and maximum values in the matrix
        for (double[] row : matrix) {
            for (double value : row) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
        }

        // Ensure that the range is not zero to avoid division by zero
        if (min == max) {
            throw new ArithmeticException("Cannot normalize matrix with equal min and max values.");
        }

        // Normalize the values to the range 0 to 100
        double[][] normalizedMatrix = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                normalizedMatrix[i][j] = 100.0 * (matrix[i][j] - min) / (max - min);
            }
        }

        return normalizedMatrix;
    }

    public double[][] getMatrix() {
        System.out.println("making the csv file into a matrix");
        if (matrix == null) { // if the matrix is empty
            try {
                List<String[]> data = readCSV(csvFilePath);
                buildMatrix(data);
            } catch (IOException | CsvException e) {// in case the file has not been found
                e.printStackTrace();
            }
        }

        System.out.println("matrix has been created");
        return matrix;
    }

    public int getNumOfCols(RealMatrix matrix) {
        return matrix.getColumnDimension();
    }

    public int getNumOfRows(RealMatrix matrix) {
        return matrix.getRowDimension();
    }

    public long getIdForNewUser() {
        try {
            return getLastCellValue();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * Retrieves the value of the last cell in the specified CSV file.
     * used to generate Ids for new user
     *
     * @return the value of the last cell in the CSV file as a long integer.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public long getLastCellValue() throws IOException {
        String filePath = "src\\main\\java\\yinonx\\apitest\\CSV's\\Modified.csv";
        long lastCellValue = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Splitting the CSV line by comma
                String[] values = line.split(",");

                // Assuming the first column is numeric
                lastCellValue = Long.parseLong(values[0]);
            }
        }
        System.out.println("the ID that the function returend is :" + lastCellValue);
        return lastCellValue;

    }

    //function to all the games and their ratings for a user using his user ID from the reccomandation CSV file
    //as of now will return top 10 games to reccomend
    //TODO  exlude games that the user has already played
    public List<Game> getReccomandations(long userId) {
        List<Game> recommendedGames = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(predictionCsvFilePath))) {
            String line;

            // Skip the header line
            br.readLine();
            System.out.println("reading the csv file");
            // Read each line and search for the given user ID
            while ((line = br.readLine()) != null) {
                String[] cells = line.split(",");
                if (Long.parseLong(cells[0]) == userId) {
                    System.out.println("found user: " + cells[0]);
                    for (int i = 1; i < cells.length; i++) {
                        System.out.println("itirating through the cells at " + i);
                        String gameName = getGameName(i);
                       //TODO supposedly games should not have a null valuse, but should be handheld nonetheless, also handel cases where game name from function returned incorrectly
                        double rating = 0;
                        if (cells[i]!="") {
                            rating = Double.parseDouble(cells[i]);
                        }
                        recommendedGames.add(new Game(gameName,rating));
                        System.out.println("added game: " + gameName + " with rating: " + rating);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // // Sort recommended games by rating in descending order
         recommendedGames.sort(Comparator.comparing(Game::getRating).reversed());
        // Return top 10 recommendations
        return recommendedGames.subList(0, Math.min(10, recommendedGames.size()));
    }

    // Helper method to get the game name from the CSV header
    //gets a cell index and looks at the top row at the same index to get the game name, tested and working
    public String getGameName(int columnIndex) {
        // Assuming the first row of the CSV file contains the game names
        try (BufferedReader br = new BufferedReader(new FileReader(predictionCsvFilePath))) {
            String headerLine = br.readLine();
            String[] headers = headerLine.split(",");
            return headers[columnIndex];//may need +1 becuase of the first colomn being the id
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}