/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry5573.chat.utils;

import com.harry5573.chat.SimpleChatPlugin;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author devan_000
 */
public class ConfigurationUtils {

    private static SimpleChatPlugin plugin = SimpleChatPlugin.get();

    public static HashMap<String, String> loadSwearWords() {
        HashMap<String, String> swearWords = new HashMap<>();
        Scanner scanner = new Scanner(plugin.getResource("swearWords.txt"));

        while (scanner.hasNext()) {
            try{
                swearWords.put(scanner.next().toLowerCase(), "Swear");
            } catch(Exception err){
                err.printStackTrace();
            }
        }

        scanner.close();
        return swearWords;
    }

}
