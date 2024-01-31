package yinonx.apitest.matrixTest;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.javatuples.Pair;
 

@Service
public class MatrixFactorization {

    private CsvService csvService;

    public MatrixFactorization(CsvService csvService) {
        this.csvService = csvService;
    }
    
    public void testMatrix() {
        int userToPredict= 0;
        // int rank = 5;
        // double lambda1 = 0.001;
        // double learningRate = 0.02;
        double[][] matrix = getMatrixFromCsv();
        //System.out.println("matrix as from the CSV conversion");
        //csvService.printMatrix(matrix);
        matrix =  normalizeData(matrix);
        //csvService.printMatrix(matrix);
        //System.out.println("matrix post normaliziation");
        //csvService.printMatrix(matrix);
        predict(matrix,userToPredict, getSimRows(matrix, userToPredict)); 
        
        System.out.println(getSimRows(matrix, userToPredict));
       //  System.out.println("the prediction has been complete, the new matrix is:");
        //csvService.printMatrix(matrix);
        
    }
    
    private void predict(double[][] matrix, int usertopredict,  ArrayList<Pair<Double, Integer>> neighbors ) {
        //neighbors.sort(null);
        Collections.sort(neighbors, (pair1, pair2) -> Double.compare(pair1.getValue0(), pair2.getValue0()));

        //System.out.println(neighbors);
        for (int i = 0; i < matrix.length; i++) {
            if(matrix[usertopredict][i]==0)
            {
              //   System.out.println("prediction for line "+i+"is:"+matrix[i][neighbors.get(0).getValue1()]+"\n");
                // matrix[i][usertopredict] = matrix[i][neighbors.get(0).getValue1()];
                
            }
        
        }
    }

    public double cosineSim(double[][] matrix, int userToPredict, int otherUser) {
        if (userToPredict < 0 || userToPredict >= matrix.length ||
            otherUser < 0 || otherUser >= matrix.length) {
            throw new IllegalArgumentException("Invalid user numbers");
        }

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        // Calculate dot product and norms
        for (int i = 0; i < matrix[0].length; i++) {
            dotProduct += matrix[userToPredict][i] * matrix[otherUser][i];
            normA += Math.pow(matrix[userToPredict][i], 2);
            normB += Math.pow(matrix[otherUser][i], 2);
        }

        // avoiding dividing by zero(will casue errors)
        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        // Calculate cosine similarity(the score of how simuler a line is to the user to predict for)
        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));

        return similarity;
    }



    private ArrayList<Pair<Double, Integer>> getSimRows(double[][] matrix, int userToPredict)
     {
        
        ArrayList<Pair<Double, Integer>> cosPairs = new ArrayList<>();
        //itrate over the whole matrix and find the cosine simularity for the user
        for (int i = 0; i < matrix.length; i++) {
            Pair<Double, Integer> p = new Pair(cosineSim(matrix, userToPredict, i), i);
            cosPairs.add(p);
        }
        cosPairs.sort(null);
        //System.out.println(cosPairs);
        return cosPairs;

    }
       public double[][] getMatrixFromCsv() {

        double[][] matrix = csvService.getMatrix();
        return matrix;

    }

    public double[][] normalizeData(double[][] matrix) {
        // Iterate over rows to calculate mean and standard deviation
        for (int i = 0; i < matrix.length; i++) {
            double sumOfRatings = 0;
            double numOfRatings = 0;
    
            // Calculate mean
            for (int j = 1; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) {
                    sumOfRatings += matrix[i][j];
                    numOfRatings++;
                }
            }
    
            if (numOfRatings > 0) {
                double mean = sumOfRatings / numOfRatings;
    
                double sumOfSquaredDifferences = 0;
    
                // Calculate sum of squared differences for standard deviation
                for (int j = 1; j < matrix[i].length; j++) {
                    if (matrix[i][j] != 0) {
                        double difference = matrix[i][j] - mean;
                        sumOfSquaredDifferences += difference * difference;
                    }
                }
    
                double standardDeviation = Math.sqrt(sumOfSquaredDifferences / numOfRatings);
    
                // Standardize the ratings in the original matrix and round to 3 decimal points
                for (int y = 1; y < matrix[i].length; y++) {
                    if (matrix[i][y] != 0) {
                        matrix[i][y] = Math.round(((matrix[i][y] - mean) / standardDeviation) * 1000.0) / 1000.0;
                    }
                }
            }
        }
    
        return matrix;
    }
     
    

}
