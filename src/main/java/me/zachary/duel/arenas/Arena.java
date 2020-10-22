package me.zachary.duel.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private Location loc1;
    private Location loc2;
    private List<Player> players;
    private boolean isStarted;

    public Arena(Location loc1, Location loc2) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        restart();
    }

    public Location getFirstLoc() {
        return loc1;
    }

    public Location getSecondLoc() {
        return loc2;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void eliminate(Player victim) {

        players.remove(victim);
        checkWin();
    }

    private void checkWin() {

        if (players.size() == 1) {
            Player winner = players.get(0);
            Bukkit.broadcastMessage(winner.getName() + " Win Duel!");
            restart();
        }

    }

    private void restart() {
        this.players = new ArrayList<>();
        this.isStarted = false;

    }

    public void setStarted() {
        this.isStarted = true;
    }

}
