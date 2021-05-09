package me.michqql.bhd.gui;

import me.michqql.bhd.presets.Preset;
import me.michqql.bhd.presets.PresetHandler;
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
import java.util.List;

public class PresetGUI extends GUI {

    private final PresetHandler presetHandler;

    public PresetGUI(Plugin bukkitPlugin, Player player, PresetHandler presetHandler) {
        super(bukkitPlugin, player);
        this.presetHandler = presetHandler;
        build("&9Presets", 6);
    }

    @Override
    protected void createInventory() {
        updateInventory();
    }

    @Override
    protected void updateInventory() {
        {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta meta = back.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "<-- Go back");
                back.setItemMeta(meta);
            }
            this.inventory.setItem(0, back);
        }

        {
            // Global Preset
            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.BLUE + "Global Preset");
                meta.setLore(Collections.singletonList(
                        ChatColor.GRAY + " -> " + presetHandler.getGlobalPreset().getId()
                ));
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            inventory.setItem(4, item);
        }

        final List<Preset> presets = presetHandler.getPresets();
        int slot = 9;

        for (Preset preset : presets) {
            if (slot >= inventory.getSize())
                break;

            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                boolean global = presetHandler.getGlobalPreset().getId().equals(preset.getId());
                meta.setDisplayName(ChatColor.AQUA + (global ? "Global " : "") + "Preset: " + preset.getId());

                if (global) {
                    meta.setLore(Collections.singletonList(
                            ChatColor.YELLOW + "LEFT CLICK " + ChatColor.WHITE + "to edit"
                    ));
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                } else {
                    meta.setLore(Arrays.asList(
                            ChatColor.YELLOW + "LEFT CLICK "  + ChatColor.WHITE + "to edit",
                            ChatColor.YELLOW + "RIGHT CLICK " + ChatColor.WHITE + "to make global"
                    ));
                }

                item.setItemMeta(meta);
            }
            inventory.setItem(slot, item);
            slot++;
        }
    }

    @Override
    protected void onCloseEvent() {}

    @Override
    protected boolean onClickEvent(int slot, ClickType clickType) {
        if(slot == 0) {
            InventoryLibPlugin.openPreviousGUI(player.getUniqueId());
            return true;
        }

        // Clicked on a preset
        if(slot >= 9) {
            final List<Preset> presets = presetHandler.getPresets();
            int index = slot - 9;
            if(index >= presets.size())
                return true;

            Preset preset = presets.get(index);
            if(preset == null)
                return true;

            if(clickType == ClickType.LEFT)
                new SettingsGUI(bukkitPlugin, player, preset).openGUI();
            else {
                presetHandler.setGlobalPreset(preset);
                updateInventory();
            }
        }
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return false;
    }
}
