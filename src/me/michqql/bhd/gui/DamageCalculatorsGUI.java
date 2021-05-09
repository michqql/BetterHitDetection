package me.michqql.bhd.gui;

import me.michqql.bhd.damage.AbstractDamageCalculator;
import me.michqql.bhd.damage.DamageCalculatorHandler;
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

import java.util.Collection;
import java.util.Collections;

public class DamageCalculatorsGUI extends GUI {

    private final DamageCalculatorHandler handler;

    public DamageCalculatorsGUI(Plugin bukkitPlugin, Player player, DamageCalculatorHandler handler) {
        super(bukkitPlugin, player);
        this.handler = handler;
        build("&9Damage Calculators", 6);
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

        final Collection<AbstractDamageCalculator> calculators = handler.getDamageCalculators();
        int slot = 9;
        for (AbstractDamageCalculator calculator : calculators) {
            if (slot >= inventory.getSize())
                break;

            ItemStack item = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "ID: " + calculator.getId());
                meta.setLore(Collections.singletonList(
                        ChatColor.GRAY + "Author: " + ChatColor.WHITE + calculator.getAuthor()
                ));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            inventory.setItem(slot, item);
            slot++;
        }
    }

    @Override
    protected void onCloseEvent() {

    }

    @Override
    protected boolean onClickEvent(int slot, ClickType clickType) {
        if(slot == 0) {
            InventoryLibPlugin.openPreviousGUI(player.getUniqueId());
        }
        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(int i, ClickType clickType) {
        return true;
    }
}
