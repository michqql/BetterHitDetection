package me.michqql.bhd.commands;

import me.michqql.bhd.presets.Preset;
import me.michqql.bhd.presets.PresetHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class PresetCommand implements CommandExecutor {

    private final PresetHandler presetHandler;

    public PresetCommand(PresetHandler presetHandler) {
        this.presetHandler = presetHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("bhd.admin")) {
            sender.sendMessage(ChatColor.DARK_RED + "No permission.");
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[BetterHitDetection] " + ChatColor.WHITE + "Preset Command:");
            sender.sendMessage(ChatColor.YELLOW + "/preset create <name> " + ChatColor.WHITE + "Creates a new preset with default values");
            sender.sendMessage(ChatColor.YELLOW + "/preset save <name> " + ChatColor.WHITE + "Saves the specified preset");
            sender.sendMessage(ChatColor.YELLOW + "/preset load <name> " + ChatColor.WHITE + "Loads preset " + ChatColor.ITALIC + "<name>");
            sender.sendMessage(ChatColor.YELLOW + "/preset list " + ChatColor.WHITE + "Lists all presets");
            sender.sendMessage("");
            return true;
        }

        String subCommand = args[0];

        if(subCommand.equalsIgnoreCase("create")) {
            if(args.length < 2) {
                sender.sendMessage(ChatColor.DARK_RED + "/preset create <name>");
                sender.sendMessage(ChatColor.WHITE + "Creates a new preset with the default settings");
                sender.sendMessage(ChatColor.WHITE + "The preset will be saved under " + ChatColor.ITALIC + "<name>");
                return true;
            }

            String presetName = args[1];
            if(presetHandler.hasPreset(presetName)) {
                sender.sendMessage(ChatColor.DARK_RED + "A preset called " + ChatColor.WHITE + presetName + ChatColor.DARK_RED + " already exists");
                return true;
            }

            Preset preset = new Preset(presetHandler, presetName.toLowerCase());
            preset.save();

            sender.sendMessage(ChatColor.GREEN + "Created preset " + ChatColor.WHITE + preset.getId());
        }
        else if(subCommand.equalsIgnoreCase("save")) {
            Preset preset = presetHandler.getGlobalPreset();
            if(args.length >= 2) {
                String presetName = args[1];

                if(!presetHandler.hasPreset(presetName)) {
                    sender.sendMessage(ChatColor.DARK_RED + "A preset called " + ChatColor.WHITE + presetName + ChatColor.DARK_RED + " doesn't exist");
                    return true;
                }

                preset = presetHandler.getPreset(presetName.toLowerCase());
            }

            preset.save();
            sender.sendMessage(ChatColor.GREEN + "Saved preset " + ChatColor.WHITE + preset.getId());
        }
        else if (subCommand.equalsIgnoreCase("load")) {
            if(args.length < 2) {
                sender.sendMessage(ChatColor.DARK_RED + "/preset load <name>");
                sender.sendMessage(ChatColor.WHITE + "Loads the specified preset");
                return true;
            }

            String presetName = args[1].toLowerCase();
            if(!presetHandler.hasPreset(presetName)) {
                sender.sendMessage(ChatColor.DARK_RED + "A preset called " + ChatColor.WHITE + presetName + ChatColor.DARK_RED + " doesn't exist");
                return true;
            }

            presetHandler.getGlobalPreset().save();
            presetHandler.setGlobalPreset(presetHandler.getPreset(presetName));
            sender.sendMessage(ChatColor.GREEN + "Loaded preset " + ChatColor.WHITE + presetHandler.getGlobalPreset().getId());
        }
        else if(subCommand.equalsIgnoreCase("list")) {
            Collection<Preset> presets = presetHandler.getPresets();

            StringBuilder builder = new StringBuilder();
            for(Preset preset : presets) {
                builder.append(preset.getId()).append(", ");
            }
            sender.sendMessage(ChatColor.GREEN + "Listing all presets:");
            sender.sendMessage(builder.toString());
        }
        return true;
    }
}
