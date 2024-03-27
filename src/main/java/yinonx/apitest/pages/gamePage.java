package yinonx.apitest.pages;

import com.helger.commons.annotation.PrivateAPI;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import yinonx.apitest.classes.Game;
import yinonx.apitest.classes.User;
import yinonx.apitest.matrixTest.MatrixFactorization;
import yinonx.apitest.services.CsvService;
import yinonx.apitest.services.GamesService;
import yinonx.apitest.services.UserService;

@Route(value = "/game")
public class gamePage extends VerticalLayout {

    private String loggedInUser =(String)UI.getCurrent().getSession().getAttribute("username");
    private Grid<Game> recommendedGameGrid = new Grid<>();
    private UserService userService;
    private GamesService gamesService;
    private MatrixFactorization matrixFactorization;
    private Grid<Game> gameGrid = new Grid<>();
    private TextField inputTextField = new TextField("Enter Game Name");
    private TextField gameTimeInputField = new TextField("play time in hours");
    private TextField gameNameTextField;
    private TextField gameRatingTextField;
    private TextField gameReleaseDateTextField;

    public gamePage(GamesService gamesService, MatrixFactorization matrixFactorization, CsvService csvService,
    UserService userService) {
      
        this.gamesService = gamesService;
        this.matrixFactorization = matrixFactorization;
        this.userService = userService;
        Notification.show("the currnt logged in user is "+ (String)VaadinSession.getCurrent().getAttribute("username"));      
    
        TabSheet gametabSheet = new TabSheet();// create a new tab sheet(the object to put tabs within)
        this.getStyle().setAlignItems(AlignItems.CENTER);// set tab aligment ot be centered

        // create all the bottons nececery
        Button searchBotton = new Button("Search");
        Button addToPlayedGames = new Button("Add game to played list");
     //   Button factorizeButton = new Button("factorize");

        // create all the labels and TextFileds to show info of games that are searchd
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
        // set spacingfor the
        // gameReleaseDateSpan.getStyle().set("white-space", "pre-line");

        // activate the search button
        searchBotton.addClickListener(e -> matrixFactorization.getMatrixFromCsv());
        // factorizeButton.addClickListener(e -> FactorizeMatrix());
        // addToPlayedGames
        //         .addClickListener(e -> AddGameToUser(inputTextField.getValue(), gameTimeInputField.getValue(),
        //                 loggedInUser));
        // // Add a KeyDownListener to the TextField to trigger the search on Enter key press ass well

        // //activate
        // Registration enterListenerRegistration =
        // inputTextField.addKeyDownListener(event -> {
        // if ("Enter".equals(()event.getKey())) {
        // getGameDetails(inputTextField.getValue());
        // }
        // });
        add(gametabSheet);
              // Configure the grid for recommended games
        recommendedGameGrid.addColumn(Game::getName).setHeader("Name");
        recommendedGameGrid.addComponentColumn(game -> {
            Image image = new Image(game.getCoverImageLink(), "Cover Image");
            image.setHeight("50px"); // Set the height as needed
            image.setWidth("50px"); // Set the width as needed
            return image;
        }).setHeader("Cover Image");

        // Configure the grid for played games
        // add all the neccecery colomns
        gameGrid.addColumn(Game::getName).setHeader("Name");
        gameGrid.addColumn(Game::getReleaseDate).setHeader("Release Date");
        gameGrid.addColumn(Game::getRating).setHeader("Rating");
        gameGrid.addColumn(Game::getPlayTimeAsString).setHeader("playTime");
        gameGrid.addColumn(game -> game.getPlatforms() != null ? String.join(", ", game.getPlatforms()) : "")
                .setHeader("Platforms");

        gameGrid.addItemDoubleClickListener(event -> deleteGame(event.getItem().getName(),
                loggedInUser));
        // add double click ec=vent to acctivate deletaion of game

        // Add components to the layout
        // style compunents
        inputTextField.getStyle().setPadding("20px");
        gameTimeInputField.getStyle().setPadding("20px");

        new Div(new Text("here you can add games"));
        Div gameTabDiv = new Div(gameGrid);
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

        

        Div newDiv = new Div(span1, span2, span3);
        Div recoDiv = new Div(recommendedGameGrid);

        Div mainDiv = new Div(yourGamesDiv1, yourGamesDiv2, newDiv);
        gametabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);
        gametabSheet.add("played Games", gameTabDiv);
        gametabSheet.add("reccomended games",recoDiv);
       
        gametabSheet.add("add Games", mainDiv);
        gametabSheet.setSizeFull();

        populateGridWithUserGames(loggedInUser);

    }

    private boolean AddGameToUser(String gameName, String playtimeString, String userName) {
        int playtime = 0;
        User currentuser = null;
        Game currntGame = null;

        if (!isValidPlaytime(playtimeString)) {
            Notification.show("invalid Play time");
            return false;
        }
        if (!isValidGameName(gameName)) {
           
            Notification.show("invalid game name");
            return false;
        }
        
        try {
            currentuser = userService.findUserByUn(userName);
            

        } catch (Exception e) {
            
            Notification.show("User is not in the data base, try to login again");
            return false;
        }
        try {
            currntGame = gamesService.getGameDetailsByName(gameName);
            currntGame.setPlayTime(playtime);
        } catch (Exception e) {
            
            Notification.show("game is not in the APIs dtabase");
            return false;
        }
        try {
            currentuser.addGame(currntGame);
            userService.UpdateUser(currentuser);

        } catch (Exception e) {
            Notification.show("couldent add game to user, please try again");
            return false;
        }
        Notification.show("the game " + currntGame.getName() + " was added to the user " + currentuser.getUn()
                + "played games list");
        populateGridWithUserGames(userName);
        clearComponentes();
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

    private void FactorizeMatrix() {
        System.out.println("gamePage.factorizeMatrix: the matrix factorazetion has started");
        // matrixFactorization.getFactorizedMatrix(csvService.getMatrix());
        matrixFactorization.getMatrixFromCsv();
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

}
