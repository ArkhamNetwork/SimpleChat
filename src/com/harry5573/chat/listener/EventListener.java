package com.harry5573.chat.listener;

import com.harry5573.chat.SimpleChatPlugin;
import com.harry5573.chat.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Harry5573
 */
public class EventListener implements Listener {

    static SimpleChatPlugin plugin = SimpleChatPlugin.get();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("simplechat.admin")) {
            return;
        }

        if (plugin.isChatHalted) {
            player.sendMessage(plugin.prefix + ChatColor.GRAY + " Chat is currently halted.");
            event.setCancelled(true);
            return;
        }

        if (plugin.blockChatUntilPlayerMoved && plugin.hasntMoved.contains(player.getUniqueId())) {
            player.sendMessage(plugin.prefix + ChatColor.RED + " You cannot chat until you have moved!");
            event.setCancelled(true);
            return;
        }

        if (plugin.blockDuplicateMessages) {
            if (plugin.lastMessage.containsKey(player.getUniqueId())) {
                final String oldmsg = plugin.lastMessage.get(player.getUniqueId());

                plugin.lastMessage.remove(player.getUniqueId());
                if (event.getMessage().contains(oldmsg)) {
                    player.sendMessage(plugin.prefix + ChatColor.RED + " Please do not send duplicate messages!");
                    event.setCancelled(true);
                    plugin.lastMessage.put(player.getUniqueId(), event.getMessage());
                    return;
                }
            }
            plugin.lastMessage.put(player.getUniqueId(), event.getMessage());
        }

        if (ChatUtils.checkMessageForAdvertising(event.getMessage())) {
            plugin.handleAdvertisingAttempt(player);
            event.setCancelled(true);
            return;
        }

        // GOODBYE SWEARING
        if (plugin.blockSwearing) {
            event.setMessage(ChatUtils.getFilteredSwearMessage(event.getMessage()));
        }
        
        // NOT TOO MANY CAPITALS
        event.setMessage(ChatUtils.getFilteredUppercaseMessage(event.getMessage()));

        // NICE LOOKING CHAT
        event.setMessage(capitalizeFirstLetter(event.getMessage()));
        
        // COLOR?
        if (plugin.removeColorChat) {
            event.setMessage(event.getMessage().replace("&", ""));
        }
    }

    public String capitalizeFirstLetter(String original) {
        if (original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        plugin.lastMessage.remove(event.getPlayer().getUniqueId());
        plugin.hasntMoved.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        plugin.lastMessage.remove(event.getPlayer().getUniqueId());
        plugin.hasntMoved.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.blockChatUntilPlayerMoved) {
            plugin.hasntMoved.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.blockChatUntilPlayerMoved) {
            return;
        }
        
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        plugin.hasntMoved.remove(event.getPlayer().getUniqueId());
    }
}
