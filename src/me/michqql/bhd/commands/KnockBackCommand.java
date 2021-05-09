package me.michqql.bhd.commands;

import me.michqql.bhd.presets.Preset;
import me.michqql.bhd.presets.PresetHandler;
import me.michqql.bhd.presets.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KnockBackCommand implements CommandExecutor {

    private final PresetHandler presetHandler;

    public KnockBackCommand(PresetHandler presetHandler) {
        this.presetHandler = presetHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!sender.hasPermission("bhd.admin")) {
            sender.sendMessage(ChatColor.DARK_RED + "No permission.");
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "[BetterHitDetection] " + ChatColor.WHITE + "Settings command:");
            sender.sendMessage(ChatColor.AQUA + "/settings cooldown <length> <cancel> <immunity>");
            sender.sendMessage(ChatColor.AQUA + "/settings kb <x> <y>");
            sender.sendMessage(ChatColor.AQUA + "/settings combo <period> <x> <y>");
            sender.sendMessage(ChatColor.AQUA + "/settings pots <x> <y>");
            sender.sendMessage("");
            return true;
        }

        String subCommand = args[0];
        Preset global = presetHandler.getGlobalPreset();
        Settings settings = global.getSettings();

        if(subCommand.equalsIgnoreCase("cooldown")) {
            if(args.length < 4) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.DARK_RED + "/settings cooldown <length> <cancel> <immunity>");
                sender.sendMessage("length - time before player can hit again");
                sender.sendMessage("cancel - should hits while on cooldown be cancelled");
                sender.sendMessage("immunity - time damaged player cannot be hit for");
                sender.sendMessage(ChatColor.GRAY + "Currently:");
                sender.sendMessage(settings.cooldownLength + ", " + settings.cancelOnCooldown + ", " + settings.immunityTicks);
                sender.sendMessage("");
                return true;
            }

            try {
                settings.cooldownLength = Long.parseLong(args[1]);
                settings.cancelOnCooldown = Boolean.parseBoolean(args[3]);
                settings.immunityTicks = Long.parseLong(args[4]);

                sender.sendMessage(ChatColor.GREEN + "Set cooldown to");
                sender.sendMessage(settings.cooldownLength + ", " + settings.cancelOnCooldown + ", " + settings.immunityTicks);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Length must be a number");
            }
        }
        else if(subCommand.equalsIgnoreCase("kb")) {
            if(args.length < 3) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.DARK_RED + "/settings kb <x> <y>");
                sender.sendMessage("x & y - horizontal and vertical multipliers");
                sender.sendMessage(ChatColor.GRAY + "Currently:");
                sender.sendMessage(settings.kbX + ", " + settings.kbY);
                sender.sendMessage("");
                return true;
            }

            try {
                settings.kbX = Double.parseDouble(args[1]);
                settings.kbY = Double.parseDouble(args[2]);

                sender.sendMessage(ChatColor.GREEN + "Set knockback multipliers to");
                sender.sendMessage(settings.kbX + ", " + settings.kbY);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Values must be a number");
            }
        }
        else if(subCommand.equalsIgnoreCase("combo")) {
            if(args.length < 4) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.DARK_RED + "/settings combo <period> <x> <y>");
                sender.sendMessage("period - of time where consecutive hits will count as a combo");
                sender.sendMessage("x & y - horizontal and vertical combo multipliers");
                sender.sendMessage(ChatColor.GRAY + "Currently:");
                sender.sendMessage(settings.comboPeriod + ", " + settings.comboX + ", " + settings.comboY);
                sender.sendMessage("");
                return true;
            }

            try {
                settings.comboPeriod = Long.parseLong(args[1]);
                settings.comboX = Double.parseDouble(args[2]);
                settings.comboY = Double.parseDouble(args[3]);

                sender.sendMessage(ChatColor.GREEN + "Set combo multipliers to");
                sender.sendMessage(settings.comboPeriod + ", " + settings.comboX + ", " + settings.comboY);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Values must be a number");
            }
        }
        else if(subCommand.equalsIgnoreCase("pots")) {
            if(args.length < 3) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.DARK_RED + "/settings pots <x> <y>");
                sender.sendMessage("x & y - horizontal and vertical pot multipliers");
                sender.sendMessage(ChatColor.GRAY + "Currently:");
                sender.sendMessage(settings.potX + ", " + settings.potY);
                sender.sendMessage("");
                return true;
            }

            try {
                settings.potX = Double.parseDouble(args[1]);
                settings.potY = Double.parseDouble(args[2]);

                sender.sendMessage(ChatColor.GREEN + "Set pot multipliers to");
                sender.sendMessage(settings.potX + ", " + settings.potY);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Values must be a number");
            }
        }
        global.save();
        return true;
    }
}
