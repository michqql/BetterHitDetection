package me.michqql.bhd.gui;

import me.michqql.bhd.presets.Preset;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.inventorylib.GUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

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
            // Global Preset
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
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

            ItemStack item = new ItemStack(Material.EMERALD);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                boolean global = presetHandler.getGlobalPreset().getId().equals(preset.getId());

                meta.setDisplayName(ChatColor.AQUA + (global ? "Global " : "") + "Preset: " + preset.getId());
                meta.setLore(Collections.singletonList(
                        ChatColor.GRAY + "Click to edit preset"
                ));

                if (global) {
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
        // Clicked on a preset
        if(slot >= 9) {
            final List<Preset> presets = presetHandler.getPresets();
            int index = slot - 9;
            if(index >= presets.size())
                return true;

            Preset preset = presets.get(index);
            new SettingsGUI(bukkitPlugin, player, preset).openGUI();
        }
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return false;
    }
}
