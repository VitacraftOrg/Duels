package dev.siea.duels.game;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DuelMap {
    private final DuelType type;
    private final Inventory items;
    private final Location spawn1;
    private final Location spawn2;
    private final Location vertex;
    private final Location vertex2;
    private final Location corner2;

    public DuelMap(DuelType type, Inventory items, Location spawn1, Location spawn2, Location vertex, Location vertex2, Location corner2) {
        this.type = type;
        this.items = items;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.vertex = vertex;
        this.vertex2 = vertex2;
        this.corner2 = corner2;
    }

    public DuelType getType() {
        return type;
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

    public Location getCorner2() {
        return corner2;
    }

    public Inventory getItems() {
        return items;
    }
}
