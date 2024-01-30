package dev.siea.duels;

import dev.siea.base.Base;
import dev.siea.duels.commands.DuelCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Duels extends JavaPlugin {
    private static final Base base = (Base) Bukkit.getServer().getPluginManager().getPlugin("Base");
    private static Plugin plugin;
    @Override
    public void onEnable() {
        plugin = this;
        Objects.requireNonNull(getCommand("duel")).setExecutor(new DuelCommand());
    }

    @Override
    public void onDisable() {

    }

    public static Base getBase(){
        return base;
    }

    public static Plugin getPlugin(){
        return plugin;
    }
}
