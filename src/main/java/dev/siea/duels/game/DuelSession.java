package dev.siea.duels.game;

import dev.siea.base.api.messenger.MessageType;
import dev.siea.base.api.messenger.Messenger;
import dev.siea.base.api.messenger.NotificationReason;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DuelSession {
    private final List<Player> players;
    private final List<Player> alivePlayers;
    private final DuelType type;

    private final List<Block> playerBlocks = new ArrayList<>();

    private final DuelMap map;

    private GameState gameState = GameState.WAITING;

    public DuelSession(Player player1, Player player2, DuelType type, DuelMap map){
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        this.players = players;
        this.type = type;
        this.map = map;
        alivePlayers = players;
        gameState = GameState.STARTING;
        initializeGame();
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

    public DuelMap getMap(){
        return map;
    }

    private void initializeGame(){
        gameState = GameState.STARTING;

        if (map == null){
            cancel("§cNo Lobby found.");
            return;
        }
        if (players.size() < 2){
            cancel("§cNot enough players.");
            return;
        }

        Player player1 = players.get(0);
        Player player2 = players.get(1);

        player1.teleport(map.getSpawn1());
        player2.teleport(map.getSpawn2());
        HashMap<ItemStack, Integer> items = map.getItems();
        for (Player player : players){
            for (ItemStack item : items.keySet()){
                player.getInventory().setItem(items.get(item), item);
            }
            player.setHealth(20);
            player.setSaturation(20);
            player.setGameMode(GameMode.SURVIVAL);
        }
        start();
    }

    public void start(){
        for (Player player : players){
            Messenger.sendMessage(player, "§cStart!", NotificationReason.SOFT_WARNING, MessageType.CHAT_MESSAGE);
        }
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
        for (Block block : playerBlocks){
            block.setType(Material.AIR);
        }
        GameManager.stopDuel(this);
    }

    private void stop(){
        gameState = GameState.STOPPING;
        for (Player player : alivePlayers){
            Messenger.sendMessage(player, "§aYou won!", NotificationReason.AWARD, MessageType.TITLE);
        }
        kill();
    }

    public void playerDied(Player player){
        if (gameState == GameState.PLAYING){
            alivePlayers.remove(player);
            Messenger.sendMessage(player, "§cYou died!", NotificationReason.SOFT_WARNING, MessageType.TITLE);
            for (Player loopplayer: alivePlayers){
                Messenger.sendMessage(loopplayer, "§c" + player.getName() + " died.", NotificationReason.SOFT_WARNING, MessageType.CHAT_MESSAGE);
            }
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

    public void blockPlaced(BlockPlaceEvent e){
        playerBlocks.add(e.getBlock());
    }

    public void blockBroken(BlockBreakEvent e){
        if (playerBlocks.contains(e.getBlock())) return;
        else{
            e.setCancelled(true);
        }
    }

    public void foodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
