package yinonx.apitest.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Desktop;

import org.springframework.stereotype.Service;

import yinonx.apitest.classes.Game;

import yinonx.apitest.classes.User;

@Service
public class MatrixService {
    private UserService userService;

    public MatrixService(UserService userService) {
        this.userService = userService;
    }

    public double[][] generateMatrix(List<User> users) {
        System.out.println("generating matrix");
        if (users.isEmpty()||users==null) {
            System.out.println("cannot generate matrix, List of users cannot be empty");
            return null;
        }
        
        List<Game> uniqeGames = new ArrayList<>();
        for (User user : users) {
           if (user.getGames()!=null) {
                for (Game game : user.getGames()) {
                   if(game!=null&&game.getName()!=null)
                    {uniqeGames.add(game);}//   
                }
           }
        }
        uniqeGames = GamesService.removeDuplicateGames(uniqeGames);
        //list of all the uniqe games has been created

        String[][] matrix = new String[users.size()+1][uniqeGames.size()+1];
        int i = 1;
        for (Game game : uniqeGames) {
            matrix[0][i] = game.getName();
            i++;
        }   
    
    
        System.out.println("\n\n\nnumber of games is" + uniqeGames.size());
        i = 1;
        for (User user : users) {
            if (user.getPlayedGames() != null) {
                matrix[i][0] = String.valueOf(user.getId());
                for (Game game : user.getPlayedGames()) {
                    if (getGameIndex(matrix,game.getName())!=-1)
                    {
                        matrix[i][getGameIndex(matrix, game.getName())] = String.valueOf(game.getPlayTime());
                        System.out.println("the game is "+game.getName()+" and the index is "+getGameIndex(matrix,game.getName()));
                    }
                }
                i++;
            }
        }
        System.out.println("\n\n\nthe complete matrix is\n\n");//
        for (int j = 0; j < matrix.length; j++) {
            for (int k = 0; k < matrix[0].length; k++) {
                System.out.print(","+matrix[j][k]);
            }
            System.out.println("||");
        }
        matrixToCSV(matrix, "C:\\Users\\ynon\\Desktop\\csv.csv");
        return convertPlaytimeMatrix(matrix);
    }
public static void matrixToCSV(String[][] matrix, String filePath) {
        File csvFile = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            for (String[] row : matrix) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Open the CSV file with the default application
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(csvFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop is not supported. Cannot open the CSV file.");
        }
    }

    public static void main(String[] args) {
        String[][] matrix = {
            {"Name", "Age", "City"},
            {"Alice", "30", "New York"},
            {"Bob", "25", "San Francisco"},
            {"Charlie", "35", "Los Angeles"}
        };

        String filePath = "matrix.csv";
        matrixToCSV(matrix, filePath);
    }
    // public List<Game> getAllUniquePlayedGames(List<Game> games) {
    //     Set<Game> uniquePlayedGames = GamesService.removeDuplicateGames(users);
        
    //     return new ArrayList<>(uniquePlayedGames);
    // }
   
    public int getGameIndex(String[][] matrix, String game) {
        for (int i = 1; i < matrix.length+1; i++) {
            if (matrix[0][i].equals(game)) {
                return i;
            }
        }
        return -1;
    }
    public static double[][] convertPlaytimeMatrix(String[][] playtimeMatrix) {
        // Determine the dimensions of the new double matrix
        int rows = playtimeMatrix.length - 1;
        int cols = playtimeMatrix[0].length - 1;

        double[][] result = new double[rows][cols];

        // Fill the double matrix with converted values
        for (int i = 1; i < playtimeMatrix.length; i++) {
            for (int j = 1; j < playtimeMatrix[i].length; j++) {
               if (playtimeMatrix[i][j] == null) {
                result[i - 1][j - 1] = 0.0;
               }
                else try {
                    result[i - 1][j - 1] = Double.parseDouble(playtimeMatrix[i][j]);
                } catch (NumberFormatException e) {
                    // Handle the case where parsing failed
                    System.out.println("Error parsing value at (" + i + ", " + j + "): " + playtimeMatrix[i][j]);
                    result[i - 1][j - 1] = 0.0; // fill with defult 0
                }
            }
        }

        return result;
    }

}
