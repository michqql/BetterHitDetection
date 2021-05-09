package me.michqql.bhd.commands;

import me.michqql.bhd.BetterHitDetectionPlugin;
import me.michqql.bhd.damage.DamageCalculatorHandler;
import me.michqql.bhd.gui.MainGUI;
import me.michqql.bhd.presets.PresetHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BetterHitDetectionCommand implements CommandExecutor {

    private final BetterHitDetectionPlugin plugin;
    private final PresetHandler presetHandler;
    private final DamageCalculatorHandler damageCalculatorHandler;

    public BetterHitDetectionCommand(BetterHitDetectionPlugin plugin, PresetHandler presetHandler, DamageCalculatorHandler damageCalculatorHandler) {
        this.plugin = plugin;
        this.presetHandler = presetHandler;
        this.damageCalculatorHandler = damageCalculatorHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("bhd.admin")) {
            sender.sendMessage(ChatColor.DARK_RED + "No permission.");
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "You must be a player to use this command.");
            return true;
        }

        if(!plugin.useInventoryLib()) {
            sender.sendMessage(ChatColor.DARK_RED + "Required dependency is not installed.");
            sender.sendMessage(ChatColor.DARK_RED + "Please install InventoryLib to use GUI's!");
            return true;
        }

        new MainGUI(plugin, (Player) sender, presetHandler, damageCalculatorHandler).openGUI();
        return true;
    }
}
