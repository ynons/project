package yinonx.apitest.services;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;


@Service
public class CsvService {
    private double[][] matrix;
    private String csvFilePath = "src\\main\\java\\yinonx\\apitest\\matrixTest\\CSV's\\Modified.csv";
    private int numRowsToProcess = 4;
    private int numColsToProcess = 10; // Default value, -1 means process all columns

    public CsvService() {
    }
    public CsvService(int numRowsToProcess, int numColsToProcess) {
        this.numRowsToProcess = numRowsToProcess;
        this.numColsToProcess = numColsToProcess;
    }
    private void buildMatrix(List<String[]> data) {
        int numRows = Math.min(data.size(), numRowsToProcess);
    
        // Ensure there is at least one row of data
        if (numRows == 0) {
            throw new IllegalArgumentException("Empty data list");
        }
    
        int numCols = (numColsToProcess == -1) ? data.get(0).length - 1 : 
                      Math.min(data.get(0).length - 1, numColsToProcess);
    
        matrix = new double[numRows][numCols];
    
        for (int i = 1; i <= numRows; i++) { // Start from index 1 to ignore the header row
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
    public int getNumOfCols(RealMatrix matrix)
    {
        return matrix.getColumnDimension();
    }
    public int getNumOfRows(RealMatrix matrix)
    {
        return matrix.getRowDimension();
    }
}
