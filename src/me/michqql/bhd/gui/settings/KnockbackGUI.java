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

import java.util.Collections;

public class KnockbackGUI extends GUI {

    private final static int KNOCKBACK_X_DECREASE_ONE = 1;
    private final static int KNOCKBACK_X_DECREASE_SMALL = 2;
    private final static int KNOCKBACK_X_DECREASE_SMALLEST = 3;
    private final static int KNOCKBACK_X = 4;
    private final static int KNOCKBACK_X_INCREASE_SMALLEST = 5;
    private final static int KNOCKBACK_X_INCREASE_SMALL = 6;
    private final static int KNOCKBACK_X_INCREASE_ONE = 7;

    private final static int KNOCKBACK_Y_DECREASE_ONE = 10;
    private final static int KNOCKBACK_Y_DECREASE_SMALL = 11;
    private final static int KNOCKBACK_Y_DECREASE_SMALLEST = 12;
    private final static int KNOCKBACK_Y = 13;
    private final static int KNOCKBACK_Y_INCREASE_SMALLEST = 14;
    private final static int KNOCKBACK_Y_INCREASE_SMALL = 15;
    private final static int KNOCKBACK_Y_INCREASE_ONE = 16;

    private final static int BACK = 22;

    private final Preset preset;
    private final Settings settings;

    public KnockbackGUI(Plugin bukkitPlugin, Player player, Preset preset) {
        super(bukkitPlugin, player);
        this.preset = preset;
        this.settings = preset.getSettings();
        build("&9Knockback", 3);
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

        /* X settings */
        { // X decrease one
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease X by 1");
                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_X_DECREASE_ONE, item);
        }

        { // X decrease small
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease X by 0.1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_X_DECREASE_SMALL, item);
        }

        { // X decrease smallest
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease X by 0.01");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_X_DECREASE_SMALLEST, item);
        }

        { // X increase smallest
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase X by 0.01");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_X_INCREASE_SMALLEST, item);
        }

        { // X increase small
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase X by 0.1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_X_INCREASE_SMALL, item);
        }

        { // X increase one
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase X by 1");
                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_X_INCREASE_ONE, item);
        }

        /* Y settings */
        { // Y decrease one
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease Y by 1");
                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_Y_DECREASE_ONE, item);
        }

        { // Y decrease small
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease Y by 0.1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_Y_DECREASE_SMALL, item);
        }

        { // Y decrease smallest
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Decrease Y by 0.01");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_Y_DECREASE_SMALLEST, item);
        }

        { // Y increase smallest
            ItemStack item = new ItemStack(Material.STONE_BUTTON);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase Y by 0.01");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_Y_INCREASE_SMALLEST, item);
        }

        { // Y increase small
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase Y by 0.1");
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_Y_INCREASE_SMALL, item);
        }

        { // Y increase one
            ItemStack item = new ItemStack(Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Increase Y by 1");
                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_Y_INCREASE_ONE, item);
        }
        updateInventory();
    }

    @Override
    protected void updateInventory() {
        {
            // Knockback Multipliers X
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Knockback X: " + ChatColor.WHITE + format(settings.kbX));
                meta.setLore(Collections.singletonList(
                        ChatColor.GRAY + "Horizontal knockback multiplier"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_X, item);
        }

        {
            // Knockback Multipliers Y
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.AQUA + "Knockback Y: " + ChatColor.WHITE + format(settings.kbY));
                meta.setLore(Collections.singletonList(
                        ChatColor.GRAY + "Vertical knockback multiplier"
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            this.inventory.setItem(KNOCKBACK_Y, item);
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

            case KNOCKBACK_X_DECREASE_ONE:
                settings.kbX -= 1;
                break;

            case KNOCKBACK_X_DECREASE_SMALL:
                settings.kbX -= 0.1;
                break;

            case KNOCKBACK_X_DECREASE_SMALLEST:
                settings.kbX -= 0.01;
                break;

            case KNOCKBACK_X_INCREASE_SMALLEST:
                settings.kbX += 0.01;
                break;

            case KNOCKBACK_X_INCREASE_SMALL:
                settings.kbX += 0.1;
                break;

            case KNOCKBACK_X_INCREASE_ONE:
                settings.kbX += 1;
                break;

            case KNOCKBACK_Y_DECREASE_ONE:
                settings.kbY -= 1;
                break;

            case KNOCKBACK_Y_DECREASE_SMALL:
                settings.kbY -= 0.1;
                break;

            case KNOCKBACK_Y_DECREASE_SMALLEST:
                settings.kbY -= 0.01;
                break;

            case KNOCKBACK_Y_INCREASE_SMALLEST:
                settings.kbY += 0.01;
                break;

            case KNOCKBACK_Y_INCREASE_SMALL:
                settings.kbY += 0.1;
                break;

            case KNOCKBACK_Y_INCREASE_ONE:
                settings.kbY += 1;
                break;
        }

        updateInventory();
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return false;
    }

    private String format(double d) {
        return String.format("%,.2f", d);
    }
}
