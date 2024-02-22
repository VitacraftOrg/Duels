package net.vitacraft.game;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DuelMap {
    private final DuelType type;
    private final HashMap<Integer, ItemStack> items;

    private final Location center;
    private final Location spawn1;
    private final Location spawn2;
    private final Location vertex;
    private final Location vertex2;

    public DuelMap(DuelType type, HashMap<Integer, ItemStack> items, Location center, Location spawn1, Location spawn2, Location vertex, Location vertex2) {
        this.type = type;
        this.items = items;
        this.center = center;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.vertex = vertex;
        this.vertex2 = vertex2;
    }

    public DuelType getType() {
        return type;
    }

    public Location getCenter(){
        return center;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public Location getVertex() {
        return vertex;
    }

    public Location getVertex2() {
        return vertex2;
    }

    public HashMap<Integer, ItemStack> getItems(){
        return items;
    }
}
