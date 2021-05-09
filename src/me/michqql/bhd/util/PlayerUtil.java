package me.michqql.bhd.util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

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

    public static void damageItem(Player player, EquipmentSlot slot) {
        if(player.getEquipment() == null)
            return;

        damageItem(player.getEquipment().getItem(slot));
    }

    public static void damageItem(ItemStack item) {
        if(item == null)
            return;

        ItemMeta meta = item.getItemMeta();
        if(meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(damageable.getDamage() - 1);
            item.setItemMeta((ItemMeta) damageable);
        }
    }
}
