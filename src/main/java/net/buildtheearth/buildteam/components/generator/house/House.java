package net.buildtheearth.buildteam.components.generator.house;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.utils.ChatUtil;
import net.buildtheearth.utils.Item;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class House {

    public static String WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command";

    public static HashMap<UUID, HouseSettings> playerHouseSettings = new HashMap<>();


    public static void analyzeCommand(Player p, String[] args){

        if(args.length == 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }


        /** Conversion:
         *
         * Command: /gen house -w 123:12 -r 456:78
         * args: ["-w", "123:12", "-r", "456:78"]
         * HouseSettings:
         *  WALL_COLOR: 123:12
         *  ROOF_TYPE:  456:78
         */

        playerHouseSettings.put(p.getUniqueId(), new HouseSettings(p));

        String argsString = " " + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        String[] argsArray = argsString.split(" -");
        String[] flagsArray = Arrays.copyOfRange(argsArray, 1, argsArray.length);


        for(String flagAndValue : flagsArray){
            String[] values = flagAndValue.split(" ");
            String flagName = values[0];
            String flagValue;

            try {
                flagValue = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");
            } catch (ArrayIndexOutOfBoundsException e) {
                p.sendMessage("§cInvalid flag value: -" + flagName + "§n§c ???");
                return;
            }

            HouseFlag houseFlag = HouseFlag.byString(flagName);

            if(houseFlag == null)
                continue;

            playerHouseSettings.get(p.getUniqueId()).setValue(houseFlag, flagValue);
        }

        if(playerHouseSettings.get(p.getUniqueId()).getValues().size() == 0 && args.length > 1){
            sendHelp(p);
            return;
        }

        generate(p);
    }

    public static void sendHelp(Player p){
        //TODO send houses help
        p.sendMessage("TODO send Houses Help");
    }

    public static void sendMoreInfo(Player p){
           p.sendMessage(" ");
           p.sendMessage("§cFor more information take a look at the wiki:");
           p.sendMessage("§c" + WIKI_PAGE);
    }

    public static void sendError(Player p){
        p.sendMessage("§cThere was an error while generating the house. Please contact the admins");
    }

    public static Block[][][] analyzeRegion(Region polyRegion, World world) {
        Block[][][] blocks = new Block[polyRegion.getWidth()][polyRegion.getHeight()][polyRegion.getLength()];

        for (int i = polyRegion.getMinimumPoint().getBlockX(); i <= polyRegion.getMaximumPoint().getBlockX(); i++)
        for (int j = polyRegion.getMinimumPoint().getBlockY(); j <= polyRegion.getMaximumPoint().getBlockY(); j++)
        for (int k = polyRegion.getMinimumPoint().getBlockZ(); k <= polyRegion.getMaximumPoint().getBlockZ(); k++)
            if (polyRegion.contains(new Vector(i, j, k))) {
                Block block = world.getBlockAt(i, j, k);
                blocks[i - polyRegion.getMinimumPoint().getBlockX()][j - polyRegion.getMinimumPoint().getBlockY()][k - polyRegion.getMinimumPoint().getBlockZ()] = block;
            }

        return blocks;
    }

    /**
     * Checks if polygon region contains a block of a certain type
     * @param blocks List of blocks in polygon region
     * @param material Material to check for (e.g. Material.WALL_SIGN)
     * @param data Data value of material to check for (0-15)
     * @return true if polygon region contains a sign, false otherwise
     */
    public static boolean containsBlock(Block[][][] blocks, Material material, byte data){
        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null && block.getType() == material && block.getData() == data)
                        return true;

        return false;
    }

    /**
     * Checks the maximum height of a polygon region
     * @param blocks List of blocks in polygon region
     * @return Maximum height of polygon region
     */
    public static int getMaxHeight(Block[][][] blocks, Material... ignoreMaterials){
        int maxHeight = 0;
        List<Material> ignoreMaterialsList = Arrays.asList(ignoreMaterials);

        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null &&! ignoreMaterialsList.contains(block.getType()) && block.getType().isSolid() && block.getY() > maxHeight)
                        maxHeight = block.getY();

        return maxHeight;
    }

    public static boolean checkPlayer(Player p){
        // Get WorldEdit selection of player
        Region polyRegion = Generator.getWorldEditSelection(p);

        if(polyRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            House.sendMoreInfo(p);
            return false;
        }

        if(playerHouseSettings.get(p.getUniqueId()).getBlocks() == null)
            playerHouseSettings.get(p.getUniqueId()).setBlocks(analyzeRegion(polyRegion, p.getWorld()));

        Block[][][] blocks = playerHouseSettings.get(p.getUniqueId()).getBlocks();

        if(!House.containsBlock(blocks, Material.BRICK, (byte) 0)){
            p.sendMessage("§cPlease make a selection around an outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            House.sendMoreInfo(p);

            return false;
        }

        if(!House.containsBlock(blocks, Material.WOOL, (byte) 4)){
            p.sendMessage("§cPlease place a yellow wool block inside the outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            House.sendMoreInfo(p);

            ItemStack yellowWool = Item.create(Material.WOOL, null, (short) 4, null);
            if(!p.getInventory().contains(yellowWool)) {
                p.getInventory().setItem(4, yellowWool);
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }

            return false;
        }


        return true;
    }

    public static void generate(Player p){
        if(!House.checkPlayer(p))
            return;

        Region polyRegion = Generator.getWorldEditSelection(p);

        HouseScripts.buildscript_v_1_2(p, playerHouseSettings.get(p.getUniqueId()), polyRegion);

        HashMap<HouseFlag, String> flags = playerHouseSettings.get(p.getUniqueId()).getValues();
        String command = "/gen house";
        for(HouseFlag houseFlag : flags.keySet())
            command += " -" + houseFlag.getFlag() + " " + flags.get(houseFlag);


        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");

        TextComponent tc = new TextComponent(BuildTeamTools.PREFIX + "Building §asuccessfully §7generated. §e[Copy Command]");
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to copy command").create()));

        p.spigot().sendMessage(tc);

        p.sendMessage(" ");
        p.sendMessage("§cNote: You can undo the edit with /gen undo.");
    }
}
