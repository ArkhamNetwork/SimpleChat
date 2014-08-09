/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry5573.chat.utils;

import com.harry5573.chat.SimpleChatPlugin;
import java.util.regex.Matcher;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author devan_000
 */
public class ChatUtils {

    static SimpleChatPlugin plugin = SimpleChatPlugin.get();

    public static void clearChat(Player whoCleared) {
        final Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
        for (Player player : onlinePlayers) {
            for (int i = 0; i < 75; i++) {
                player.sendMessage(" ");
            }
            player.sendMessage(ChatColor.RED + "♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪");
            player.sendMessage(ChatColor.GREEN + "The chat has been cleared by " + ChatColor.YELLOW + whoCleared.getName());
            player.sendMessage(ChatColor.RED + "♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪");
        }
        whoCleared.sendMessage(plugin.prefix + ChatColor.GREEN + " You have cleared the chat.");
    }

    public static void toggleChat(Player whoToggled) {
        if (!plugin.isChatHalted) {
            plugin.isChatHalted = true;
            final Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
            for (Player player : onlinePlayers) {
                for (int i = 0; i < 75; i++) {
                    player.sendMessage(" ");
                }
                player.sendMessage(ChatColor.RED + "♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪");
                player.sendMessage(ChatColor.GREEN + "The chat has been disabled by " + ChatColor.YELLOW + whoToggled.getName());
                player.sendMessage(ChatColor.RED + "♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪");
            }
            whoToggled.sendMessage(plugin.prefix + ChatColor.RED + " You have toggled the chat OFF.");
        } else {
            plugin.isChatHalted = false;
            final Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
            for (Player player : onlinePlayers) {
                for (int i = 0; i < 75; i++) {
                    player.sendMessage(" ");
                }
                player.sendMessage(ChatColor.DARK_GREEN + "♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪");
                player.sendMessage(ChatColor.GREEN + "The chat has been enabled by " + ChatColor.YELLOW + whoToggled.getName());
                player.sendMessage(ChatColor.DARK_GREEN + "♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪♫♪♫♪♫♪♫♪♫♪♫♫♪♫♪");
            }
            whoToggled.sendMessage(plugin.prefix + ChatColor.GREEN + " You have toggled the chat ON.");
        }
    }

    public static boolean checkMessageForAdvertising(String message) {
        if (plugin.banOnIpPost) {
            if (checkMessageForIP(message) == true) {
                return true;
            }
        }

        if (plugin.banOnWeblinkPost) {
            if (checkMessageForWebPattern(message) == true) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkMessageForIP(String message) {
        Matcher regexMatcher = plugin.ipPattern.matcher(message);

        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                if (plugin.ipPattern.matcher(message).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkMessageForWebPattern(String message) {
        Matcher regexMatcherurl = plugin.webpattern.matcher(message);

        while (regexMatcherurl.find()) {
            String text = regexMatcherurl.group().trim().replaceAll("www.", "").replaceAll("http://", "").replaceAll("https://", "");
            if (regexMatcherurl.group().length() != 0 && text.length() != 0) {
                if (plugin.webpattern.matcher(message).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String buildPlaceholders(int length, String placeHolderChar) {
        StringBuilder placeHolderBuilder = new StringBuilder();

        for (int i = 1; i <= length; i++) {
            placeHolderBuilder.append(placeHolderChar);
        }

        return placeHolderBuilder.toString();
    }
    private static int getUppercaseCount(String word) {
        int count = 0;
        
        for (int i = 0; i <= (word.length() - 1); i++) {
            if (Character.isUpperCase(word.charAt(i))) {
                count++;
            }
        }
        
        return count;
    }

    public static String getFilteredSwearMessage(String message) {
        StringBuilder newMessage = new StringBuilder();

        for (String word : message.split(" ")) {
            //We do not want to break the first char.
            if (newMessage.length() != 0) {
                newMessage.append(" ");
            }

            if (plugin.swearWords.get(word.toLowerCase()) != null && plugin.swearWords.get(word.toLowerCase()).equals("Swear")) {
                newMessage.append(buildPlaceholders(word.length(), "*"));
            } else {
                newMessage.append(word);
            }
        }

        return newMessage.toString();
    }
    
    public static String getFilteredUppercaseMessage(String message) {
        StringBuilder newMessage = new StringBuilder();
        
        for (String word : message.split(" ")) {
            if (newMessage.length() != 0) {
                newMessage.append(" ");
            }
            
            if (getUppercaseCount(word) > plugin.maxUppercaseLettersPerWord) {
                newMessage.append(word.toLowerCase());
            }
            else {
                newMessage.append(word);
            }
        }
        
        return newMessage.toString();
    }
}
