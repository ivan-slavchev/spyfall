package i2n.spyfall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import i2n.spyfall.model.Game;
import i2n.spyfall.model.Player;

@SpringView(name = GameView.VIEW_NAME)
@SpringComponent(GameView.VIEW_NAME)
@UIScope
public class GameView extends VerticalLayout implements View, Broadcaster.BroadcastListener {

    public final static String VIEW_NAME = "game";

    @Autowired
    private SharedData sharedData;

    @Autowired
    private Navigator navigator;

    private Binder<Integer> binder = new Binder<>();

    private Game game;
    private Integer numberOfSpies = 1;

    private Label playerName = new Label();
    private Label gameCodeLabel = new Label();
    private Label location = new Label();
    private Grid<Player> players = new Grid<>(Player.class);
    private Grid<String> locations = new Grid<>();
    private TextField numberOfSpiesField = new TextField("Number of spies");
    private Button startGameButton = new Button("Start game");

    @PostConstruct
    protected void init() {
        Broadcaster.register(this);

        playerName.setCaption("Player");
        addComponent(playerName);

        gameCodeLabel.setCaption("Game code");
        addComponent(gameCodeLabel);

        location.setCaption("Location");
        addComponent(location);

        players.setCaption("Players");
        players.setWidth(100, Unit.PERCENTAGE);
        players.setSelectionMode(Grid.SelectionMode.MULTI);
        players.setHeightMode(HeightMode.ROW);
        addComponent(players);

        locations.setCaption("Locations");
        locations.setWidth(100, Unit.PERCENTAGE);
        locations.setSelectionMode(Grid.SelectionMode.MULTI);
        locations.addColumn(name -> name)
            .setCaption("Name");
        locations.setHeightMode(HeightMode.ROW);
        addComponent(locations);

        startGameButton.addClickListener(event -> {
            game.setLocation(getRandomLocation());
            game.setSpies(getRandomSpies(numberOfSpies));
            Broadcaster.broadcast(game.getCode());
        });

        binder.forField(numberOfSpiesField)
            .withConverter(new StringToIntegerConverter("Not a number"))
            .withValidator(new IntegerRangeValidator("Not in range", 0, 5))
            .asRequired()
            .bind(source -> numberOfSpies, (bean, fieldValue) -> numberOfSpies = fieldValue);

        binder.setBean(numberOfSpies);

        addComponent(numberOfSpiesField);
        addComponent(startGameButton);
        Button leaveGameButton = new Button("Leave game");
        leaveGameButton.addClickListener(event -> {
            game.getPlayers().removeIf(player -> player.getName()
                .equals(playerName.getValue()));
            navigator.navigateTo(MainView.VIEW_NAME);
        });
        addComponent(leaveGameButton);
    }

    private Collection<Player> getRandomSpies(int numberOfSpies) {

        Collection<Player> spies = new HashSet<>();
        List<Player> playersList = new ArrayList<>(game.getPlayers());

        for (int i = 0; i < numberOfSpies; i++) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(playersList.size());
            spies.add(playersList.get(randomIndex));
            playersList.remove(randomIndex); //to avoid same spy in next iteration
        }

        return spies;
    }

    private String getRandomLocation() {
        Random rand = new Random();

        List<String> locationsList = new ArrayList<>(game.getLocations());

        int randomIndex = rand.nextInt(locationsList.size());
        return locationsList.get(randomIndex);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        String player = (String) VaadinService.getCurrentRequest()
            .getWrappedSession()
            .getAttribute("player");

        if (player == null) {
            navigator.navigateTo(MainView.VIEW_NAME);
        }

        game = sharedData.getGameByCode(event.getParameters());
        playerName.setValue(player);

        if (game == null || player == null) {
            navigator.navigateTo(MainView.VIEW_NAME);
            return;
        }

        game.getPlayers()
            .add(new Player(player));

        gameCodeLabel.setValue(game.getCode());
        players.setDataProvider(DataProvider.ofCollection(game.getPlayers()));
        locations.setDataProvider(DataProvider.ofCollection(game.getLocations()));

        Broadcaster.broadcast(game.getCode());
    }

    @Override
    public void receiveBroadcast(String message) {
        UI.getCurrent()
            .access(() -> {

                if (game == null) {
                    return;
                }

                players.getDataProvider()
                    .refreshAll();
                players.setHeightByRows(game.getPlayers()
                    .size());

                locations.getDataProvider()
                    .refreshAll();
                locations.setHeightByRows(game.getLocations()
                    .size());

                boolean isSpy = game.getSpies()
                    .stream()
                    .anyMatch(player -> player.getName()
                        .equals(playerName.getValue()));
                if (isSpy) {
                    location.setValue("SPY!!!");
                    return;
                }

                location.setValue(game.getLocation());
            });
    }
}