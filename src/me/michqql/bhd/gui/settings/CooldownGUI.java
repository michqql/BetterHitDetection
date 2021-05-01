package me.michqql.bhd.gui.settings;

import me.michqql.bhd.presets.Preset;
import me.michqql.bhd.presets.Settings;
import me.michqql.inventorylib.GUI;
import me.michqql.inventorylib.InventoryLibPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;

public class CooldownGUI extends GUI {

    private final static int COOLDOWN_DECREASE = 3;
    private final static int COOLDOWN = 4;
    private final static int COOLDOWN_INCREASE = 5;

    private final static int CANCEL_ATTACKS = 13;

    private final static int IMMUNITY_DECREASE = 21;
    private final static int IMMUNITY = 22;
    private final static int IMMUNITY_INCREASE = 23;

    private final static int BACK = 31;

    private final Preset preset;
    private final Settings settings;

    public CooldownGUI(Plugin bukkitPlugin, Player player, Preset preset) {
        super(bukkitPlugin, player);
        this.preset = preset;
        this.settings = preset.getSettings();
        build("&9Combo", 4);
    }

    @Override
    protected void createInventory() {
        /* BACK */
        {
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "<-- Go back");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(BACK, item);
        }

        /* Cooldown settings */
        { // Cooldown decrease
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease cooldown by 1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(COOLDOWN_DECREASE, item);
        }

        { // Cooldown increase
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase cooldown by 1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(COOLDOWN_INCREASE, item);
        }

        /* Immunity settings */
        { // Immunity decrease
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease immunity by 1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(IMMUNITY_DECREASE, item);
        }

        { // Immunity increase
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase immunity by 1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(IMMUNITY_INCREASE, item);
        }

        updateInventory();
    }

    @Override
    protected void updateInventory() {
        {
            // Cooldown
            ItemStack item = new ItemStack(Material.REPEATER);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Cooldown Length: " + ChatColor.WHITE + settings.cooldownLength + " ticks");
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + "The length of time the player",
                        ChatColor.GRAY + "cannot attack for (in ticks)"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(COOLDOWN, item);
        }

        {
            // Cancel
            boolean cancel = settings.cancelOnCooldown;

            ItemStack item = new ItemStack(cancel ? Material.LIME_DYE : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Cancel On Cooldown: " + ChatColor.WHITE + settings.cancelOnCooldown);
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + "Should attacks while on",
                        ChatColor.GRAY + "cooldown be cancelled",
                        "",
                        ChatColor.YELLOW + "LEFT CLICK " + ChatColor.GRAY + "to toggle"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(CANCEL_ATTACKS, item);
        }
        
        {
            // Combo Period
            ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Immunity Ticks: " + ChatColor.WHITE + settings.immunityTicks + " ticks");
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + "Length of time the damaged",
                        ChatColor.GRAY + "player cannot be hit for"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(IMMUNITY, item);
        }
    }

    @Override
    protected void onCloseEvent() {
        preset.save();
    }

    @Override
    protected boolean onClickEvent(int slot, ClickType clickType) {
        if(clickType != ClickType.LEFT)
            return true;

        switch (slot) {
            case BACK:
                InventoryLibPlugin.openPreviousGUI(player.getUniqueId());
                break;

            case COOLDOWN_DECREASE:
                settings.cooldownLength -= 1;
                break;

            case COOLDOWN_INCREASE:
                settings.cooldownLength += 1;
                break;

            case CANCEL_ATTACKS:
                settings.cancelOnCooldown = !settings.cancelOnCooldown;
                break;

            case IMMUNITY_DECREASE:
                settings.immunityTicks -= 1;
                break;

            case IMMUNITY_INCREASE:
                settings.immunityTicks += 1;
                break;
        }

        updateInventory();
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return false;
    }
}
