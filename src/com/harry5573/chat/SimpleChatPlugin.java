package com.harry5573.chat;

import com.harry5573.chat.command.CommandChat;
import com.harry5573.chat.listener.EventListener;
import com.harry5573.chat.utils.ConfigurationUtils;
import com.harry5573.chat.utils.MessageUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Harry5573
 */
public class SimpleChatPlugin extends JavaPlugin {

    private static SimpleChatPlugin plugin;

    public static SimpleChatPlugin get() {
        return plugin;
    }

    public boolean isChatHalted = false;

    public final HashMap<UUID, String> lastMessage = new HashMap<>();

    public final List<UUID> hasntMoved = new ArrayList<>();
    public final List<UUID> flaggedAdvertisers = new ArrayList<>();

    public final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    public final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?");

    public String prefix = null;
    public String websiteURL = null;
    public boolean blockSwearing = true;
    public boolean blockChatUntilPlayerMoved = true;
    public boolean blockDuplicateMessages = true;
    public boolean banOnIpPost = true;
    public boolean banOnWeblinkPost = true;
    public int maxUppercaseLettersPerWord = 0;

    public ConcurrentHashMap<String, String> swearWords = new ConcurrentHashMap<>();
    
    @Override
    public void onEnable() {
        plugin = this;

        logMessage("Version " + getDescription().getVersion() + " starting up...");

        saveDefaultConfig();

        reload();
        
        swearWords.putAll(ConfigurationUtils.loadSwearWords());
        
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getCommand("chat").setExecutor(new CommandChat());

        logMessage("Plugin started!");
    }

    @Override
    public void onDisable() {
        this.logMessage("Plugin disabled safely!");
    }

    public void logMessage(String msg) {
        getLogger().info(msg);
    }

    public void reload() {
        reloadConfig();

        this.prefix = MessageUtils.translateToColorCode(getConfig().getString("prefix"));
        this.websiteURL = getConfig().getString("websiteURL");
        this.blockSwearing = getConfig().getBoolean("blockSwearing");
        this.blockChatUntilPlayerMoved = getConfig().getBoolean("antiSpam.blockChatUntilPlayerMoved");
        this.blockDuplicateMessages = getConfig().getBoolean("antiSpam.blockDuplicateMessages");
        this.banOnIpPost = getConfig().getBoolean("antiAdvertising.banOnIpPost");
        this.banOnWeblinkPost = getConfig().getBoolean("antiAdvertising.banOnWeblinkPost");
        this.maxUppercaseLettersPerWord = getConfig.getInt("antiAnnoyances.maxUppercaseLettersPerWord");
    }

    public void handleAdvertisingAttempt(final Player p) {
        final UUID uniqueID = p.getUniqueId();

        if (flaggedAdvertisers.contains(uniqueID)) {
            getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + p.getName() + " 1month Autoban for advertising appeal at " + websiteURL + ".");
                    Bukkit.broadcastMessage(prefix + ChatColor.RED + " " + p.getName() + " has been auto-banned for attempting to advertise!");
                }
            }, 1L);
        } else {
            p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
            p.sendMessage(prefix + ChatColor.RED + " Your last message was flagged as possible advertising!");
            p.sendMessage(prefix + ChatColor.RED + " Do not talk again for 1 MINUTE or you will be banned.");
            p.sendMessage(prefix + ChatColor.RED + " We will tell you when you can talk again.");
            p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
            flaggedAdvertisers.add(uniqueID);

            getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (p.isOnline()) {
                        p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                        p.sendMessage(prefix + ChatColor.GREEN + " You can now talk again!");
                        p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                    }
                    plugin.flaggedAdvertisers.remove(uniqueID);
                }
            }, 60 * 20L);
        }
    }
}
