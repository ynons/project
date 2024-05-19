package yinonx.apitest.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.helger.commons.annotation.PrivateAPI;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.AlignSelf;
import com.vaadin.flow.dom.Style.TextAlign;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import yinonx.apitest.classes.Game;
import yinonx.apitest.classes.User;
import yinonx.apitest.services.CsvService;
import yinonx.apitest.services.GamesService;
import yinonx.apitest.services.MatrixFactorization;
import yinonx.apitest.services.UserService;

@Route(value = "/game")
public class gamePage extends VerticalLayout {

    private String loggedInUser =(String)UI.getCurrent().getSession().getAttribute("username");
    private Grid<Game> recommendedGameGrid = new Grid<>();
    private UserService userService;
    private CsvService csvService;
    private GamesService gamesService;
    private MatrixFactorization matrixFactorization;
    private Grid<Game> gameGrid = new Grid<>();
    private TextField inputTextField = new TextField("Enter Game Name");
    private TextField gameTimeInputField = new TextField("play time in hours");
    private TextField gameNameTextField;
    private TextField gameRatingTextField;
    private TextField gameReleaseDateTextField;
    private  String IMAGE_LOGO_URL = "https://media.npr.org/assets/img/2021/12/28/hsieh_angela_nprpchh_videogamesa_wide-7b51b98254a8a594e0ac928517b589efb3763578-s1100-c50.jpg";

