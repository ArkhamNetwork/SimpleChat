/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry5573.simplechat.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.harry5573.simplechat.SimpleChatPlugin;

/**
 *
 * @author devan_000
 */
public class ChatUtils {

      private static SimpleChatPlugin plugin = SimpleChatPlugin.getPlugin();

      public static void clearChat(Player whoCleared) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                  for (int i = 0; i < 75; i++) {
                        player.sendMessage(" ");
                  }
                  player.sendMessage(ChatColor.YELLOW + "=============================================================================================================================================================================================");
                  player.sendMessage(ChatColor.GRAY + "The chat has been " + ChatColor.AQUA + ChatColor.UNDERLINE + "cleared" + ChatColor.GRAY + " by " + ChatColor.YELLOW + whoCleared.getName());
                  player.sendMessage(ChatColor.YELLOW + "=============================================================================================================================================================================================");
            }
            whoCleared.sendMessage(plugin.prefix + ChatColor.GREEN + " You have cleared the chat.");
      }

      public static void toggleChat(Player whoToggled) {
            if (!plugin.isChatHalted) {
                  plugin.isChatHalted = true;
                  for (Player player : Bukkit.getOnlinePlayers()) {
                        for (int i = 0; i < 75; i++) {
                              player.sendMessage(" ");
                        }
                        player.sendMessage(ChatColor.YELLOW + "=============================================================================================================================================================================================");
                        player.sendMessage(ChatColor.GRAY + "The chat has been " + ChatColor.RED + ChatColor.UNDERLINE + "disabled" + ChatColor.GRAY + " by " + ChatColor.YELLOW + whoToggled.getName());
                        player.sendMessage(ChatColor.YELLOW + "=============================================================================================================================================================================================");
                  }
                  whoToggled.sendMessage(plugin.prefix + ChatColor.RED + " You have toggled the chat OFF.");
            } else {
                  plugin.isChatHalted = false;
                  for (Player player : Bukkit.getOnlinePlayers()) {
                        for (int i = 0; i < 75; i++) {
                              player.sendMessage(" ");
                        }
                        player.sendMessage(ChatColor.YELLOW + "=============================================================================================================================================================================================");
                        player.sendMessage(ChatColor.GRAY + "The chat has been " + ChatColor.GREEN + ChatColor.UNDERLINE + "enabled" + ChatColor.GRAY + " by " + ChatColor.YELLOW + whoToggled.getName());
                        player.sendMessage(ChatColor.YELLOW + "=============================================================================================================================================================================================");
                  }
                  whoToggled.sendMessage(plugin.prefix + ChatColor.GREEN + " You have toggled the chat ON.");
            }
      }

      public static boolean checkMessageForAdvertising(String message) {
            if (plugin.punishOnIpPost) {
                  if (checkMessageForIP(message) == true) {
                        return true;
                  }
            }

            if (plugin.punishOnWeblinkPost) {
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

      public static boolean checkMessageForWebPattern(String message) {
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
                  if (newMessage.length() > 0) {
                        newMessage.append(" ");
                  }

                  if (word.length() <= 1) {
                        newMessage.append(word);
                        continue;
                  }

                  if (isSwearWord(word.toLowerCase())) {
                        newMessage.append(buildPlaceholders(getRemovedDelimeters(word).length(), "*"));
                  } else {
                        newMessage.append(word);
                  }
            }

            return newMessage.toString();
      }
      
      public static boolean isSwearWord(String word) {
    	  /* BASIC CHECK */
    	  if (plugin.swearWords.get(word.toLowerCase()) != null) return true;
    	  
    	  /* CONTAINS CHECK */
    	  for (String sw : plugin.swearWords.keySet())
    		  if (word.contains(sw)) return true;
    	  
    	  /* DELIMETER REDUCTION CHECK */
    	  String noDelimeterWord = getRemovedDelimeters(word);
    	  for (String sw : plugin.swearWords.keySet())
    		  if (noDelimeterWord.contains(sw)) return true;
    	  
    	  return false;
      }
      
      public static String getRemovedDelimeters(String string) {
    	  List<Character> delimeters = Arrays.asList('.', ',', ';', ':', '-', '_', '|', '/', '?', '(', ')', '!', '@', '#', '4', '5', '6', '7', '8', '9', '0', '`', '~', '+', '<', '>');
    	  String noDelimeterWord = "";
    	  for (char c : string.toCharArray())
    		  if (!delimeters.contains(c)) noDelimeterWord = noDelimeterWord + c;
    	  
    	  return noDelimeterWord;
      }

      public static String getFilteredUppercaseMessage(String message) {
            StringBuilder newMessage = new StringBuilder();

            for (String word : message.split(" ")) {
                  if (newMessage.length() > 0) {
                        newMessage.append(" ");
                  }

                  if (word.length() <= 1) {
                        newMessage.append(word);
                        continue;
                  }

                  if (getUppercaseCount(word) > plugin.maxUppercaseLettersPerWord) {
                        newMessage.append(word.toLowerCase());
                  } else {
                        newMessage.append(word);
                  }
            }
            return newMessage.toString();
      }
}
