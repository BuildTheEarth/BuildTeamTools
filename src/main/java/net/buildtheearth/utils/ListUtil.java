package net.buildtheearth.utils;

import java.util.ArrayList;

public class ListUtil {

    public static ArrayList<String> createList(String... args) {
        ArrayList<String> list = new ArrayList<String>();
        for (String arg : args)
            if (arg != null) {
                arg = "§7" + arg;
                list.add(arg);
            }
        return list;
    }
}
