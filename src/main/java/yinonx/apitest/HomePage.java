package yinonx.apitest;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import yinonx.apitest.matrixTest.CsvService;
import yinonx.apitest.matrixTest.MatrixFactorization;

@Route(value = "/")
public class HomePage extends VerticalLayout {

    private Game currentGame;
    private GamesService gamesService;
    private MatrixFactorization matrixFactorization;
    private Grid<Game> gameGrid = new Grid<>();
    private TextField inputTextField = new TextField("Enter Game Name");
    

    public HomePage(GamesService gamesService,MatrixFactorization matrixFactorization,CsvService csvService) {
        this.gamesService = gamesService;
        this.matrixFactorization = matrixFactorization;
        // Create a Button to start the search
        Button saveButton = new Button("Search");
        Button factorizeButton = new Button("factorize");
        saveButton.addClickListener(e -> getGameDetails(inputTextField.getValue()));
        factorizeButton.addClickListener(e -> FactorizeMatrix());
        // Add a KeyDownListener to the TextField to trigger the search on Enter key press
        Registration enterListenerRegistration = inputTextField.addKeyDownListener(event -> {
            if ("Enter".equals(event.getKey())) {
                getGameDetails(inputTextField.getValue());
            }
        });

        // Configure the grid
        gameGrid.addColumn(Game::getName).setHeader("Name");
        gameGrid.addColumn(Game::getReleaseDate).setHeader("Release Date");
        gameGrid.addColumn(Game::getRating).setHeader("Rating");
        gameGrid.addColumn(game -> game.getPlatforms() != null ? String.join(", ", game.getPlatforms()) : "")
                .setHeader("Platforms");

        // Add components to the layout
        inputTextField.addKeyPressListener(Key.ENTER, keyPressEvent -> getGameDetails(inputTextField.getValue()));
        add(inputTextField, saveButton, gameGrid,factorizeButton);

        // Clean up the KeyDownListener registration when the component is detached
        addDetachListener(detachEvent -> enterListenerRegistration.remove());
    }

    private void FactorizeMatrix() {
    System.out.println("the matrix factorazetion has started");
 //   matrixFactorization.getFactorizedMatrix(csvService.getMatrix());
        matrixFactorization.testMatrix();
    }

    public void getGameDetails(String name) {
        currentGame = gamesService.getGameDetailsByName(name);
        updateGrid(currentGame);
    }
    
    public void updateGrid(Game game) {
        // Display the current game in the grid, if available
        if (currentGame != null) {
            gameGrid.setItems(game);
        }
    }
}
