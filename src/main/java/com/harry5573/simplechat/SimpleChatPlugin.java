package com.harry5573.simplechat;

import com.harry5573.simplechat.command.CommandChat;
import com.harry5573.simplechat.listener.EventListener;
import com.harry5573.simplechat.utils.ConfigurationUtils;
import com.harry5573.simplechat.utils.MessageUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Harry5573
 */
public class SimpleChatPlugin extends JavaPlugin {
      @Getter
      private static SimpleChatPlugin plugin;

      public boolean isChatHalted = false;

      public final HashMap<UUID, String> lastMessage = new HashMap<>();
      public final HashMap<UUID, Long> lastMessage_time = new HashMap<>();
      
      public final HashSet<UUID> hasntMoved = new HashSet<>();
      public final HashSet<UUID> flaggedAdvertisers = new HashSet<>();

      public final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
      public final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?");

      public String prefix = null;
      public String websiteURL = null;
      public boolean blockSwearing = true;
      public boolean blockChatUntilPlayerMoved = true;
      public boolean blockDuplicateMessages = true;
      public boolean punishOnIpPost = true;
      public boolean punishOnWeblinkPost = true;
      public int maxUppercaseLettersPerWord = 0;
      public boolean removeColors = true;

      public HashMap<String, String> swearWords = new HashMap<>();

      @Override
      public void onEnable() {
            plugin = this;

            logMessage("=[ Plugin version " + getDescription().getVersion() + " starting");

            saveDefaultConfig();
            reload();

            getServer().getPluginManager().registerEvents(new EventListener(), this);
            getCommand("chat").setExecutor(new CommandChat());

            logMessage("=[ Plugin version " + getDescription().getVersion() + " started");
      }

      @Override
      public void onDisable() {
            logMessage("=[ Plugin version " + getDescription().getVersion() + " stopping");

            logMessage("=[ Plugin version " + getDescription().getVersion() + " shutdown");
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
            this.punishOnIpPost = getConfig().getBoolean("antiAdvertising.punishOnIpPost");
            this.punishOnWeblinkPost = getConfig().getBoolean("antiAdvertising.punishOnWeblinkPost");
            this.maxUppercaseLettersPerWord = getConfig().getInt("antiSpam.maxUppercaseLettersPerWord");
            this.removeColors = getConfig().getBoolean("removeColors");

            swearWords.clear();
            swearWords.putAll(ConfigurationUtils.loadSwearWords());
      }

      public void handleAdvertisingAttempt(final Player p) {
            final UUID uniqueID = p.getUniqueId();

            if (flaggedAdvertisers.contains(uniqueID)) {
                  getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempmute " + p.getName() + " 7d Automatically muted for 1 week.");
                              Bukkit.broadcastMessage(prefix + ChatColor.RED + " " + p.getName() + " has been auto-muted for attempting to advertise!");
                        }
                  }, 1L);
            } else {
                  p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                  p.sendMessage(prefix + ChatColor.RED + " Your last message was flagged as possible advertising!");
                  p.sendMessage(prefix + ChatColor.RED + " Do not chat any links for 1 minute or you will be muted.");
                  p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                  flaggedAdvertisers.add(uniqueID);

                  getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                              if (p.isOnline()) {
                                    p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                                    p.sendMessage(prefix + ChatColor.GREEN + " You may now chat safely again!");
                                    p.sendMessage(prefix + ChatColor.DARK_RED + " " + ChatColor.BOLD + "*****************************");
                              }
                              plugin.flaggedAdvertisers.remove(uniqueID);
                        }
                  }, 60 * 20L);
            }
      }
}
