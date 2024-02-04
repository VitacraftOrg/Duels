package dev.siea.duels.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;

public class CuboidRegion {
    private final Vector min;
    private final Vector max;

    public CuboidRegion(Location loc1, Location loc2) {
        this.min = new Vector(
                Math.min(loc1.getX(), loc2.getX()),
                Math.min(loc1.getY(), loc2.getY()),
                Math.min(loc1.getZ(), loc2.getZ())
        );

        this.max = new Vector(
                Math.max(loc1.getX(), loc2.getX()),
                Math.max(loc1.getY(), loc2.getY()),
                Math.max(loc1.getZ(), loc2.getZ())
        );
    }

    public boolean contains(Location loc) {
        Vector vector = loc.toVector();
        return Objects.equals(loc.getWorld(), vector.toLocation(loc.getWorld()).getWorld())
                && vector.isInAABB(min, max);
    }
}

