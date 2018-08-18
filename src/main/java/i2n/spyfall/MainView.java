package i2n.spyfall;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import i2n.spyfall.model.Game;
import i2n.spyfall.model.GameStatus;

@SpringView(name = MainView.VIEW_NAME)
@SpringComponent(MainView.VIEW_NAME)
@UIScope
public class MainView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "main";

    @Autowired
    private SharedData sharedData;

    private Grid<Game> gamesGrid;

    @PostConstruct
    protected void init() {
        Button button = new Button("INCREASE COUNTER");
        button.addClickListener(clickEvent -> {

            Game newGame = new Game();
            newGame.setCode(RandomStringUtils.randomAlphanumeric(4).toLowerCase());
            newGame.setStatus(GameStatus.CREATED);
            sharedData.getGames()
                .add(newGame);
            gamesGrid.getDataProvider()
                .refreshAll();
            gamesGrid.recalculateColumnWidths();
        });

        gamesGrid = new Grid<>(Game.class);
        gamesGrid.setDataProvider(DataProvider.ofCollection(sharedData.getGames()));
        addComponent(gamesGrid);
        addComponent(button);
    }
}