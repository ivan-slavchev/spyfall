package i2n.spyfall.model;

import java.util.Collection;
import java.util.HashSet;

public class Game {

    private GameStatus status = GameStatus.CREATED;
    private String code;
    private String location;
    private Collection<Player> players = new HashSet<>();
    private Collection<Player> spies = new HashSet<>();
    private Collection<String> locations = new HashSet<>();

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Collection<Player> players) {
        this.players = players;
    }

    public Collection<Player> getSpies() {
        return spies;
    }

    public void setSpies(Collection<Player> spies) {
        this.spies = spies;
    }

    public Collection<String> getLocations() {
        return locations;
    }

    public void setLocations(Collection<String> locations) {
        this.locations = locations;
    }
}
