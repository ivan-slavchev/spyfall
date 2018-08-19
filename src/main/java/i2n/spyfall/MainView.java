package i2n.spyfall;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import i2n.spyfall.model.Game;
import i2n.spyfall.model.Player;

@SpringView(name = MainView.VIEW_NAME)
@SpringComponent(MainView.VIEW_NAME)
@UIScope
public class MainView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "main";

    @Autowired
    private SharedData sharedData;

    @Autowired
    private Navigator navigator;

    private Grid<Game> gamesGrid;
    private TextField playerNameTextField;

    @PostConstruct
    protected void init() {

        playerNameTextField = new TextField();
        playerNameTextField.setCaption("Player name");
        playerNameTextField.addValueChangeListener(event -> {
            VaadinService.getCurrentRequest()
                .getWrappedSession()
                .setAttribute("player", event.getValue());
            sharedData.getUserUiMap()
                .put(event.getValue(), UI.getCurrent());
        });

        Button createGameButton = new Button("Create new game");
        createGameButton.addStyleName(ValoTheme.BUTTON_SMALL);
        createGameButton.addClickListener(clickEvent -> {

            if (StringUtils.isEmpty(getPlayerName())) {
                Notification.show("Set player name", Notification.Type.WARNING_MESSAGE);
                return;
            }

            Game newGame = new Game();
            newGame.setCode(RandomStringUtils.randomAlphanumeric(4)
                .toLowerCase());
            newGame.getPlayers()
                .add(new Player(playerNameTextField.getValue()));
            newGame.setLocations(sharedData.getDefaultLocations());

            sharedData.getGames()
                .add(newGame);
            gamesGrid.getDataProvider()
                .refreshAll();
            gamesGrid.recalculateColumnWidths();

            navigateToGame(newGame.getCode());
        });

        gamesGrid = new Grid<>();
        gamesGrid.addColumn(Game::getCode)
            .setExpandRatio(1);
        gamesGrid.setSizeFull();
        gamesGrid.setDataProvider(DataProvider.ofCollection(sharedData.getGames()));

        gamesGrid.addComponentColumn(game -> {
            Button joinButton = new Button("JOIN");
            joinButton.addStyleName(ValoTheme.BUTTON_TINY);
            joinButton.addClickListener(clickEvent -> {
                onJoinGame(game);
            });
            return joinButton;
        });

        addComponent(playerNameTextField);
        addComponent(gamesGrid);
        addComponent(createGameButton);
    }

    private void onJoinGame(Game game) {
        game.getPlayers()
            .add(new Player(playerNameTextField.getValue()));
        navigateToGame(game.getCode());
    }

    private void navigateToGame(String code) {
        navigator.navigateTo(GameView.VIEW_NAME + "/" + code);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String player = getPlayerName();
        if (player != null) {
            playerNameTextField.setValue(player);
        }

        gamesGrid.getDataProvider()
            .refreshAll();
    }

    private String getPlayerName() {
        return (String) VaadinService.getCurrentRequest()
            .getWrappedSession()
            .getAttribute("player");
    }
}