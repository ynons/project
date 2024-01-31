package yinonx.apitest;

import java.util.List;

public class Users {
private String userName;
private Double id;
private List <Game> games;
public String getUserName() {
    return userName;
}
public void setUserName(String userName) {
    this.userName = userName;
}
public Double getId() {
    return id;
}
public void setId(Double id) {
    this.id = id;
}
public List<Game> getGames() {
    return games;
}
public void setGames(List<Game> games) {
    this.games = games;
}
    


}
