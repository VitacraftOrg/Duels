package dev.siea.duels.manager;

public enum DuelType {
    UHC("§6UHC"),
    BOW("$"),
    SUMO(""),
    COMBO("");

    private final String displayName;
    DuelType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
