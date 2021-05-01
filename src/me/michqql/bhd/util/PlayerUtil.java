package me.michqql.bhd.util;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class PlayerUtil {

    private final static double MAX_HIT_RANGE_SURVIVAL = 3.0D;
    private final static double MAX_HIT_RANGE_CREATIVE = 6.0D;

    public static double attribute(Player player, Attribute attribute) {
        AttributeInstance instance = player.getAttribute(attribute);
        return (instance == null) ? 0D : instance.getValue();
    }

    public static boolean failsRangeCheck(Player attacker, Player damaged) {
        double max = attacker.getGameMode() == GameMode.CREATIVE ? MAX_HIT_RANGE_CREATIVE : MAX_HIT_RANGE_SURVIVAL;
        return attacker.getLocation().distance(damaged.getLocation()) > max;
    }
}
