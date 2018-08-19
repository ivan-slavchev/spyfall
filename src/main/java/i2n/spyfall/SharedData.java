package i2n.spyfall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import i2n.spyfall.model.Game;

@Component
public class SharedData {

    private Collection<Game> games = new ArrayList<>();
    private Collection<String> defaultLocations = new HashSet<>(Arrays.asList(
        "Restaurant",
        "Space station",
        "Beach",
        "Broadway Theater",
        "Casino",
        "Circus Tent"
        ));
    private Map<String, UI> userUiMap = new HashMap<>();

    public Collection<Game> getGames() {
        return games;
    }

    public Game getGameByCode(String code) {
        return games.stream()
            .filter(game -> Objects.equals(game.getCode(), code))
            .findAny()
            .orElse(null);
    }

    public Collection<String> getDefaultLocations() {
        return defaultLocations;
    }

    public Map<String, UI> getUserUiMap() {
        return userUiMap;
    }
}
