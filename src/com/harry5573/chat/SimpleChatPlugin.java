/*Copyright (C) Harry5573 2013-14

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
package com.harry5573.chat;

import com.google.common.collect.Lists;
import com.harry5573.chat.command.CommandChat;
import com.harry5573.chat.listener.EventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Harry5573
 */
public class SimpleChatPlugin extends JavaPlugin {

    private static SimpleChatPlugin plugin;

    public boolean isChatHalted = false;

    public String prefix;

    public final HashMap<UUID, String> lastMessage = new HashMap<>();

    public final List<UUID> hasntMoved = new ArrayList<>();
    public final List<UUID> cantChat = new ArrayList<>();
    public final List<UUID> advertiser = new ArrayList<>();

    private final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    private final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?");

    public static SimpleChatPlugin get() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        log("Version " + this.getDescription().getVersion() + " starting up...");

        saveDefaultConfig();

        loadPrefix();

        registerCommands();
        registerEvents();

        log("Plugin started!");
    }

    @Override
    public void onDisable() {
        this.log("Plugin disabled safely!");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventListener(), this);
    }

    private void registerCommands() {
        getCommand("chat").setExecutor(new CommandChat());
    }

    public void log(String msg) {
        this.getLogger().info(msg);
    }

    public void handleChat(Player p) {
        if (this.isChatHalted != true) {
            Bukkit.broadcastMessage(prefix + ChatColor.RED + " Normal chat halted by " + ChatColor.YELLOW + p.getName());
            this.isChatHalted = true;
        } else {
            Bukkit.broadcastMessage(prefix + ChatColor.GREEN + " Normal chat resumed by " + ChatColor.YELLOW + p.getName());
            this.isChatHalted = false;
        }
    }

    public String translateToColorCode(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void loadPrefix() {
        this.prefix = this.translateToColorCode(this.getConfig().getString("prefix"));
    }

    public void chatCooldown(final Player p) {
        final UUID uniqueID = p.getUniqueId();

        cantChat.add(uniqueID);

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                plugin.cantChat.remove(uniqueID);
            }
        }, getConfig().getInt("delay") * 20);
    }

    public int checkForAdvertising(String message) {
        int advertising = 0;
        if (this.getConfig().getBoolean("banip")) {
            advertising = checkForIPPattern(message);
        }

        if (advertising == 0 && this.getConfig().getBoolean("banweb")) {
            advertising = checkForWebPattern(message);
        }

        return advertising;
    }

    private int checkForIPPattern(String message) {
        int advertising = 0;
        Matcher regexMatcher = ipPattern.matcher(message);

        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                if (ipPattern.matcher(message).find()) {
                    advertising = 2;
                }
            }
        }
        return advertising;
    }

    private int checkForWebPattern(String message) {
        int advertising = 0;
        Matcher regexMatcherurl = webpattern.matcher(message);

        while (regexMatcherurl.find()) {
            String text = regexMatcherurl.group().trim().replaceAll("www.", "").replaceAll("http://", "").replaceAll("https://", "");
            if (regexMatcherurl.group().length() != 0 && text.length() != 0) {
                if (webpattern.matcher(message).find()) {
                    advertising = 2;
                }
            }
        }
        return advertising;
    }

    public void handleAdvertiser(final Player p) {
        final UUID uniqueID = p.getUniqueId();

        if (advertiser.contains(uniqueID)) {
            getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + p.getName() + " 1month Autoban for advertising appeal at " + plugin.getConfig().getString("website"));
                    Bukkit.broadcastMessage(prefix + ChatColor.RED + " " + p.getName() + " Has been auto banned for trying to advertise!");
                }
            }, 1L);
        } else {
            p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
            p.sendMessage(prefix + ChatColor.RED + " Your last message was flagged as possible advertising!");
            p.sendMessage(prefix + ChatColor.RED + " Do not talk again for 1 MINUTE or you will be banned.");
            p.sendMessage(prefix + ChatColor.RED + " We will tell you when you can talk again.");
            p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
            advertiser.add(uniqueID);

            getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (p.isOnline()) {
                        p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                        p.sendMessage(prefix + ChatColor.GREEN + " You can now talk again!");
                        p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                    }
                    plugin.advertiser.remove(uniqueID);
                }
            }, 60 * 20L);
        }
    }
}
