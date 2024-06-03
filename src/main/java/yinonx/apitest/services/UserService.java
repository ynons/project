package yinonx.apitest.services;

import java.util.List;

import org.springframework.stereotype.Service;

import yinonx.apitest.classes.Game;
import yinonx.apitest.classes.User;
import yinonx.apitest.repos.UserRepository;

/**
 * User Service.
 * Ilan Peretz | 16.11.2023
 */
@Service
public class UserService {
   private GamesService gamesService;
   private UserRepository userRepo;
   private CsvService csvService;

   public UserService(UserRepository userRepo, GamesService gamesService, CsvService csvCervice) {
      this.csvService = csvCervice;
      this.userRepo = userRepo;
      this.gamesService = gamesService;
   }

   /**
    * Creates a new user with the given username and password.
    * does not add it to the database, but only creates and returns a user with a
    * legal ID
    *
    * @param username the username of the new user
    * @param passward the password of the new user
    * @return the newly created user
    */
   public User createUserWithId(String username, String passward) {
      User user = new User(getIdForNewUser(), username, passward);
      return user;
   }

   public boolean addGameToUser(String gameName, String userName, int playTime) {
      User user = findUserByUn(userName);
      if (user == null) {
         System.out.println("UserService.addGameToUser: User not found");
         return false;
      }

      Game game = gamesService.getGameDetailsByName(gameName);

      if (game == null) {
         System.out.println("UserService.addGameToUser: Game not found");
         return false;
      }
      if (user.getPlayedGames() == null) {
         user.setPlayedGames(new java.util.ArrayList<Game>());
      }

      game.setPlayTime(playTime);
      List<Game> playedGames = null;
      try {
         playedGames = user.getPlayedGames();
      } catch (Exception e) {
         System.out.println(e);
      }
      
      for (Game playedGame : playedGames) {
            if (playedGame.getName().equals(gameName)) {
               System.out.println("UserService.addGameToUser: Game already in user's list");
               return false;
            }
         }
         user.addGame(game);
         try {
            userRepo.save(user);
         } catch (Exception e) {
            System.out.println(e);
            System.out.println("UserService.addGameToUser: Could not update user");
            return false;
         }
         return true;

      }
   

   public long getIdForNewUser() {
      return csvService.getIdForNewUser();
   }

   public boolean addUser(User user) {
     if (user == null||userRepo.findByUn(user.getUn())!=null) {
      System.out.println("UserService.addUser: user is null username is taken");
      return false;
     }
      try {
         userRepo.insert(user);
      } catch (Exception e) {
         System.out.println(e + "could not insert user");
         return false;
      }
      return true;
   }

   public void UpdateUser(User currntUser) {

      try {
         userRepo.save(currntUser);
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("userService.updateUser: could not update user");
      }
   }

   public User findUserById(long Id) {
      User currntUser = null;
      try {
         currntUser = userRepo.findById(Id);
         return currntUser;
      } catch (Exception e) {
         System.out.println(e);
         System.out.println("userService.finUserbyUn:could not find user by ID");
         return null;
      }
   }

   public List<User> getAllUsers() {
      return userRepo.findAll();
   }

   public User findUserByUn(String un) {
      try {
         User curUser = userRepo.findByUn(un);
         return curUser;
      } catch (Exception e) {
         System.out.println("\n" + e);
         System.out.println("userService.findUser: user has not been found");
         return null;

      }

   }

   public boolean isUserExists(String un, String pw) {
      System.out.println("userService.isUserExsists: looking for user is user");
      // User currntUser = null;
      // try {
      // currntUser = userRepo.findByUn(un);
      // }
      // catch(Exception e) {
      // System.out.println("\n"+e.toString());
      // System.out.println("userService: the user dosnt exist");
      // }
      // if (currntUser != null)
      // {

      // if (currntUser.getPw().equals(pw))
      // return true;
      // System.out.println("passward matches");
      // }
      // return false;

      User curUser = userRepo.findByUn(un);

      if (curUser != null) {
         if (curUser.getPw().equals(pw)) {
            System.out.println("Userservice.isUserExsists: user found");
            return true;
         }

      }
      return false;
   }

   public void deleteGame(String gameName, String userName) {

      User curUser = findUserByUn(userName);
      if (curUser == null) {
         System.out.println("UserService.deleteGame the user has not been found");
      }

      Game game = gamesService.getGameDetailsByName(gameName);
      curUser.removeGame(game);
   }

   /**
    * Checks if the password is valid based on its length.
    * passeard is valid if its length is greater than or equal to 3
    * validates the password
    *
    * @param passward the password to be checked
    * @return true if the password length is greater than or equal to 3, false
    *         otherwise
    */
   public boolean pwIsValid(String passward) {
      if (passward.length() < 3) {
         return false;
      }
      return true;
   }

}
