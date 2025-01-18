package com.marcpg.libpg.storing;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Adapter for converting {@link Cord} to and from Minecraft {@link Location} objects.
 */
public final class CordMinecraftAdapter {
    /**
     * Converts the {@link Cord} to a {@link Location org.bukkit.Location}.
     * @param cord The Cord to convert.
     * @param world The world of the converted Location.
     * @return The converted Location.
     */
    public static @NotNull Location toLocation(@NotNull Cord cord, @NotNull World world) {
        return new Location(world, cord.x(), cord.y(), cord.z());
    }

    /**
     * Converts the {@link Location org.bukkit.Location} to a simple {@link Cord}.
     * @param location The Location to convert.
     * @return The converted Cord.
     */
    public static @NotNull Cord ofLocation(@NotNull Location location) {
        return new Cord(location.getX(), location.getY(), location.getZ());
    }
}
