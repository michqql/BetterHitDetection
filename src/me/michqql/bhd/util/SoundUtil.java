package me.michqql.bhd.util;

import me.michqql.bhd.BetterHitDetectionPlugin;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playSound(Player damaged) {
        World world = damaged.getWorld();

        switch (BetterHitDetectionPlugin.getInstance().getCraftBukkitVersion()) {
            case "v1_16_R3":
            case "v1_16_R2":
            case "v1_16_R1":
            case "v1_15_R1":
                world.playSound(damaged.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);

            case "v1_8_R1":
                world.playSound(damaged.getLocation(), Sound.valueOf("HURT_FLESH"), 1.0f, 1.0f);
        }
    }
}
