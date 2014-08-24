/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry5573.simplechat.utils;

import com.harry5573.simplechat.SimpleChatPlugin;
import java.util.HashMap;
import java.util.Scanner;
import lombok.Cleanup;

/**
 *
 * @author devan_000
 */
public class ConfigurationUtils {

      private static SimpleChatPlugin plugin = SimpleChatPlugin.getPlugin();

      public static HashMap<String, String> loadSwearWords() {
            HashMap<String, String> swearWords = new HashMap<>();

            @Cleanup
            Scanner scanner = new Scanner(plugin.getResource("swearWords.txt"));

            while (scanner.hasNext()) {
                  try {
                        swearWords.put(scanner.next().toLowerCase(), "Swear");
                  } catch (Exception err) {
                        err.printStackTrace();
                  }
            }
            return swearWords;
      }
}
