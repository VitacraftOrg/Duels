package dev.siea.duels.manager;

public enum DuelType {
    CLASSIC("§eClassic Duel"),
    UHC("§6UHC Duel"),
    KIT("§dKit Duel"),
    BOW("§dBow Duel"),
    SUMO("§bSumo Duel"),
    COMBO("§4Combo Duel");

    private final String displayName;
    DuelType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
