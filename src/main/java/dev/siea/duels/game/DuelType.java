package dev.siea.duels.game;

import org.bukkit.Material;

public enum DuelType {
    CLASSIC("§eClassic Duel", Material.FISHING_ROD, "§7The Classic feel of Duels"),
    UHC("§6UHC Duel", Material.GOLDEN_APPLE, "§7No natural regeneration"),
    BOW("§dBow Duel", Material.BOW, "§7Ranged Duel"),
    SUMO("§bSumo Duel", Material.SLIME_BALL, "§7Knock your enemy down"),
    BOXING("§4Boxing Duel", Material.WOODEN_SWORD, "§7Combo your enemy for as long as possible");

    private final String displayName;

    private final String description;
    private final Material icon;
    DuelType(String displayName,Material icon, String description){
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName(){
        return displayName;
    }

    public Material getIcon(){
        return icon;
    }

    public String getDescription() {
        return description;
    }
}
