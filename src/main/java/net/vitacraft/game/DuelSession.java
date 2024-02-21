package net.vitacraft.game;

import net.vitacraft.Duels;
import net.vitacraft.api.messenger.MessageType;
import net.vitacraft.api.messenger.Messenger;
import net.vitacraft.api.messenger.NotificationReason;
import net.vitacraft.api.storage.Finances;
import net.vitacraft.utils.CuboidRegion;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class DuelSession {
    private final List<Player> players;
    private final List<Player> alivePlayers = new ArrayList<>();

    private final long duelStartTime = System.currentTimeMillis();

    private long duelEndTime;

    private final CuboidRegion region;
    private final DuelType type;
    private int countdown = 61;
    private final List<Block> playerBlocks = new ArrayList<>();
    private final DuelMap map;
    private GameState gameState = GameState.WAITING;

    private BukkitTask runnable;
    private final HashMap<Player, Integer> hits = new HashMap<>();

    public DuelSession(Player player1, Player player2, DuelType type, DuelMap map){
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        this.players = players;
        this.type = type;
        this.map = map;
        region = new CuboidRegion(map.getVertex(), map.getVertex2());
        alivePlayers.addAll(players);
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
        runnable = new BukkitRunnable() {
            public void run() {
                countdown--;
                if (countdown == 60){
                    for (Player player : alivePlayers){
                        Messenger.send(player,"§a3", NotificationReason.SOFT_WARNING, MessageType.TITLE);
                    }
                }
                else if (countdown == 40){
                    for (Player player : alivePlayers){
                        Messenger.send(player,"§a2",NotificationReason.SOFT_WARNING,MessageType.TITLE);
                    }
                }
                else if (countdown == 20){
                    for (Player player : alivePlayers){
                        Messenger.send(player,"§a1",NotificationReason.SOFT_WARNING,MessageType.TITLE);
                    }
                }
                else if (countdown == 0){
                    runnable.cancel();
                    start();
                }
            }
        }.runTaskTimer(Duels.getPlugin(), 1L, 1L);
    }

    public void start(){
        for (Player player : players){
            Messenger.send(player, "§cStart!", NotificationReason.SOFT_WARNING, MessageType.TITLE);
        }
        gameState = GameState.PLAYING;
    }

    public void cancel(String reason){
        gameState = GameState.STOPPING;
        if (reason != null){
            for (Player player : alivePlayers){
                Messenger.send(player, reason, NotificationReason.SOFT_WARNING);
            }
        }
        kill();
    }

    private void kill(){
        gameState = GameState.STOPPED;
        for (Player player : players){
            try{
                GameManager.joinLobby(player);
            } catch (Exception e){
                player.sendMessage("Couldnt teleport you to the lobby: " + e.getMessage());
            }
        }
        System.out.println("Players: " + players);
        for (Block block : playerBlocks){
            block.setType(Material.AIR);
        }
        GameManager.stopDuel(this);
    }

    private void stop(){
        gameState = GameState.STOPPING;
        duelEndTime = System.currentTimeMillis();
        int playTimeInSecs = (int) ((duelEndTime - duelStartTime) / 1000);
        int reward = playTimeInSecs / 20;
        for (Player player : alivePlayers){
            Messenger.send(player, "§aYou won!", NotificationReason.AWARD, MessageType.TITLE);
            Finances.addCoins(player, reward);
            Messenger.send(player,"§eYou got " + reward + " coins for winning the duel.", NotificationReason.AWARD);
        }

        for (Player player : players){
            player.setArrowsInBody(0);
            player.setHealth(20);
            player.getActivePotionEffects().clear();
            player.getInventory().clear();
            player.setSaturation(20);
        }

        countdown = 101;
        runnable = new BukkitRunnable() {
            public void run() {
                countdown--;
                for (Player player : alivePlayers){
                    playWinningAnimation(player);
                }
                if (countdown == 0){
                    runnable.cancel();
                    kill();
                }
            }
        }.runTaskTimer(Duels.getPlugin(), 1L, 1L);
    }

    private void playWinningAnimation(Player player){

    }

    public void playerDied(Player player){
        if (gameState == GameState.PLAYING){
            alivePlayers.remove(player);
            Messenger.send(player, "§cYou died!", NotificationReason.SOFT_WARNING, MessageType.TITLE);
            for (Player loopplayer: alivePlayers){
                Messenger.send(loopplayer, "§c" + player.getName() + " died.", NotificationReason.SOFT_WARNING, MessageType.CHAT_MESSAGE);
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

    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if (gameState != GameState.PLAYING) {
            e.setCancelled(true);
            return;
        }
        if (type == DuelType.SUMO || type == DuelType.BOXING) e.setDamage(0);
        if (type == DuelType.BOXING) {
            hits.put((Player) e.getDamager(), hits.getOrDefault((Player) e.getDamager(), 0)+1);
            if (hits.get((Player) e.getDamager()) > 99){
                List<Player> deadPlayer = new ArrayList<>(alivePlayers);
                deadPlayer.remove((Player) e.getDamager());
                playerDied(deadPlayer.get(0));
            }
        }
        if ((((Player) e.getEntity()).getHealth() - e.getDamage() <= 0)) {
            playerDied((Player) e.getEntity());
        }
    }

    public void onPlayerRegainHealth(EntityRegainHealthEvent e) {
        if (type == DuelType.UHC && (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || e.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)) e.setCancelled(true);
    }

    public void blockPlaced(BlockPlaceEvent e){
        if (gameState != GameState.PLAYING) e.setCancelled(true);
        playerBlocks.add(e.getBlock());
    }

    public void blockBroken(BlockBreakEvent e){
        if (gameState != GameState.PLAYING) e.setCancelled(true);
        if (playerBlocks.contains(e.getBlock())) return;
        else{
            e.setCancelled(true);
        }
    }

    public void projectileFired(ProjectileLaunchEvent e){
        if (gameState != GameState.PLAYING) e.setCancelled(true);
    }

    public void foodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(false);
    }

    public void playerMoved(PlayerMoveEvent e){
        if (gameState != GameState.PLAYING && gameState != GameState.STOPPING) {
            e.setCancelled(true);
            return;
        }
        if (!region.contains(Objects.requireNonNull(e.getTo()))) playerDied(e.getPlayer());
    }
}
