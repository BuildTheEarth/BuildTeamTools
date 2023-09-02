package net.buildtheearth.utils;

import java.util.ArrayList;

public class Liste {
	
	public static ArrayList<String> createStringList(String arg1){
		ArrayList<String> list = new ArrayList<>();
		if(arg1 != null){list.add(arg1);}	
		return list;		
	}
	
	public static ArrayList<String> createList(String... args){
		ArrayList<String> list = new ArrayList<>();
		for(String arg : args)
			if(arg != null) {
				arg = "§7" + arg;
				list.add(arg);
			}
		return list;		
	}
	
}
