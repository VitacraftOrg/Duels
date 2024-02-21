package net.vitacraft.game;

import net.vitacraft.api.messenger.Messenger;
import net.vitacraft.api.messenger.NotificationReason;
import net.vitacraft.Duels;
import net.vitacraft.utils.ConfigUtil;
import net.vitacraft.utils.MapConfig;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager implements Listener {
    private static final List<DuelSession> activeDuels = new ArrayList<>();
    private static final List<DuelRequest> duelRequests = new ArrayList<>();
    private static final HashMap<Player, DuelType> duelQue = new HashMap<>();
    private static final HashMap<DuelMap, Boolean> duelMaps = new HashMap<>();

    private static final Location spawn = reloadSpawn();


    public GameManager(){
        reloadDuelMaps();
        new BukkitRunnable() {
            public void run() {
                for (DuelRequest duelRequest : duelRequests){
                    if (duelRequest.isExpired()) duelRequest.expired();
                }
            }
        }.runTaskTimer(Duels.getPlugin(), 1L, 1L);
    }

    /*
        Everything Spawning related
    */

    public static void joinLobby(Player player) {
        player.teleport(spawn);
        player.setArrowsInBody(0);
        player.setSaturation(10);
        player.setHealth(20);
        player.getActivePotionEffects().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
    }


    /*
        These are que related methods handling matchmaking
     */
    public static void joinQue(Player player, DuelType type){
        removeFromOtherQues(player);
        Messenger.send(player, "§eJoining que in : " + type.getDisplayName(), NotificationReason.SOFT_WARNING);
        duelQue.put(player,type);
        matchMake();
    }

    private static void matchMake() {
        Set<Player> matchedPlayers = new HashSet<>();
        Set<DuelType> matchedTypes = new HashSet<>();

        for (Map.Entry<Player, DuelType> entry : duelQue.entrySet()) {
            Player player = entry.getKey();
            DuelType type = entry.getValue();
            if (matchedPlayers.contains(player)) {
                continue;
            }
            if (matchedTypes.contains(type)) {
                continue;
            }
            for (Map.Entry<Player, DuelType> otherEntry : duelQue.entrySet()) {
                if (otherEntry.getKey().equals(player) || matchedPlayers.contains(otherEntry.getKey())) {
                    continue;
                }

                if (otherEntry.getValue().equals(type)) {
                    matchedPlayers.add(player);
                    matchedPlayers.add(otherEntry.getKey());
                    matchedTypes.add(type);
                    startDuel(player, otherEntry.getKey(), type);
                    break;
                }
            }
        }
        for (Player player : matchedPlayers) {
            duelQue.remove(player);
        }
    }

    /*
        These methods handle the manuel requesting of Duels
    */
    public static void requestDuel(Player player1, Player player2, DuelType type){
        for (DuelRequest duelRequest : duelRequests){
            if (duelRequest.getInitiator() == player1){
                Messenger.send(player1, "§cYou already have an outgoing Duel request", NotificationReason.SOFT_WARNING);
                return;
            }
        }
        DuelRequest duelRequest = new DuelRequest(player1, player2, type);
        duelRequests.add(duelRequest);
    }

    /*
        These methods manage the start and handling of Duel sessions
    */
    public static void startDuel(Player player1, Player player2, DuelType type){
        removeFromOtherQues(player1, player2);
        DuelMap map = findMap(type);
        if (map == null){
            Messenger.send(player1, "§cUnable to find suitable map for " + type.getDisplayName() + ".§c Try a different GameMode");
            Messenger.send(player2, "§cUnable to find suitable map for " + type.getDisplayName() + ".§c Try a different GameMode");
            return;
        }
        DuelSession session = new DuelSession(player1,player2,type,map);
        activeDuels.add(session);
    }

    public static void startDuel(DuelRequest duelRequest){
        removeFromOtherQues(duelRequest.getInitiator(), duelRequest.getRecipient());

        DuelSession session = new DuelSession(duelRequest.getInitiator(),duelRequest.getRecipient(),duelRequest.getType(),findMap(duelRequest.getType()));
        activeDuels.add(session);
    }

    /*
        These methods manage the stop and handling of Duel sessions
    */

    public static void stopDuel(DuelSession session) {
        activeDuels.remove(session);
        duelMaps.put(session.getMap(), false);
    }

    private static void purgeDuelSessions(){
        for (DuelSession duelSession : activeDuels){
            duelSession.cancel("§cDuel canceled due to administrative reasons.");
        }
    }

    /*
        These methods manage events during the Duels
    */
    public static void playerDied(Player player){
        DuelSession duelSession = findDuelSession(player);
        if (duelSession == null) return;
        duelSession.playerDied(player);
    }


    /*
        Util Methods
    */
    private static void removeFromOtherQues(Player player){
        for (DuelRequest duelRequest : duelRequests){
            if (duelRequest.getInitiator() == player || duelRequest.getRecipient() == player){
                duelRequest.expired();
            }
        }
        duelQue.remove(player);
    }

    private static void removeFromOtherQues(Player player1, Player player2){
        removeFromOtherQues(player1);
        removeFromOtherQues(player2);
    }

    public static void removeDuelRequest(DuelRequest duelRequest){
        duelRequests.remove(duelRequest);
    }

    private static DuelSession findDuelSession(Player player){
        for (DuelSession duelSession : activeDuels){
            if (duelSession.getPlayers().contains(player)) return duelSession;
        }
        return null;
    }

    private static DuelMap findMap(DuelType type){
        DuelMap foundDuelMap;
        for (DuelMap duelMap : duelMaps.keySet()){
            if (duelMaps.get(duelMap)) continue;
            if (duelMap.getType() == type) {
                foundDuelMap = duelMap;
                duelMaps.put(foundDuelMap, true);
                return foundDuelMap;
            }
        }
        return null;
    }

    public static void reloadDuelMaps(){
        List<DuelMap> duelMapsList = MapConfig.getAllMaps();
        purgeDuelSessions();
        duelMaps.clear();
        for (DuelMap map : duelMapsList){
            duelMaps.put(map, false);
        }
    }

    public static Location reloadSpawn() {
        ConfigUtil config = new ConfigUtil(Duels.getPlugin(),"locations.yml");
        return config.getConfig().getLocation("spawn");
    }

    public static void onDisable(){
        purgeDuelSessions();
    }

    /*
       Listeners
     */
    @EventHandler
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if (!(e.getEntity() instanceof Player)) return;
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains((Player) e.getEntity())) {
                session.onPlayerDamage(e);
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public static void onEntityDamage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player)) return;
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains((Player) e.getEntity())) {
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public static void onEntityRegainHealth(EntityRegainHealthEvent e){
        if (!(e.getEntity() instanceof Player)) return;
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains((Player) e.getEntity())) {
                session.onPlayerRegainHealth(e);
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public static void onBlockBreak(BlockBreakEvent e){
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains(e.getPlayer())) {
                session.blockBroken(e);
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public static void onBlockBreak(BlockPlaceEvent e){
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains(e.getPlayer())) {
                session.blockPlaced(e);
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public static void onFoodLevelChange(FoodLevelChangeEvent e){
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains((Player) e.getEntity())) {
                session.foodLevelChange(e);
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public static void onProjectileLaunchEvent(ProjectileLaunchEvent e){
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains((Player) e.getEntity())) {
                session.projectileFired(e);
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public static void projectileHitEvent(ProjectileHitEvent e){
        if (!(e.getEntity().getName().toLowerCase().contains("arrow"))) return;
        if (e.getHitBlock() != null) e.getEntity().remove();
    }

    @EventHandler
    public static void playerMoved(PlayerMoveEvent e){
        for (DuelSession session : activeDuels){
            if (session.getPlayers().contains(e.getPlayer())) {
                session.playerMoved(e);
                return;
            }
        }
    }

    public static HashMap<DuelMap, Boolean> getMaps() {
        return duelMaps;
    }
}
