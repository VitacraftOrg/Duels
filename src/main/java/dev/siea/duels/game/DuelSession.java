package dev.siea.duels.game;

import dev.siea.base.api.messenger.MessageType;
import dev.siea.base.api.messenger.Messenger;
import dev.siea.base.api.messenger.NotificationReason;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DuelSession {
    private final List<Player> players;
    private final List<Player> alivePlayers;
    private final DuelType type;

    private GameState gameState = GameState.WAITING;

    public DuelSession(Player player1, Player player2, DuelType type){
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        this.players = players;
        this.type = type;
        alivePlayers = players;
        gameState = GameState.STARTING;
        start();
    }

    public List<Player> getPlayers(){
        return players;
    }

    public DuelType getType(){
        return type;
    }

    public GameState getGameState(){
        return gameState;
    }

    public void playerDied(Player player){
        if (gameState == GameState.PLAYING){
            alivePlayers.remove(player);
            Messenger.sendMessage(player, "§You died!", NotificationReason.SOFT_WARNING, MessageType.TITLE);
            if (alivePlayers.size() <= 1){
                stop();
            }
        }
        else if (gameState == GameState.STARTING){
            alivePlayers.remove(player);
            if (alivePlayers.size() <= 1){
                cancel("§cThe Game was canceled because 1 or more players disconnected!");
            }
        }
    }

    public void start(){





        gameState = GameState.PLAYING;
    }

    public void cancel(String reason){
        gameState = GameState.STOPPING;
        if (reason != null){
            for (Player player : alivePlayers){
                Messenger.sendMessage(player, reason, NotificationReason.SOFT_WARNING);
            }
        }
        kill();
    }

    private void kill(){
        gameState = GameState.STOPPED;
        for (Player player : players){
            try{
                GameManager.joinLobby(player);
            } catch (Exception ignore){
            }
        }
    }

    private void stop(){
        gameState = GameState.STOPPING;
        for (Player player : alivePlayers){
            Messenger.sendMessage(player, "You won!", NotificationReason.AWARD, MessageType.TITLE);
        }
        kill();
    }
}
