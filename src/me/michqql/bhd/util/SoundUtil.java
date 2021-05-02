package me.michqql.bhd.util;

import me.michqql.bhd.BetterHitDetectionPlugin;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playSound(BetterHitDetectionPlugin plugin, Location location) {
        World world = location.getWorld();
        if(world == null)
            return;

        switch (plugin.getCraftBukkitVersion()) {
            case "v1_16_R3":
            case "v1_16_R2":
            case "v1_16_R1":
            case "v1_15_R1":
            case "v1_14_R1":
            case "v1_12_R1":
                world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                break;

            case "v1_8_R3":
            case "v1_7_R4":
                world.playSound(location, Sound.valueOf("HURT_FLESH"), 1.0f, 1.0f);
        }
    }

    public static void playSound(BetterHitDetectionPlugin plugin, Player damaged) {
        World world = damaged.getWorld();

        switch (plugin.getCraftBukkitVersion()) {
            case "v1_16_R3":
            case "v1_16_R2":
            case "v1_16_R1":
            case "v1_15_R1":
            case "v1_14_R1":
            case "v1_12_R1":
                damaged.playSound(damaged.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                break;

            case "v1_8_R3":
            case "v1_7_R4":
                damaged.playSound(damaged.getLocation(), Sound.valueOf("HURT_FLESH"), 1.0f, 1.0f);
        }
    }
}
