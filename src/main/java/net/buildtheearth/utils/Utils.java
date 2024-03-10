package net.buildtheearth.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.network.NetworkModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static String[] splitStringByLineLength(String input, int maxLineLength, String separator) {
        String[] parts = input.split(separator);
        StringBuilder currentLine = new StringBuilder();
        ArrayList<String> lines = new ArrayList<>();

        int i = 0;
        for (String part : parts) {
            if (currentLine.length() + part.length() > maxLineLength) {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }

            currentLine.append(part);

            if (i != parts.length - 1)
                currentLine.append(", ");

            i++;
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines.toArray(new String[0]);
    }

    public static void sendPlayerToServer(Player player, String server) {
        if(NetworkModule.getInstance().getBuildTeam() == null
        || !NetworkModule.getInstance().getBuildTeam().isConnected())
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(BuildTeamTools.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static Object pickRandom(Object[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    /**
     * Converts the given Time to a time string
     *
     * @param p    player for translation
     * @param time time in Milliseconds
     * @return
     */
    public static String toDate(Player p, long time) {
        String s = "";
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (time > 86400000) {                    //Tage
            days = (int) (time / 86400000);
            time = time - (86400000 * days);
        }

        if (time > 3600000) {                        //Stunden
            hours = (int) (time / 3600000);
            time = time - (3600000 * hours);
        }

        if (time > 60000) {                        //Minuten
            minutes = (int) (time / 60000);
            time = time - (60000 * minutes);
        }

        if (time > 1000) {                        //Sekunden
            seconds = (int) (time / 1000);
            time = time - (1000 * seconds);
        }

        if (days > 0) {
            if (days == 1)
                s = s + days + " Day, ";
            else
                s = s + days + " Days, ";
        }
        if (hours > 0 | days > 0) {
            if (hours == 1)
                s = s + hours + " Hour";
            else
                s = s + hours + " Hours";
        }
        if ((minutes > 0 | hours > 0) & days == 0) {
            if (hours > 0)
                s = s + ", ";

            if (minutes == 1)
                s = s + minutes + " Minute";
            else
                s = s + minutes + " Minutes";
        }
        if ((seconds > 0 | minutes > 0) & hours == 0 & days == 0) {
            if (minutes > 0)
                s = s + ", ";

            if (seconds == 1)
                s = s + seconds + " Second";
            else
                s = s + seconds + " Seconds";
        }

        return s;
    }

    /**
     * Returns a list of suggestions for the tab completer.
     * If the player is already typing the argument, only the suggestions that start with the argument will be returned.
     *
     * @param args         the args[] from the onTabComplete method
     * @param parentArg    the parent argument
     * @param argPos       the position of the argument
     * @param suggestions  the suggestions as a list
     * @return a list of suggestions for the tab completer filtered by the player's input
     */
    public static List<String> getTabCompleterArgs(String[] args, String parentArg, int argPos, List<String> suggestions){
        if(args.length == argPos && args[0].equalsIgnoreCase(parentArg))
            return suggestions.stream().filter(s -> s.toLowerCase().startsWith(args[argPos - 1].toLowerCase())).collect(Collectors.toList());
        return null;
    }
}
