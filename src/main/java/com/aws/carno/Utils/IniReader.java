/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 读取ini文件
 */
public class IniReader {

    private static Map<String, HashMap<String, String>> sectionsMap = new HashMap<String, HashMap<String, String>>();
    private static HashMap<String, String> itemsMap = new HashMap<String, String>();
    private static String currentSection = "";

    /**
     * Load data from target file
     *
     * @param file target file. It should be in ini format
     */
    private static void loadData(File file) {
        BufferedReader reader = null;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if ("".equals(line)) {
                    continue;
                }
                if (line.startsWith("[") && line.endsWith("]")) {
                    // Ends last section  
                    if (itemsMap.size() > 0 && !"".equals(currentSection.trim())) {
                        sectionsMap.put(currentSection, itemsMap);
                    }
                    currentSection = "";
                    itemsMap = null;

                    // Start new section initial  
                    currentSection = line.substring(1, line.length() - 1);
                    itemsMap = new HashMap<String, String>();
                } else {
                    int index = line.indexOf("=");
                    if (index != -1) {
                        String key = line.substring(0, index).trim();
                        String value = line.substring(index + 1, line.length()).trim();
                        itemsMap.put(key, value);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String getValue(String section, String item, File file) {
        loadData(file);

        HashMap<String, String> map = sectionsMap.get(section);
        if (map == null) {
            return "No such section:" + section;
        }
        String value = map.get(item);
        if (value == null) {
            return "No such item:" + item;
        }
        return value;
    }

    public static String getValue(String section, String item, String fileName) {
        File file = new File(fileName);
        return getValue(section, item, file);
    }

    public static List<String> getSectionNames(File file) {
        List<String> list = new ArrayList<String>();
        loadData(file);
        Set<String> key = sectionsMap.keySet();
        for (Iterator<String> it = key.iterator(); it.hasNext();) {
            list.add(it.next());
        }
        return list;
    }

    public static Map<String, String> getItemsBySectionName(String section, File file) {
        loadData(file);
        return sectionsMap.get(section);
    }
}