    public gamePage(GamesService gamesService, MatrixFactorization matrixFactorization, CsvService csvService,
    UserService userService) {
        this.csvService = csvService;
        this.gamesService = gamesService;
        this.matrixFactorization = matrixFactorization;
        this.userService = userService;

        H2 addGamesText = new H2("here you can look up games and add them to your played games list");
        addGamesText.getStyle().setColor("black");
        addGamesText.getStyle().setTextAlign(TextAlign.CENTER);
        addGamesText.getStyle().setFontWeight(10);
        
        H2 yourGamesText = new H2("here you can see the games you have played, delete them or edit their play time");
           yourGamesText.getStyle().setColor("black");
           yourGamesText.getStyle().setTextAlign(TextAlign.CENTER);
           yourGamesText.getStyle().setFontWeight(10);

        H2 reccomandationText = new H2("here you can see the games that our systeam reccomends for you, you can double click a game to add it to your played games list");
           reccomandationText.getStyle().setColor("black");
           reccomandationText.getStyle().setTextAlign(TextAlign.CENTER);
           reccomandationText.getStyle().setFontWeight(10);

        add(new H2("welcome to YourGamesList.com"));
        
        Notification.show("the currnt logged in user is "+ (String)VaadinSession.getCurrent().getAttribute("username"));      
        // .getStyle().setBackground("linear-gradient(to bottom, #FFFFFF, blue)");

        TabSheet gametabSheet = new TabSheet();// create a new tab sheet(the object to put tabs within)
        this.getStyle().setAlignItems(AlignItems.CENTER);// set tab aligment ot be centered
       
        // create all the bottons nececery
        Button searchBotton = new Button("Search");
        searchBotton.addClassName("game-page-button-1");
        Button factorizeBotton = new Button("factorize");
        Button addToPlayedGames = new Button("Add game to played list");
     //   Button factorizeButton = new Button("factorize");

        // create all the labels to be used
        gameNameTextField = new TextField();
        gameRatingTextField = new TextField();
        gameReleaseDateTextField = new TextField();
        // set them as read only so they can be used as labels
        gameReleaseDateTextField.setReadOnly(true);
        gameRatingTextField.setReadOnly(true);
        gameNameTextField.setReadOnly(true);
        
        // create text compunents to put infront of the labels
        Span gameNamelSpan = new Span("gameName = ");
        Span gameRatingSpan = new Span("gameRating = ");
        Span gameReleaseDateSpan = new Span("gameReleaseDate = ");
        
        // activate the search button
        searchBotton.addClickListener(e -> getGameDetails(inputTextField.getValue()));
       // activate the factorize button
    // factorizeBotton.addClickListener(e->System.out.println(csvService.getReccomandations(userService.findUserByUn(loggedInUser).getId())));
        factorizeBotton.addClickListener(e->{
            try {
                matrixFactorization.factorizeMatrix();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        add(gametabSheet);

              // Configure the grid for the recommended games
              //add all the neccecery colomns
              recommendedGameGrid.addColumn(Game::getName).setHeader("Name");
              recommendedGameGrid.addComponentColumn(game -> {
                  if (game.getCoverImageLink() != null) {
                      Image image = new Image(game.getCoverImageLink(), "Cover Image");
                      image.setHeight("50px"); // Set the height as needed
                      image.setWidth("50px"); // Set the width as needed
                      return image;
                  } else {
                      // If cover image link is null, return a placeholder image or an empty component
                      // Example of a placeholder image:
                      // Image placeholderImage = new Image("path/to/placeholder-image.png", "Placeholder Image");
                      // placeholderImage.setHeight("50px");
                      // placeholderImage.setWidth("50px");
                      // return placeholderImage;
                      
                      // Alternatively, you can return an empty component
                      return new Span(); // Or any other Vaadin component that suits your needs
                  }
              }).setHeader("Cover Image");
              
        // Configure the grid for played games
        // add all the neccecery colomns
        gameGrid.addColumn(Game::getName).setHeader("Name");
        gameGrid.addColumn(Game::getReleaseDate).setHeader("Release Date");
        gameGrid.addColumn(Game::getRating).setHeader("Rating");
        gameGrid.addColumn(Game::getPlayTimeAsString).setHeader("playTime");
        gameGrid.addColumn(game -> game.getPlatforms() != null ? String.join(", ", game.getPlatforms()) : "")
                .setHeader("Platforms");
        //add a listiner for double clicking a game
        gameGrid.addItemDoubleClickListener(event -> deleteGame(event.getItem().getName(),
                loggedInUser));

        // Add components to the layout
        // style compunents

        //set the distance betwen th ecomponents
        inputTextField.getStyle().setPadding("20px");
        gameTimeInputField.getStyle().setPadding("20px");

        new Div(new Text("here you can add games"));
        Div gameTabDiv = new Div(yourGamesText,gameGrid);
        Div yourGamesDiv2 = new Div(inputTextField, searchBotton);
        Div yourGamesDiv1 = new Div(gameTimeInputField, addToPlayedGames);

        Div span1 = new Div(gameNamelSpan, gameNameTextField);
        Div span2 = new Div(gameRatingSpan, gameRatingTextField);
        Div span3 = new Div(gameReleaseDateSpan, gameReleaseDateTextField);
        span1.add();
        recommendedGameGrid.addItemDoubleClickListener(event->{
            AddGameToUser(event.getItem().getName(), "1" ,loggedInUser);
        
        });
        recommendedGameGrid.setColumnRendering(ColumnRendering.EAGER);
        recommendedGameGrid.setItems(userService.findUserByUn("yinon").getGames());

        addToPlayedGames.addClickListener(e->{
            AddGameToUser(gameNameTextField.getValue(), gameTimeInputField.getValue() ,loggedInUser);
        });

        Div searchDiv = new Div(span1, span2, span3);
        Div recoDiv = new Div(factorizeBotton,reccomandationText,recommendedGameGrid);
       
        Div mainDiv = new Div(addGamesText,yourGamesDiv1, yourGamesDiv2, searchDiv);
        
        gametabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);
        gametabSheet.add("played Games", gameTabDiv);
        gametabSheet.add("reccomended games",recoDiv);
        gametabSheet.add("add Games", mainDiv);
        gametabSheet.setSizeFull();
        //gametabSheet.getStyle().set("height", "100%");
        //gameGrid.setHeight("50%");
        populateGridWithUserGames(loggedInUser);
       boolean gripopulated = populatereccomndationGrid(loggedInUser);
       if (gripopulated==false) {
        //show messege saying recomendations cannot be dislayed
        Notification.show("reccomendations cannot be displayed, please try again later");
       } 
       //<theme-editor-local-classname>
        addClassName("game-page-vertical-layout-1");

    }

    

    private boolean AddGameToUser(String gameName, String playtimeString, String userName) {
        System.out.println("adding game to user");
        int playtime = 0;
        User currentUser = null;
        Game currentGame = null;
    
        if (!isValidPlaytime(playtimeString)) {
            Notification.show("Invalid play time");
            return false;
        }
        if (!isValidGameName(gameName)) {
            Notification.show("Invalid game name");
            return false;
        }
    
        try {
            currentUser = userService.findUserByUn(userName);
        } catch (Exception e) {
            Notification.show("User is not in the database, try to login again");
            return false;
        }
    
        try {
            currentGame = gamesService.getGameDetailsByName(gameName);
            if (currentUser.getPlayedGames().contains(currentGame)) {
                Notification.show("Game is already in the user's list of played games");
                return false;
            }
            currentGame.setPlayTime(playtime);
        } catch (Exception e) {
            Notification.show("Game is not in the API's database");
            return false;
        }
    
        try {
            currentUser.addGame(currentGame);
            userService.UpdateUser(currentUser);
        } catch (Exception e) {
            Notification.show("Couldn't add game to user, please try again");
            return false;
        }
    
        Notification.show("The game " + currentGame.getName() + " was added to the user " + currentUser.getUn()
                + "'s played games list");
        populateGridWithUserGames(userName);
        return true;
    }
    

    private boolean isValidGameName(String name) {
        if (name == null || name.isEmpty()) {
            Notification.show("please enter a valid game name");
            return false;
        }
        return true;
    }

    private boolean isValidPlaytime(String stringPlaytime) {
        int playtime;
        try {
            playtime = Integer.parseInt(stringPlaytime);
        } catch (Exception e) {
            System.out.println(e);
            Notification.show("please enter a valid playtime");
            return false;
        }
        if (playtime == 0 || playtime < 1) {
            Notification.show("please enter a valid playtime");
            return false;
        }
        return true;

    }

    private void clearComponentes() {
        gameTimeInputField.clear();
        inputTextField.clear();
    }

    private void deleteGame(String game1, String userName) {
        Game game = gamesService.getGameDetailsByName(game1);
        Notification.show(game + "");
        System.out.println("the game recived from the click listener " + game);
        User user = userService.findUserByUn(userName);
        System.out.println("NFSDJKNF WDAJKFNJNSD JK+++++++++++++++FHKJFIDFHSADHFODS");
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setCloseOnEsc(false);
        dialog.setHeader("Delete game: " + game.getName() + "?");
        dialog.setText("Are you sure you want to delete this game?");
        dialog.setCancelable(true);
        dialog.addCancelListener(event -> Notification.show("Delete Cancled!"));
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            user.removeGame(game);
            userService.UpdateUser(user);
            System.out.println("debuggin in gamePage.deleteGame. the game to delete is:" + game
                    + "and the list to delete t from is:" + user.getPlayedGames());
            Notification.show("game by the name " + game.getName() + " DELETED successfuly.");
            populateGridWithUserGames(userName);
        });
        dialog.open();
    }

