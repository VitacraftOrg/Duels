package net.vitacraft;

import net.vitacraft.api.VitacraftAPI;
import net.vitacraft.api.messenger.Messenger;
import net.vitacraft.commands.DuelCommand;
import net.vitacraft.creator.CreatorManager;
import net.vitacraft.game.GameManager;
import net.vitacraft.gui.GUIListener;
import net.vitacraft.listeners.PlayerConnectionListeners;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Duels extends JavaPlugin {
    private static Plugin plugin;
    private static VitacraftAPI vitacraftapi;

    @Override
    public void onEnable() {
        plugin = this;
        Plugin vitacraftplugin = getServer().getPluginManager().getPlugin("VitacraftAPI");
        if (vitacraftplugin != null) {
            Messenger.log("Hooked into the VitacraftAPI, " + plugin.getDescription().getVersion() + "!");
            vitacraftapi = (VitacraftAPI) vitacraftplugin;
        } else {
            getLogger().severe(String.format("[%s] - Disabled due to invalid VitacraftAPI connection!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Objects.requireNonNull(getCommand("duel")).setExecutor(new DuelCommand());
        getServer().getPluginManager().registerEvents(new PlayerConnectionListeners(), this);
        getServer().getPluginManager().registerEvents(new GameManager(), this);
        getServer().getPluginManager().registerEvents(new CreatorManager(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
    }

    @Override
    public void onDisable() {
        GameManager.onDisable();
    }

    public static Plugin getPlugin(){
        return plugin;
    }
}
