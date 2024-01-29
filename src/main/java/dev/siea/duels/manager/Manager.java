package dev.siea.duels.manager;

import dev.siea.base.api.messenger.Messenger;
import dev.siea.base.api.messenger.NotificationReason;
import dev.siea.duels.Duels;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Manager {
    private static final List<DuelSession> activeDuels = new ArrayList<>();
    private static final List<DuelRequest> duelRequests = new ArrayList<>();

    private static final HashMap<Player, DuelType> duelQue = new HashMap<>();

    public Manager(){
        for (DuelRequest duelRequest : duelRequests){
            if (duelRequest.isExpired()) duelRequest.expired();
        }

        new BukkitRunnable() {
            public void run() {
            }
        }.runTaskTimer(Duels.getPlugin(), 1L, 1L);
    }

    /*
        These are que related methods handling matchmaking
     */
    public static void joinQue(Player player, DuelType type){
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
                Messenger.sendMessage(player1, "§cYou already have an outgoing Duel request", NotificationReason.SOFT_WARNING);
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

        DuelSession session = new DuelSession(player1,player2,type);
        activeDuels.add(session);
    }

    public static void startDuel(DuelRequest duelRequest){
        removeFromOtherQues(duelRequest.getInitiator(), duelRequest.getRecipient());

        DuelSession session = new DuelSession(duelRequest.getInitiator(),duelRequest.getRecipient(),duelRequest.getType());
        activeDuels.add(session);
    }

    /*
        These methods manage the stop and handling of Duel sessions
    */
    public static void stopDuel(DuelSession duelSession){

    }

    /*
        These methods remove expired duel requests, and similar
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
}
