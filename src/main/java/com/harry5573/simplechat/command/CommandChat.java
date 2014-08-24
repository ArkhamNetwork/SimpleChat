package com.harry5573.simplechat.command;

import com.harry5573.simplechat.SimpleChatPlugin;
import com.harry5573.simplechat.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Harry5573
 */
public class CommandChat implements CommandExecutor {

      private static SimpleChatPlugin plugin = SimpleChatPlugin.getPlugin();

      @Override
      public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
            if (!(sender instanceof Player)) {
                  plugin.logMessage("That is not a console command.");
                  return true;
            }
            Player player = (Player) sender;

            if (!player.hasPermission("simplechat.admin")) {
                  player.sendMessage(plugin.prefix + ChatColor.RED + " You do not have permission to do that.");
                  return true;
            }

            if (args.length != 1) {
                  player.sendMessage(plugin.prefix + ChatColor.RED + " Usage: /chat <clear|toggle|reload>");
                  return true;
            }

            if (args.length == 1) {
                  if (args[0].equalsIgnoreCase("clear")) {
                        ChatUtils.clearChat(player);
                        return true;
                  }

                  if (args[0].equalsIgnoreCase("toggle")) {
                        ChatUtils.toggleChat(player);
                        return true;
                  }

                  if (args[0].equalsIgnoreCase("reload")) {
                        plugin.reload();
                        player.sendMessage(plugin.prefix + ChatColor.GREEN + " You have reloaded the configuration.");
                        return true;
                  }

                  player.sendMessage(plugin.prefix + ChatColor.RED + " Usage: /simplechat <clear|toggle|reload>");
                  return true;
            }
            return false;
      }
}
