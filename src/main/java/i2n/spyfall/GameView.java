package i2n.spyfall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import i2n.spyfall.model.Game;
import i2n.spyfall.model.GameStatus;
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

    private Game game;

    private Label playerName = new Label();
    private Label gameCodeLabel = new Label();
    private Label location = new Label();
    private Grid<Player> players = new Grid<>(Player.class);
    private Grid<String> locations = new Grid<>();
    private Button startGameButton = new Button("Start game");

    @PostConstruct
    protected void init() {
        Broadcaster.register(this);

        gameCodeLabel.setCaption("Game code");
        addComponent(gameCodeLabel);

        location.setCaption("Location");
        addComponent(location);

        players.setCaption("Players");
        players.setWidth(100, Unit.PERCENTAGE);
        players.setSelectionMode(Grid.SelectionMode.NONE);
        addComponent(players);

        locations.setCaption("Locations");
        locations.setWidth(100, Unit.PERCENTAGE);
        locations.setSelectionMode(Grid.SelectionMode.NONE);
        locations.addColumn(name -> name)
            .setCaption("Name");
        addComponent(locations);

        startGameButton.addClickListener(event -> {
            game.setLocation(getRandomLocation());
            game.setSpies(getRandomSpies(1));
            Broadcaster.broadcast(game.getCode());
        });
        addComponent(startGameButton);
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

        playerName.setValue(player);

        String gameCode = event.getParameters();

        game = sharedData.getGameByCode(gameCode);

        if (game == null) {
            navigator.navigateTo(MainView.VIEW_NAME);
            return;
        }

        gameCodeLabel.setValue(gameCode);
        players.setDataProvider(DataProvider.ofCollection(game.getPlayers()));
        locations.setDataProvider(DataProvider.ofCollection(game.getLocations()));

        Broadcaster.broadcast(gameCode);
    }

    @Override
    public void receiveBroadcast(String message) {
        UI.getCurrent()
            .access(() -> {

                if (game == null) {
                    return;
                }

                players.getDataProvider().refreshAll();
                locations.getDataProvider().refreshAll();

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