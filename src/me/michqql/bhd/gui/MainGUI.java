package me.michqql.bhd.gui;

import me.michqql.bhd.BetterHitDetectionPlugin;
import me.michqql.bhd.damage.DamageCalculatorHandler;
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

import java.util.Arrays;
import java.util.Collections;

public class MainGUI extends GUI {

    private final static int INFO = 0;
    private final static int PRESETS = 2;
    private final static int DAMAGE_CALCULATORS = 3;

    private final String craftBukkitVersion;
    private final PresetHandler presetHandler;
    private final DamageCalculatorHandler damageCalculatorHandler;

    public MainGUI(BetterHitDetectionPlugin plugin, Player player, PresetHandler presetHandler, DamageCalculatorHandler damageCalculatorHandler) {
        super(plugin, player);
        this.craftBukkitVersion = plugin.getCraftBukkitVersion();
        this.presetHandler = presetHandler;
        this.damageCalculatorHandler = damageCalculatorHandler;
        build("&9BetterHitDetection", 1);
    }

    @Override
    protected void createInventory() {
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

        { // Presets
            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
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

        { // Damage Calculators
            ItemStack item = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.BLUE + "Damage Calculators");
                meta.setLore(Collections.singletonList(
                        ChatColor.GRAY + "View all installed damage calculators"
                ));
                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(DAMAGE_CALCULATORS, item);
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
        } else if(slot == DAMAGE_CALCULATORS) {
            new DamageCalculatorsGUI(bukkitPlugin, player, damageCalculatorHandler).openGUI();
        }
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return false;
    }
}
