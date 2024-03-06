package yinonx.apitest.matrixTest;

import java.util.Random;


import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.springframework.stereotype.Service;

import yinonx.apitest.services.CsvService;

@Service
public class MatrixFactorization {

    private CsvService csvService;

    public MatrixFactorization(CsvService csvService) {
        this.csvService = csvService;
    }

    // technicly the main function to call for NOW
    public void getMatrixFromCsv() {
        int minValue = 0;
        int maxValue = 1;
        int numFactors = 5;
        // get the matrix from the CSV file
        // double[][] matrix = csvService.getMatrix();
        double[][] matrix = {
                { 0.725, 0.392, 0.864, 0.123 },
                { 0.543, 0.987, 0.654, 0.321 },
                { 0.234, 0.567, 0.890, 0.432 },
                { 0.901, 0.345, 0.678, 0.987 },
        };

        System.out.println("the matrix from the CSV");
        csvService.printMatrix(matrix);
        RealMatrix userMatrix = initializeRealMatrix(matrix[0].length, numFactors, minValue, maxValue);
        RealMatrix itemMatrix = initializeRealMatrix(numFactors, matrix.length, minValue, maxValue);
    //    System.out.println("the item matrix is \n");
    //    csvService.printRealMatrix(itemMatrix);
    //    System.out.println("the user matrix is \n");
    //    csvService.printRealMatrix(userMatrix);
        matrixFactorization(matrix, convertToDoubleArray(userMatrix), convertToDoubleArray(itemMatrix), numFactors);
        System.out.println("the factorized matrix is\n");
        System.out.println(matrix);
    }

    // private static void matrixFactorization(double[][] R, double[][] P, double[][] Q, int K) {
    //     int steps = 5000; // Maximum number of steps
    //     double alpha = 0.0002; // Learning rate
    //     double beta = 0.02; // Regularization parameter

    //     for (int step = 0; step < steps; step++) {
    //         for (int i = 0; i < R.length; i++) {
    //             for (int j = 0; j < R[0].length; j++) {
    //                 if (R[i][j] > 0) {
    //                     double eij = R[i][j] - dotProduct(P[i], Q[j]);
    //                     for (int k = 0; k < K; k++) {
    //                         P[i][k] = P[i][k] + alpha * (2 * eij * Q[j][k] - beta * P[i][k]);
    //                         Q[j][k] = Q[j][k] + alpha * (2 * eij * P[i][k] - beta * Q[j][k]);
    //                     }
    //                 }
    //             }
    //         }

    //         // Calculate error and check for convergence
    //         double error = calculateError(R, P, Q, beta);
    //         if (error < 0.001) {
    //             break;
    //         }
    //     }
    // }
    private static void matrixFactorization(double[][] R, double[][] P, double[][] Q, int K) {
       
        int steps = 5000; // Maximum number of steps allowd for the algorithem,
        double alpha = 0.0002; // Learning rate, the size of steps taken for twiking the wights
        double beta = 0.02; // Regularization parameter, use to prevent over fitting, higher = less fitting
        int h =0;

        for (int step = 0; step < steps; step++) {
            System.out.println("factorizing matrix step: "+ step+"out of "+ steps);
            for (int i = 0; i < R.length; i++) {
                System.out.println("intirating over matrix R row number "+i);
                for (int j = 0; j < R[0].length; j++) {
                    System.out.println("itirating over matrix R colomn number"+j);
                    if (R[i][j] > 0) {
                        System.out.println("found a value that is not missign") ;
                        //error in [i][j]
                        double eij = R[i][j] - dotProduct(P[i], Q[j]);
                        System.out.println("caculated the error for place I J ");
                        for (int k = 0; k < K; k++) {
                            System.out.println("updating wight time " +h);
                            h++;
                            P[i][k] = P[i][k] + alpha * (2 * eij * Q[j][k] - beta * P[i][k]);
                            Q[j][k] = Q[j][k] + alpha * (2 * eij * P[i][k] - beta * Q[j][k]);
                        }
                    }
                }
            }

            // Calculate error and check for convergence
            double error = calculateError(R, P, Q, beta);
            if (error < 0.1) {
                break;
            }
        }
    }
    //of whole matrix
    private static double calculateError(double[][] R, double[][] P, double[][] Q, double beta) {
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

    private static double dotProduct(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length-1; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    // populate a matrix with random values according to provided parameters
    private static double[][] initializeMatrix(int numRows, int numCols, double minValue, double maxValue) {
        double[][] matrix = new double[numRows][numCols];
        Random random = new Random();
        double range = maxValue - minValue;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrix[i][j] = random.nextDouble() * range + minValue;
            }
        }
        return matrix;
    }

    public void gredientDecent(double[][] user, double[][] item, double[][] predictedMatrix, double[][] original) {
        int y = 1;
        int x = 1;
        double lerningRate = 0.01;
        double error = Double.MAX_VALUE;
        double prevError = 0;

        for (int i = 0; i < 1000; i++) {
            prevError = error;
            error = calculateRMSE(predictedMatrix[x][y], original[x][y]);
            System.out.println("the RMSE for cycle number: " + i + " is: " + error + "\n");
            if (error < prevError) {
                System.out.println("\n");
                updateWights(user, item, error, lerningRate);
            }
            // else
            // {
            // break;
            // }
        }
    }

    private void updateWights(double[][] user, double[][] item, double error, double lerningRate) {

    }

    public static double calculateRMSE(double actual, double predicted) {
        // Calculate the squared error
        double squaredError = Math.pow(actual - predicted, 2);

        // Return the square root of the mean of squared errors
        return Math.sqrt(squaredError);
    }

    public static RealMatrix initializeRealMatrix(int numRows, int numCols, double minValue, double maxValue) {
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
    private static RealMatrix multiplyMatrices(RealMatrix matrixA, RealMatrix matrixB) {
        if (matrixA.getColumnDimension() != matrixB.getRowDimension()) {
            throw new IllegalArgumentException("Invalid matrix dimensions for multiplication");
        }

        return matrixA.multiply(matrixB);
    }

    private static RealMatrix convertToRealMatrix(double[][] doubleMatrix) {
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

    private static double[][] convertToDoubleArray(RealMatrix realMatrix) {
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

    private RealMatrix getU(SingularValueDecomposition currentSvd) {
        return currentSvd.getU();
    }

    private RealMatrix getV(SingularValueDecomposition currentSvd) {
        return currentSvd.getV();
    }

    private RealMatrix getS(SingularValueDecomposition currentSvd) {
        return MatrixUtils.createRealDiagonalMatrix(currentSvd.getSingularValues());
    }
    // 進行中の作業
    // this function calculate the RMSE of two rows
    // it will get the predicted data as well as the real data in the form of two
    // arrays and return a double value representetive of the diffrence
    // exmple usage
    // *double[] actualRow = {1.0, 2.0, 3.0};
    // double[] predictedRow = {1.0, 2.0, 3.0};
    // System.out.println(calculateRowRMSE(actualRow,predictedRow));*

    private static double calculateRowRMSE(double[] actualRow, double[] predictedRow) {
        int numColumns = actualRow.length;

        double sumSquaredDiff = 0.0;

        for (int i = 0; i < numColumns; i++) {
            double diff = actualRow[i] - predictedRow[i];
            sumSquaredDiff += Math.pow(diff, 2);
        }

        double meanSquaredDiff = sumSquaredDiff / numColumns;
        double rmse = Math.sqrt(meanSquaredDiff);

        return rmse;
    }

    public static double calculateRMSE(double[][] predicted, double[][] actual) {
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