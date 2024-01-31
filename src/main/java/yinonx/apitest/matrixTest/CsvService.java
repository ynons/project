package yinonx.apitest.matrixTest;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
@Service
public class CsvService {
   // private String csvFilePath = "C:\\Users\\ynon\\Downloads\\‏‏MOCK_DATA - עותק.csv";
    private  double[][] matrix;
    private String csvFilePath = "src\\main\\java\\yinonx\\apitest\\matrixTest\\CSV's\\yinon.csv";
    
    // "apitest\\src\\main\\java\\yinonx\\apitest\\matrixTest\\CSV's\\MOCK_DATA.csv";
    public  CsvService()
    {
    
    }

    private void buildMatrix(List<String[]> data) {
        int numRows = data.size();
        int numCols = data.get(0).length;

        matrix = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                String value = data.get(i)[j].trim();
                matrix[i][j] = value.isEmpty() ? 0.0 : Double.parseDouble(value);
            }
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
               //if(value!=0||value!=0.0)
               System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public  double[][] getMatrix() {
       System.out.println("making the csv file into a matrix");
        if (matrix == null) { //if the matrix is empty
            try {
                List<String[]> data = readCSV(csvFilePath);
                buildMatrix(data);
            } catch (IOException | CsvException e) {//in case the file has not been found
                e.printStackTrace();
            }
        }
        
        System.out.println("matix has been created");
        //printMatrix(matrix); prints the matrix that has just been created from the csv file
        return matrix;
    }
}
