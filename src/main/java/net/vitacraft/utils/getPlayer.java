package net.vitacraft.utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class getPlayer {

    public static OfflinePlayer fromUsername(String username){
        return getOfflinePlayer(username);
    }

    public static UUID getUUIDFromUsername(String target){
        try {
            Player t = getServer().getPlayerExact(target);
            return t.getUniqueId();
        } catch (NullPointerException e){
            OfflinePlayer offlinet = getOfflinePlayer(target);
            if (offlinet == null){
                return null;
            }
            return offlinet.getUniqueId();
        }
    }

    public static OfflinePlayer getPlayerFromUUID(UUID target){
        return getOfflinePlayer(target);
    }

    public static Player getOnlinePlayerFromUUID(UUID target){
        return Bukkit.getPlayer(target);
    }

    public static OfflinePlayer getOfflinePlayer(Object name) {
        if (name instanceof UUID) return Bukkit.getOfflinePlayer((UUID) name);
        if (name instanceof String) return Bukkit.getOfflinePlayer((String) name);
        return null;
    }

    public static Player onlineFromUsername(String username) {
        return Bukkit.getPlayer(username);
    }
}
