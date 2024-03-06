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
public class UserService
{
   private GamesService gamesService;
   private UserRepository userRepo;

   public UserService(UserRepository userRepo,GamesService gamesService)
   {
      this.userRepo = userRepo;
      this.gamesService = gamesService;
   }

   
   public boolean addUser(User user)
   {
      try {
         userRepo.insert(user);
       }
       catch(Exception e) {
         System.out.println(e+"could not insert user");
         return false;
       } 
       return true;
   }
   public void UpdateUser(User currntUser)
   {
  
  try {
   userRepo.save(currntUser);
  } catch (Exception e) {
   System.out.println(e);
   System.out.println("userService.updateUser: could not update user");   
}    
   }
public User findUserById(long Id)
{
   User currntUser=null;
   try {
      currntUser = userRepo.findById(Id);
      return currntUser;
   } catch (Exception e) {
      System.out.println(e);
      System.out.println("userService.finUserbyUn:could not find user by ID");
      return null;
   }
}
   public List<User> getAllUsers()
   {
      return userRepo.findAll();
   }
   

   public User findUserByUn(String un)
   {
      try {
         User curUser = userRepo.findByUn(un);
         return curUser;
      } catch (Exception e) {
         System.out.println("\n"+e);
         System.out.println("userService.findUser: user has not been found");
         return null;
      
      }

   }

   public boolean isUserExists(String un, String pw)
   {
      System.out.println("userService.isUserExsists: looking for user is user");
      // User currntUser = null;
      // try {
      //     currntUser = userRepo.findByUn(un);
      //  }
      //  catch(Exception e) {
      //    System.out.println("\n"+e.toString());
      //    System.out.println("userService: the user dosnt exist");
      //  }
      //  if (currntUser != null)
      //  {
       
      //    if (currntUser.getPw().equals(pw))
      //       return true;
      //       System.out.println("passward matches");
      //  }
      // return false;

      User curUser = userRepo.findByUn(un);

      if(curUser!=null)
   {
      if(curUser.getPw().equals(pw))
      {
         System.out.println("Userservice.isUserExsists: user found");
         return true;
      }
      
   }
   return false;
   }

public  void deleteGame(String gameName,String userName) {

   User curUser = findUserByUn(userName);
 if (curUser==null)
 {
    System.out.println("UserService.deleteGame the user has not been found");
 }
 
   Game game = gamesService.getGameDetailsByName(gameName);
   curUser.removeGame(game);
}

}
