package me.michqql.bhd.gui;

import me.michqql.bhd.gui.settings.ComboGUI;
import me.michqql.bhd.gui.settings.CooldownGUI;
import me.michqql.bhd.gui.settings.KnockbackGUI;
import me.michqql.bhd.presets.Preset;
import me.michqql.inventorylib.GUI;
import me.michqql.inventorylib.InventoryLibPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class SettingsGUI extends GUI {

    private final static int COOLDOWN = 3;
    private final static int KNOCKBACK = 4;
    private final static int COMBO = 5;

    private final static int BACK = 13;

    private final Preset preset;

    public SettingsGUI(Plugin bukkitPlugin, Player player, Preset preset) {
        super(bukkitPlugin, player);
        this.preset = preset;
        build("&9Preset: " + preset.getId(), 2);
    }

    @Override
    protected void createInventory() {
        {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta meta = back.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "<-- Go back");
                back.setItemMeta(meta);
            }
            this.inventory.setItem(BACK, back);
        }

        {
            // Cooldown
            ItemStack item = new ItemStack(Material.REPEATER);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Cooldown Settings");
                meta.setLore(Collections.singletonList(
                        ChatColor.YELLOW + "CLICK " + ChatColor.GRAY + "to edit"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(COOLDOWN, item);
        }

        {
            // Knockback
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Knockback Settings");
                meta.setLore(Collections.singletonList(
                        ChatColor.YELLOW + "CLICK " + ChatColor.GRAY + "to edit"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK, item);
        }

        {
            // Combo
            ItemStack item = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Combo Settings");
                meta.setLore(Collections.singletonList(
                        ChatColor.YELLOW + "CLICK " + ChatColor.GRAY + "to edit"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(COMBO, item);
        }
    }

    @Override
    protected void updateInventory() {}

    @Override
    protected void onCloseEvent() {}

    @Override
    protected boolean onClickEvent(int slot, ClickType clickType) {
        switch (slot) {
            case BACK:
                InventoryLibPlugin.openPreviousGUI(player.getUniqueId());
                return true;

            case COOLDOWN:
                new CooldownGUI(bukkitPlugin, player, preset).openGUI();
                break;

            case KNOCKBACK:
                new KnockbackGUI(bukkitPlugin, player, preset).openGUI();
                break;

            case COMBO:
                new ComboGUI(bukkitPlugin, player, preset).openGUI();
                break;
        }
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return false;
    }
}
