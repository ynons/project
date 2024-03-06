package yinonx.apitest.classes;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection  = "User")

public class User {

@Id
private long id;
private List<Game> playedGames;
public List<Game> getPlayedGames() {
    return playedGames;
}

public void setPlayedGames(List<Game> playedGames) {
    this.playedGames = playedGames;
}
private String un;
private String pw;

public User() {
}

public User(long id, String un, String pw, List<Game> playedGames) {
    this.id = id;
    this.un = un;
    this.pw = pw;
    this.playedGames = playedGames;
}
public void addGame(Game game) {

   if (playedGames!=null) {
    playedGames.add(game);
   }
    else
    {       playedGames = new ArrayList<>(); // Initialize the list if it's null
            playedGames.add(game);
    }

}
public String getPw() {
    return pw;
}
public void removeGame(Game game)
{
    System.out.println("\n\n\n\n\n\n debuggin the game to remove is "+game);
    boolean isThisShitWorking = this.playedGames.remove(game);
    System.out.println("\n\n\n\n this shit is woking?"+isThisShitWorking);
    System.out.println("\n\n\n debbuging the list of games to remove from is "+this.getGames()+"\n\n");
}
@Override
public String toString() {
    return "User [id=" + id + ", playedGames=" + playedGames + ", un=" + un + ", pw=" + pw + "]";
}

public void setPw(String pw) {
    this.pw = pw;
}

public String getUn() {
    return un;
}
public void setUn(String userName) {
    this.un = userName;
}

public long getId() {
    return id;
}
public void setId(long id) {
    this.id = id;
}
public List<Game> getGames() {
    return playedGames;
}
public void setGames(List<Game> games) {
    this.playedGames = games;
}
    


}
