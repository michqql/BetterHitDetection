package me.michqql.bhd.gui;

import me.michqql.bhd.BetterHitDetectionPlugin;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.inventorylib.GUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class MainGUI extends GUI {

    private final static int PRESETS = 2;
    private final static int INFO = 0;

    private final String craftBukkitVersion;
    private final PresetHandler presetHandler;

    public MainGUI(BetterHitDetectionPlugin plugin, Player player, PresetHandler presetHandler) {
        super(plugin, player);
        this.craftBukkitVersion = plugin.getCraftBukkitVersion();
        this.presetHandler = presetHandler;
        build("&9BetterHitDetection", 1);
    }

    @Override
    protected void createInventory() {
        { // Presets
            ItemStack item = new ItemStack(Material.REDSTONE_TORCH);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.BLUE + "Presets");
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + "View and edit all of the presets",
                        ChatColor.GRAY + "and settings per preset",
                        "",
                        ChatColor.GOLD + "Global Preset: " + presetHandler.getGlobalPreset().getId()
                ));
                item.setItemMeta(meta);
            }
            this.inventory.setItem(PRESETS, item);
        }

        { // Info
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.BLUE + "BetterHitDetection");
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + "Plugin version: " + bukkitPlugin.getDescription().getVersion(),
                        ChatColor.GRAY + "Server version: " + craftBukkitVersion
                ));
                item.setItemMeta(meta);
            }
            this.inventory.setItem(INFO, item);
        }
    }

    @Override
    protected void updateInventory() {

    }

    @Override
    protected void onCloseEvent() {

    }

    @Override
    protected boolean onClickEvent(int slot, ClickType clickType) {
        if(slot == PRESETS) {
            new PresetGUI(bukkitPlugin, player, presetHandler).openGUI();
        }
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return false;
    }
}
