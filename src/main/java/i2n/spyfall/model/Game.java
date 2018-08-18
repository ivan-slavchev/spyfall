package i2n.spyfall.model;

import java.util.Collection;

public class Game {

    private GameStatus status;
    private String code;
    private String location;
    private Collection<Player> players;
    private Collection<Long> spies;

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

    public Collection<Long> getSpies() {
        return spies;
    }

    public void setSpies(Collection<Long> spies) {
        this.spies = spies;
    }
}
