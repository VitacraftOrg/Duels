package dev.siea.duels.manager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DuelSession {
    private final List<Player> players;
    private final DuelType type;

    public DuelSession(Player player1, Player player2, DuelType type){
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        this.players = players;
        this.type = type;
    }

    public List<Player> getPlayers(){
        return players;
    }

    public DuelType getType(){
        return type;
    }
}
