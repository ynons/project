package yinonx.apitest.matrixTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.springframework.stereotype.Service;

import yinonx.apitest.services.CsvService;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.nio.file.Path;

@Service
public class MatrixFactorization {

    private CsvService csvService;


    public MatrixFactorization(CsvService csvService) {
        this.csvService = csvService;
    }

    // technicly the main function to call for NOW
    public void factorizeMatrix() throws IOException {
        int minValue = 0;
        int maxValue = 5;
        int numFactors = 0;
        int numberOfSteps = 10000;
        double RegularizationTerm = 0.01;
        double rateOflernigng = 0.000001;
        double[][] matrix = generateMatrix(200);
        numFactors = matrix.length;
        double[][] predictedMatrix = matrixFactorization(matrix, numFactors, minValue, maxValue, numberOfSteps, numFactors, RegularizationTerm, rateOflernigng);
        csvService.writeArrayToCSV(predictedMatrix);
        runPythonScript("C:\\Users\\ynon\\Downloads\\import_matplotlib.py");
    }
    //my matrix factorization function. all the rest are used as refrence
    public  double[][] generateMatrix(int n) {
        double[][] matrix = new double[n][n];
        Random rand = new Random();
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextDouble() * 5; // Generates a random double between 0 and 5
            }
        }
        
        return matrix;
    }
    private double[][] matrixFactorization(double[][] matrix, int numFactors, int minValue, int maxValue, int steps, int numOfFactors, double regTerm, double learningRate) {
        // intililze two random matrices in the appropriate size
        double[][] userMatrix = initializeMatrix(matrix[0].length, numFactors, minValue, maxValue);
        double[][] itemMatrix = initializeMatrix(numFactors, matrix.length, minValue, maxValue);
        double prevRMSE = Double.MAX_VALUE;
        int errorCount=0;
        // set error to max value so that it will enter the loop on first go
        // create a list to store all errors in. will be turned into a grpah later, use
        // to evaluate the
        // working of the model
        double error = Double.MAX_VALUE;
        List<Double> doubleList = new ArrayList<>();

        // loop over the original matrix times that are specfied in the dunction
        // parameters(aka steps)
        for (int step = 0; step < steps; step++) {
            // debug: print the number of step being done
            System.out.println("step : " + step);

            // loop over each cell in the matrix R (og matrix)
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    // go over all non zero enteris
                    // if there is an entery in matrix R aka if user has played that game
                    if (matrix[i][j] != 0) {
                        // get the prediction of the currnt cell
                       // System.out.println("debugging"+dotProduct(getRowOfMtrix(userMatrix, i), getColomnOFMatrix(itemMatrix, j)));
                        double dotProduct = dotProduct(getRowOfMtrix(userMatrix, i), getColomnOFMatrix(itemMatrix, j));
                        // calculate the mean squared error of the prediction reletive to the actual
                        // value
                        //System.out.println("the dot product is : " + dotProduct);
                        error = Math.pow(matrix[i][j] - dotProduct, 2);

                        // for each k value in the matrices U and V
                        for (int k = 0; k < numFactors; k++) {
                            // update the k values in the matrices U and V according to the error

                            double Ustep = -2 * ((matrix[i][j] - dotProduct) * (itemMatrix[i][k]));
                            Ustep = flipSign(Ustep);
                            userMatrix[i][k] += Ustep * learningRate;
                            double Istep = -2 * ((matrix[i][j] - dotProduct) * (userMatrix[k][j]));
                            Istep = flipSign(Istep);
                            itemMatrix[j][k] += Istep * learningRate;
                         //   System.out.println("the steps taken are: " + Istep * learningRate + " and " + learningRate * Ustep);
                        }

                    }
                }

            }
            
            
            double prediction;
            double sumSquaredErrors = 0;

            for (int x = 0; x < matrix.length; x++) {
                for (int y = 0; y < matrix[0].length; y++) {
                    if (matrix[x][y] != 0) {
                        errorCount++;
                        prediction = dotProduct(getColomnOFMatrix(userMatrix, x), getRowOfMtrix(itemMatrix, y));
                        double e = matrix[x][y]-prediction;
                        sumSquaredErrors += e*e;
                       
                    }
                }
            }
            double rmse = Math.sqrt(sumSquaredErrors / errorCount);
            doubleList.add(rmse);


            System.out.println("the error is: " + rmse+"\n and the step is :" + step) ;
            if (rmse < 0.1) {
                System.out.println("error has reaced a minimum value of: " + rmse);
                System.out.println("and it took " + step + " itarations to reach this value");
                break;
            }
            if (step > 0) {
                rmse = Math.sqrt(sumSquaredErrors / errorCount);
                if (rmse > prevRMSE) {
                    System.out.println("RMSE started to rise. Stopping training...");
                    break;
                }
                prevRMSE = rmse;
            }

        }

        String csvName = "data.csv";
        String directory = "C:\\Users\\ynon\\Documents\\testing";
        writeToCSV(doubleList, directory, csvName);

        double [][] predictR = predictR(userMatrix, itemMatrix);

        return predictR;
    
    }
    
    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public double flipSign(double number) {
        return -number;
    }


    private double[] getRowOfMtrix(double[][] u, int row) {
        double[] firstRow = u[row];
        return firstRow;
    }

    private double[] getColomnOFMatrix(double[][] u, int colomn) {
        int numRows = u.length;
        double[] Column = new double[numRows];
        for (int i = 0; i < numRows; i++) {
            Column[i] = u[i][colomn];
        }
        return Column;
    }


    public double[][] predictR(double[][] U, double[][] P) {
        int numUsers = U.length;
        int numItems = P[0].length;
        double[][] predictedR = new double[numUsers][numItems];

        // Perform dot product of matrices U and P
        for (int i = 0; i < numUsers; i++) {
            for (int j = 0; j < numItems; j++) {
                predictedR[i][j] = dotProduct(U[i], P[j]);
            }
        }

        return predictedR;
    }
    public void runPythonScript(String pythonScriptPath) {
        try {
            Process process = Runtime.getRuntime().exec("python " + pythonScriptPath);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();

            int exitValue = process.exitValue();
            if (exitValue == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.out.println("Python script execution failed with error code: " + exitValue);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void writeToCSV(List<Double> dataList, String directory, String fileName) {
        FileWriter fileWriter = null;

        try {
            // Create the directory if it doesn't exist
            Path path = Paths.get(directory);
            if (!path.toFile().exists()) {
                path.toFile().mkdirs();
            }

            // Create the file within the specified directory
            fileWriter = new FileWriter(Paths.get(directory, fileName).toString());

            // Write data to the CSV file
            for (Double data : dataList) {
                fileWriter.append(String.valueOf(data));
                fileWriter.append(",");
            }

            // Add a new line character after each row
            fileWriter.append("\n");

            System.out.println("CSV file has been created successfully!");

        } catch (IOException e) {
            System.out.println("Error in writing CSV file: " + e.getMessage());
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                System.out.println("Error while closing file writer: " + e.getMessage());
            }
        }
    }

    // of whole matrix
    private double calculateError(double[][] R, double[][] P, double[][] Q, double beta) {
        double error = 0;
        for (int i = 0; i < R.length; i++) {
            for (int j = 0; j < R[0].length; j++) {
                if (R[i][j] > 0) {
                    double prediction = dotProduct(P[i], Q[j]);
                    error += Math.pow(R[i][j] - prediction, 2);
                    for (int k = 0; k < P[0].length; k++) {
                        error += (beta / 2) * (Math.pow(P[i][k], 2) + Math.pow(Q[j][k], 2));
                    }
                }
            }
        }
        return error;
    }

    private double dotProduct(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    // populate a matrix with random values according to provided parameters

    private double[][] initializeMatrix(int numRows, int numCols, int minValue, int maxValue) {
        double[][] matrix = new double[numRows][numCols];
        Random random = new Random();
        int range = maxValue - minValue;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrix[i][j] = random.nextInt(range) + minValue;
            }
        }
        return matrix;
    }

    

    public double calculateRMSE(double actual, double predicted) {
        // Calculate the squared error
        double squaredError = Math.pow(actual - predicted, 2);

        // Return the square root of the mean of squared errors
        return Math.sqrt(squaredError);
    }

    public RealMatrix initializeRealMatrix(int numRows, int numCols, double minValue, double maxValue) {
        double[][] matrixData = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                double randomValue = minValue + Math.random() * (maxValue - minValue);
                matrixData[i][j] = randomValue;
            }
        }

        return MatrixUtils.createRealMatrix(matrixData);
    }

    // multiply the matrices back to the R matrix according to the wights
    private RealMatrix multiplyRealMatrices(RealMatrix matrixA, RealMatrix matrixB) {
        if (matrixA.getColumnDimension() != matrixB.getRowDimension()) {
            throw new IllegalArgumentException("Invalid matrix dimensions for multiplication");
        }

        return matrixA.multiply(matrixB);
    }

    private double[][] multiplyMatrices(double[][] matrixA, double[][] matrixB) {
        int aRows = matrixA.length;
        int aCols = matrixA[0].length;
        int bCols = matrixB[0].length;

        if (aCols != matrixB.length) {
            throw new IllegalArgumentException("Invalid matrix dimensions for multiplication");
        }

        double[][] result = new double[aRows][bCols];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bCols; j++) {
                for (int k = 0; k < aCols; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }

    private RealMatrix convertToRealMatrix(double[][] doubleMatrix) {
        int rows = doubleMatrix.length;
        int cols = doubleMatrix[0].length;

        // Create a RealMatrix from double[][]
        RealMatrix realMatrix = new Array2DRowRealMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                realMatrix.setEntry(i, j, doubleMatrix[i][j]);
            }
        }

        return realMatrix;
    }

    private double[][] convertToDoubleArray(RealMatrix realMatrix) {
        int rows = realMatrix.getRowDimension();
        int cols = realMatrix.getColumnDimension();

        // Create a 2D array of doubles
        double[][] doubleMatrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                doubleMatrix[i][j] = realMatrix.getEntry(i, j);
            }
        }

        return doubleMatrix;
    }

    public double calculateRMSE(double[][] predicted, double[][] actual) {
        // Ensure both matrices have the same dimensions
        if (predicted.length != actual.length || predicted[0].length != actual[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions");
        }

        int numRows = predicted.length;
        int numCols = predicted[0].length;

        // Calculate the squared differences between predicted and actual values
        double sumSquaredDiff = 0.0;
        int numElements = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (!Double.isNaN(predicted[i][j]) && !Double.isNaN(actual[i][j])) {
                    double diff = predicted[i][j] - actual[i][j];
                    sumSquaredDiff += diff * diff;
                    numElements++;
                }
            }
        }

        // Calculate the mean squared error
        double meanSquaredError = sumSquaredDiff / numElements;

        // Calculate the root mean squared error
        double rmse = Math.sqrt(meanSquaredError);

        return rmse;
    }

}