package net.buildtheearth.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
	public static String getBlockID(ItemStack item){
		String blockID = "" + item.getType().getId();

		if(item.getDurability() != 0)
			blockID += ":" + item.getDurability();

		return blockID;
	}


	public static boolean containsStringFromList(String string, List<String> list) {
		for(String s : list)
		if(string.contains(s))
			return true;
		
		return false;
	}
	
	public static int getHighestY(World world, int x, int z) {
	    int y = 255;
	    while(world.getBlockAt(x, y, z).getType() == Material.AIR || world.getBlockAt(x, y, z).getType() == Material.AIR) {
	    	y--; 
	    	if(y == 0)
	    		return 0;
	    }
	    return y;
	}

	public static int[] range(int start, int stop)
	{
		int[] result = new int[stop-start];

		for(int i=0;i<stop-start;i++)
			result[i] = start+i;

		return result;
	}

	public static Player getRandomPlayer(){
		for(Player player : Bukkit.getOnlinePlayers())
			return player;

		return null;
	}

	public static Object pickRandom(Object[] array){
		return array[(int) (Math.random() * array.length)];
	}
}