    private void FactorizeMatrix() throws IOException {
        System.out.println("gamePage.factorizeMatrix: the matrix factorazetion has started");
        // matrixFactorization.getFactorizedMatrix(csvService.getMatrix());
        matrixFactorization.factorizeMatrix();
    }

    public void getGameDetails(String name) {
        Game currentGame = gamesService.getGameDetailsByName(name);
        showGameDetails(currentGame);
    }

    private void showGameDetails(Game currentGame2) {
        if (currentGame2 != null) {
            gameNameTextField.setValue(currentGame2.getName());
            gameRatingTextField.setValue("" + currentGame2.getRating());
            gameReleaseDateTextField.setValue("" + currentGame2.getReleaseDate());
        }
    }

    private boolean populateGridWithUserGames(String userName) {
        User currentUser = null;

        try {
            currentUser = userService.findUserByUn(loggedInUser);
        } catch (Exception e) {
            return false;
        }

        if (currentUser.getPlayedGames() == null) {
            return false;
        } else {
            gameGrid.setItems(currentUser.getPlayedGames());
        }
        return true;
    }
    private boolean populatereccomndationGrid(String loggedInUser2) {
       List<Game> reccmandedGames = new ArrayList<Game>();
       List<Game> finleReccmandedGames = new ArrayList<Game>();
        User currentUser = null;
        try {
            currentUser = userService.findUserByUn(loggedInUser);
        } catch (Exception e) {
            return false;
        }
       reccmandedGames = csvService.getReccomandations(currentUser.getId());
        if (reccmandedGames == null) {
            return false;
        } else {
            for (Game thisGame : reccmandedGames) {
                finleReccmandedGames.add(gamesService.getGameDetailsByName(thisGame.getName()));
            }
           recommendedGameGrid.setItems(finleReccmandedGames);
           Notification.show("reccomandation has updated in the reccomndation tab");
           return true;
        }
    }

}
