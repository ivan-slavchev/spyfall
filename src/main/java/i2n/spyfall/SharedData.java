package i2n.spyfall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import i2n.spyfall.model.Game;

@Component
public class SharedData {

    private Collection<Game> games = new ArrayList<>();

    private Map<String, Long> sharedDataMap = new HashMap<>();

    public Map<String, Long> getSharedDataMap() {
        return sharedDataMap;
    }

    public Collection<Game> getGames() {
        return games;
    }
}
