package net.buildtheearth.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

	/** Converts the given Time to a time string
	 *
	 * @param p player for translation
	 * @param time time in Milliseconds
	 * @return
	 */
	public static String toDate(Player p, long time){
		String s = "";
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		if(time > 86400000){					//Tage
			days = (int) (time/86400000);
			time = time - (86400000 * days);
		}

		if(time > 3600000){						//Stunden
			hours = (int) (time/3600000);
			time = time - (3600000  * hours);
		}

		if(time > 60000){						//Minuten
			minutes = (int) (time/60000);
			time = time - (60000 * minutes);
		}

		if(time > 1000){						//Sekunden
			seconds = (int) (time/1000);
			time = time - (1000 * seconds);
		}

		if(days > 0){
			if(days == 1)
				s = s + days + " Day, ";
			else
				s = s + days + " Days, ";
		}
		if(hours > 0 | days > 0){
			if(hours == 1)
				s = s + hours + " Hour";
			else
				s = s + hours + " Hours";
		}
		if((minutes > 0 | hours > 0)& days == 0){
			if(hours > 0)
				s = s + ", ";

			if(minutes == 1)
				s = s + minutes + " Minute";
			else
				s = s + minutes + " Minutes";
		}
		if((seconds > 0 | minutes > 0) &  hours == 0 & days == 0){
			if(minutes > 0)
				s = s + ", ";

			if(seconds == 1)
				s = s + seconds + " Second";
			else
				s = s + seconds + " Seconds";
		}

		return s;
	}

	public static String menuIconTitle(String szText)
	{
		return (ChatColor.GREEN +"" +ChatColor.BOLD +szText);
	}

	public static String loreText(String szText)
	{
		return (ChatColor.GRAY +"" +szText);
	}
}
